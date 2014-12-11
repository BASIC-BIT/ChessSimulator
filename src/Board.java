import java.util.ArrayList;
import java.util.HashMap;


public class Board {
	Piece[] pieces = new Piece[32];
	Piece blackKing;
	Piece whiteKing;
	Piece lastPieceChecked;
	
	HashMap<Integer, ArrayList<Move>> blackLocs = new HashMap<Integer, ArrayList<Move>>();
	HashMap<Integer, ArrayList<Move>> blackLocsIgnoreCastle = new HashMap<Integer, ArrayList<Move>>();
	HashMap<Integer, ArrayList<Move>> whiteLocs = new HashMap<Integer, ArrayList<Move>>();
	HashMap<Integer, ArrayList<Move>> whiteLocsIgnoreCastle = new HashMap<Integer, ArrayList<Move>>();
	
	HashMap<ArrayList<Move>, ArrayList<Move>> validateMovesCache = new HashMap<ArrayList<Move>, ArrayList<Move>>();
	
	HashMap<Integer, Boolean> whiteCheckCache = new HashMap<Integer, Boolean>();
	HashMap<Integer, Boolean> blackCheckCache = new HashMap<Integer, Boolean>();
	
	
	Integer[] primeslist = new Integer[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29 
		, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71 
		, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113 
		, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173 
		, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229 
		, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281 
		, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349 
		, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409 
		, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463 
		, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541 
		, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601 
		, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659 
		, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733 
		, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809 
		, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863 
		, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941 
		, 947, 953, 967, 971, 977, 983, 991, 997, 1009};
	
	Board(){
		pieces[0] = new Piece(this,Piece.ROOK,Piece.WHITE,0);
		pieces[1] = new Piece(this,Piece.KNIGHT,Piece.WHITE,1);
		pieces[2] = new Piece(this,Piece.BISHOP,Piece.WHITE,2);
		pieces[3] = new Piece(this,Piece.QUEEN,Piece.WHITE,3);
		pieces[4] = new Piece(this,Piece.KING,Piece.WHITE,4);
		pieces[5] = new Piece(this,Piece.BISHOP,Piece.WHITE,5);
		pieces[6] = new Piece(this,Piece.KNIGHT,Piece.WHITE,6);
		pieces[7] = new Piece(this,Piece.ROOK,Piece.WHITE,7);
		pieces[8] = new Piece(this,Piece.PAWN,Piece.WHITE,8);
		pieces[9] = new Piece(this,Piece.PAWN,Piece.WHITE,9);
		pieces[10] = new Piece(this,Piece.PAWN,Piece.WHITE,10);
		pieces[11] = new Piece(this,Piece.PAWN,Piece.WHITE,11);
		pieces[12] = new Piece(this,Piece.PAWN,Piece.WHITE,12);
		pieces[13] = new Piece(this,Piece.PAWN,Piece.WHITE,13);
		pieces[14] = new Piece(this,Piece.PAWN,Piece.WHITE,14);
		pieces[15] = new Piece(this,Piece.PAWN,Piece.WHITE,15);
		pieces[16] = new Piece(this,Piece.ROOK,Piece.BLACK,63);
		pieces[17] = new Piece(this,Piece.KNIGHT,Piece.BLACK,62);
		pieces[18] = new Piece(this,Piece.BISHOP,Piece.BLACK,61);
		pieces[19] = new Piece(this,Piece.QUEEN,Piece.BLACK,59);
		pieces[20] = new Piece(this,Piece.KING,Piece.BLACK,60);
		pieces[21] = new Piece(this,Piece.BISHOP,Piece.BLACK,58);
		pieces[22] = new Piece(this,Piece.KNIGHT,Piece.BLACK,57);
		pieces[23] = new Piece(this,Piece.ROOK,Piece.BLACK,56);
		pieces[24] = new Piece(this,Piece.PAWN,Piece.BLACK,55);
		pieces[25] = new Piece(this,Piece.PAWN,Piece.BLACK,54);
		pieces[26] = new Piece(this,Piece.PAWN,Piece.BLACK,53);
		pieces[27] = new Piece(this,Piece.PAWN,Piece.BLACK,52);
		pieces[28] = new Piece(this,Piece.PAWN,Piece.BLACK,51);
		pieces[29] = new Piece(this,Piece.PAWN,Piece.BLACK,50);
		pieces[30] = new Piece(this,Piece.PAWN,Piece.BLACK,49);
		pieces[31] = new Piece(this,Piece.PAWN,Piece.BLACK,48);
		
		blackKing = pieces[20];
		whiteKing = pieces[4];
	}
	public int hashCode(){
		int output = 0;
		for(int i=0;i<32;i++){
			Piece p = pieces[i];
			if(p.alive){
				if(output==0){
					output=p.hashCode();
				}else{
					output = output ^ (p.hashCode() * primeslist[20+i*2]);
				}
			}
		}
		return output;
	}
	
	Piece pieceAt(int pos){
		for(Piece p : pieces){
			if(p != null && p.pos == pos && p.alive){
				lastPieceChecked=p;
				return p;
			}
		}
		return null;
	}
	
	int getCol(int pos){
		return pos % 8;
	}
	
	int getRow(int pos){
		return (pos - pos % 8)/8;
	}
	int getPos(int col, int row){
		return row*8+col;
	}
	Piece getKing(boolean color){
		if(color == Piece.WHITE){
			return whiteKing;
		}else{
			return blackKing;
		}
	}

	/*ArrayList<Move> getAllPossibleLocs(boolean color){
		return getAllPossibleLocs(color,false);
	}
	ArrayList<Move> getAllPossibleLocs(boolean color, boolean ignoreCastle){
		ArrayList<Move> ret = new ArrayList<Move>();
		for(Piece p : pieces){
			if(p != null && p.color == color && p.alive && !(ignoreCastle && p.pieceID == Piece.KING)){
				ArrayList<Move> temp = p.getPossibleLocs();
				if(temp != null){
					ret.addAll(temp);
				}
			}
		}
		return ret;
	}*/
	
	ArrayList<Move> getAllPossibleLocs(boolean color){
		return getAllPossibleLocs(color,false);
	}
	ArrayList<Move> getAllPossibleLocs(boolean color, boolean ignoreCastle){  //ignore castle only used for inCheck function
		ArrayList<Move> alreadyExisting = null;
		if(ignoreCastle && color == Piece.WHITE){
			alreadyExisting = whiteLocsIgnoreCastle.get(this.hashCode());
		}else if(!ignoreCastle && color == Piece.WHITE){
			alreadyExisting = whiteLocs.get(this.hashCode());
		}else if(ignoreCastle){
			alreadyExisting = blackLocsIgnoreCastle.get(this.hashCode());
		}else{
			alreadyExisting = blackLocs.get(this.hashCode());
		}
		
		if(alreadyExisting != null){
			return alreadyExisting;
		}else{
			ArrayList<Move> ret = new ArrayList<Move>();
			for(Piece p : pieces){
				if(p != null && p.color == color && p.alive && !(ignoreCastle && p.pieceID == Piece.KING)){
					ArrayList<Move> temp = p.getPossibleLocs();
					if(temp != null){
						ret.addAll(temp);
					}
				}
			}
			
			
			if(ignoreCastle && color == Piece.WHITE){
				alreadyExisting = whiteLocsIgnoreCastle.put(this.hashCode(), ret);
			}else if(!ignoreCastle && color == Piece.WHITE){
				alreadyExisting = whiteLocs.put(this.hashCode(), ret);
			}else if(ignoreCastle){
				alreadyExisting = blackLocsIgnoreCastle.put(this.hashCode(), ret);
			}else{
				alreadyExisting = blackLocs.put(this.hashCode(), ret);
			}
			return ret;
		}
	}
	

	
	boolean inCheck(boolean color){
		boolean cachedVal=false;
		boolean found=false;
		if(color == Piece.WHITE){
			found=whiteCheckCache.containsKey(this.hashCode());
			if(found){
				cachedVal=whiteCheckCache.get(this.hashCode());
			}
		}else{
			found=blackCheckCache.containsKey(this.hashCode());
			if(found){
				cachedVal=blackCheckCache.get(this.hashCode());
			}
		}
		if(found){
			return cachedVal;
		}else{
			boolean ret=false;
			ArrayList<Move> locs = getAllPossibleLocs(!color, true);
			for(Move m : locs){
				if(m.takingPiece != null && m.takingPiece.pieceID == Piece.KING){
					ret=true;
				}
			}
			if(color == Piece.WHITE){
				whiteCheckCache.put(this.hashCode(), ret);
			}else{
				blackCheckCache.put(this.hashCode(), ret);
			}
			return ret;
		}
	}
	
	boolean inCheck(){ //checks for both colors
		return inCheck(false) || inCheck(true);
	}
	
	public Board clone(){
		Board ret = new Board();
		ret.pieces = this.pieces.clone();
		for(int i=0;i<32;i++){
			ret.pieces[i]=this.pieces[i].clone(ret);
		}
		return ret;
	}
	
	boolean canDoMove(Move m){ //copys the board, executes the move, and tests if its in check still
		boolean good=true;
		Board testBoard = this.clone();
		Move mc = m.clone();
		mc.piece = testBoard.pieceAt(mc.piece.pos); //copy the move, and switch pieces over to new board if applicable
		if(mc.takingPiece!=null){
			mc.takingPiece = testBoard.pieceAt(mc.takingPiece.pos);
		}
		testBoard.executeMove(mc);
		good = good && !testBoard.inCheck(mc.piece.color);
		return good;
	}
	
	int piecesAlive(){
		int count=0;
		for(int i=0;i<32;i++){
			if(pieces[i].alive)
				count++;
		}
		return count;
	}
	
	ArrayList<Move> validateMoves(ArrayList<Move> moves){ //checks moves to validate that they can be done (with check, checkmates, etc)
		ArrayList<Move> cached = validateMovesCache.get(moves);
		if(cached != null){
			return cached;
		}else{
			ArrayList<Move> ret = new ArrayList<Move>();
			for(Move m : moves){
				if(canDoMove(m)){
					ret.add(m);
				}
			}
			validateMovesCache.put(moves,ret);
			return ret;
		}
	}
	
	boolean executeMove(Move m){ // do NOT call except after canDoMove has been checked
		if(m.piece.pieceID == Piece.KING && !m.piece.moved && Math.abs(m.moveTo-m.piece.pos)==2){ //if castle, then also need to move rook
			if(m.moveTo>m.piece.pos){
				Piece temp = pieceAt(m.moveTo+1);
				if(temp == null){
					System.out.print("Null castle error: ");
					System.out.print(m.piece);
					System.out.println(m.moveTo);
				}
				temp.pos = m.moveTo-1;
				temp.moved = true;
			}else{
				Piece temp = pieceAt(m.moveTo-2);
				if(temp == null){
					System.out.print("Null castle error: ");
					System.out.print(m.piece);
					System.out.println(m.moveTo);
				}
				temp.pos = m.moveTo+1;
				temp.moved = true;
			}
		}
		m.piece.pos=m.moveTo;
		if(m.takingPiece!= null){
			m.takingPiece.alive=false;
		}
		if(m.piece.pieceID == Piece.PAWN && (getRow(m.piece.pos) == 0 || getRow(m.piece.pos) == 7)){
			m.piece.pieceID=Piece.QUEEN;
		}
		m.piece.moved=true;
		return true;
	}
	
	void printBoard(){
		for(int i=0;i<8;i++){
			System.out.print(8-i+" ");
			for(int ii=0;ii<8;ii++){
				System.out.print("[");
				boolean found=false;
				for(int pi=0;pi<32;pi++){
					if(pieces[pi].alive && pieces[pi].pos==getPos(ii,7-i)){
						found=true;
						System.out.print(pieces[pi].pieceRep());
						break;
					}
				}
				if(!found){
					System.out.print("  ");
				}
				System.out.print("]");
			}
			System.out.println();
		}
		System.out.println("   A   B   C   D   E   F   G   H");
	}
	
	Board copyAndExecute(Move m){
		Board testBoard = this.clone();
		Move mc = m.clone();
		mc.piece = testBoard.pieceAt(mc.piece.pos); //copy the move, and switch pieces over to new board if applicable
		if(mc.takingPiece!=null){
			mc.takingPiece = testBoard.pieceAt(mc.takingPiece.pos);
		}
		testBoard.executeMove(mc);
		return testBoard;
	}
}
