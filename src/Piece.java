import java.util.ArrayList;

//TODO: Implement En Passant


public class Piece {
	
	final static int PAWN=5;
	final static int KNIGHT=3;
	final static int BISHOP=4;
	final static int ROOK=2;
	final static int QUEEN=1;
	final static int KING=0;
	final static boolean WHITE=true;
	final static boolean BLACK=false;
	
	
	boolean moved=false;
	boolean alive=true;
	int pos;
	int pieceID;
	boolean color;
	Board board;
	
	public int hashCode(){
		return (((Integer)pos).hashCode() * 7919) ^ (((Integer)pieceID).hashCode()* 41) ^ (((Boolean)color).hashCode() * 71) ^ (((Boolean)moved).hashCode() * 73);
	}
	Piece(Board board, int pieceID, boolean color, int initialPosition){
		this.board=board;
		this.pieceID=pieceID;
		this.color=color;
		this.pos=initialPosition;
		
	}
	
	public String toString(){
		String ret = "Piece: {pos="+Integer.toString(pos)+"}";
		return ret;
	}
	
	public Piece clone(Board b){
		Piece ret = new Piece(b, pieceID, color, pos);
		ret.moved=moved;
		ret.alive=alive;
		
		return ret;
	}
	
	ArrayList<Move> getPossibleLocs(){
		ArrayList<Move> ret = new ArrayList<Move>();
		if(pieceID == Piece.PAWN){
			int mult=(color==Piece.WHITE) ? 1 : -1;  //Pawns go up if white, down if black
			if(board.pieceAt(pos+mult*8) == null && board.pieceAt(pos+mult*16) == null && pos+mult*16 >= 0 && pos+mult*16 < 64 & !moved){ //2 space move forward
				ret.add(new Move(this, (pos+mult*16)));
			}
			if(board.pieceAt(pos+mult*8) == null && pos+mult*8 >= 0 && pos+mult*8 < 64){ //1 space move forward
				ret.add(new Move(this, (pos+mult*8)));
			}
			if(!((color == Piece.WHITE && pos % 8 == 0) || (color == Piece.BLACK && pos % 8 == 7)) && board.pieceAt(pos+mult*7)!=null && board.pieceAt(pos+mult*7).color != color){ //taking to one side
				ret.add(new Move(this, (pos+mult*7), board.lastPieceChecked));
			}
			if(!((color == Piece.WHITE && pos % 8 == 7) || (color == Piece.BLACK && pos % 8 == 0)) && board.pieceAt(pos+mult*9)!=null && board.pieceAt(pos+mult*9).color != color){ //taking to the other
				ret.add(new Move(this, (pos+mult*9), board.lastPieceChecked));
			}
		}else if(pieceID == Piece.KNIGHT){
			int[] possibles = {15, 17, -15, -17, 6, 10, -6, -10};
			for (int i : possibles){
				int temp = pos+i;
				int diffX = Math.abs(board.getCol(pos) - board.getCol(temp));
				int diffY = Math.abs(board.getRow(pos) - board.getRow(temp));
				if(diffX!=0 && diffY!=0 && diffX+diffY == 3 && temp >= 0 && temp< 64 && (board.pieceAt(temp) == null || board.pieceAt(temp).color != color)){
					if(board.lastPieceChecked.pos == temp){
						ret.add(new Move(this, temp, board.lastPieceChecked));
					}else{
						ret.add(new Move(this, temp));
					}
				}
			}
		}else if(pieceID == Piece.KING){
			int[] possibles = {7,8,9,-1,1,-7,-8,-9};
			for (int i : possibles){
				int temp = pos+i;
				int diffX = Math.abs(board.getCol(pos) - board.getCol(temp));
				int diffY = Math.abs(board.getRow(pos) - board.getRow(temp));
				if(diffX<=1 && diffY<=1 && temp >= 0 && temp< 64 && (board.pieceAt(temp) == null || board.pieceAt(temp).color != color)){
					if(board.lastPieceChecked.pos == temp){
						ret.add(new Move(this, temp, board.lastPieceChecked));
					}else{
						ret.add(new Move(this, temp));
					}
				}
			}
			
			if(!moved && !board.inCheck(color)){ //check for castling TODO: Fix this code to avoid errors
				for(Piece p : board.pieces){
					if(p.color == color && p.pieceID == Piece.ROOK && !p.moved && p.alive){
						int temp=pos;
						boolean good=true;
						if(p.pos > pos){
							while(temp<p.pos-1){
								temp++;
								if(board.pieceAt(temp) != null){
									good=false;
									break;
								}
								if(board.copyAndExecute(new Move(this, temp)).inCheck(this.color)){
									good=false;
									break;
								}
							}
							if(good){
								ret.add(new Move(this, p.pos-1));
							}
						}else{
							while(temp>p.pos+1){
								temp--;
								if(board.pieceAt(temp) != null){
									good=false;
									break;
								}
								if(board.copyAndExecute(new Move(this, temp)).inCheck(this.color)){
									good=false;
									break;
								}
							}
							if(good){
								ret.add(new Move(this, p.pos+2));
							}
						}
					}
				}
			}
		}
		if(pieceID == Piece.BISHOP || pieceID == Piece.QUEEN){
			int[][] directions = {{1,1}, {1, -1}, {-1, 1}, {-1, -1}};
			int col = board.getCol(pos);
			int row = board.getRow(pos);
			for(int[] dir : directions){
				int tempCol = col+dir[0];
				int tempRow = row+dir[1];
				if(tempCol >= 0 && tempCol < 8 && tempRow >=0 && tempRow < 8){
					int tempPos;
					do{
						tempPos = board.getPos(tempCol, tempRow);
						if(board.pieceAt(tempPos) == null){
							ret.add(new Move(this, tempPos));;
						}else if(board.pieceAt(tempPos).color != color){
							ret.add(new Move(this, tempPos, board.lastPieceChecked));
						}
						tempCol+=dir[0];
						tempRow+=dir[1];
						if(tempCol<0 || tempCol >= 8 || tempRow<0 || tempRow>=8)
							break;
					}while (board.pieceAt(tempPos) == null);
				}
			}
		}
		if(pieceID == Piece.ROOK || pieceID == Piece.QUEEN){
			int[][] directions = {{1,0}, {-1, 0}, {0, 1}, {0, -1}};
			int col = board.getCol(pos);
			int row = board.getRow(pos);
			for(int[] dir : directions){
				int tempCol = col+dir[0];
				int tempRow = row+dir[1];
				if(tempCol >= 0 && tempCol < 8 && tempRow >=0 && tempRow < 8){
					int tempPos;
					do{
						tempPos = board.getPos(tempCol, tempRow);
						if(board.pieceAt(tempPos) == null){
							ret.add(new Move(this, tempPos));;
						}else if(board.pieceAt(tempPos).color != color){
							ret.add(new Move(this, tempPos, board.lastPieceChecked));
						}
						tempCol+=dir[0];
						tempRow+=dir[1];
						if(tempCol<0 || tempCol >= 8 || tempRow<0 || tempRow>=8)
							break;
					}while (board.pieceAt(tempPos) == null);
				}
			}
		}
		return ret;
		
	}
	String pieceRep(){
		String ret = "";
		if(color == Piece.WHITE){
			ret+="w";
		}else{
			ret+="b";
		}
		if (pieceID == Piece.PAWN)
			ret+="P";
		else if (pieceID == Piece.KNIGHT)
			ret+="N";
		else if (pieceID == Piece.BISHOP)
			ret+="B";
		else if (pieceID == Piece.ROOK)
			ret+="R";
		else if (pieceID == Piece.KING)
			ret+="K";
		else if (pieceID == Piece.QUEEN)
			ret+="Q";
		return ret;
	}
	
	int value(){
		if(pieceID==Piece.QUEEN){
			return 10;
		}else if(pieceID==Piece.ROOK){
			return 5;
		}else if(pieceID==Piece.BISHOP){
			return 3;
		}else if(pieceID==Piece.KNIGHT){
			return 3;
		}else if(pieceID==Piece.PAWN){
			return 1;
		}else if(pieceID==Piece.KING){
			return Integer.MIN_VALUE; //Kind of a workaround, but prevents the king from ever coming up in piece value compare (it shouldn't even be in piece value compare in the first place)
		}
		return Integer.MIN_VALUE;
	}
}
