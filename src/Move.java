
public class Move {
	Piece piece;
	int moveTo;
	Piece takingPiece;
	
	Move(Piece piece, int moveTo){
		this.piece=piece;
		this.moveTo=moveTo;
	}
	
	Move(Piece piece, int moveTo, Piece takingPiece){
		this.piece=piece;
		this.moveTo=moveTo;
		this.takingPiece=takingPiece;
	}
	public Move clone(){
		Move ret = new Move(piece,moveTo, takingPiece);
		return ret;
	}
	public String toString(){
		String ret = "";
		ret+=getPosNotation(piece.pos);
		if(takingPiece != null){
			ret+="x";
		}else{
			ret+=" ";
		}
		ret+=getPosNotation(moveTo);
		return ret;
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
	String getPosNotation(int pos){
		char col=(char)(97+getCol(pos));
		String ret = "";
		ret+=col;
		ret+=Integer.toString(getRow(pos)+1);
		return ret;
	}
}
