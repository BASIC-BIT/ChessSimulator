import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public abstract class ComputerPlayer extends GamePlayer{
	Random r;
	HashMap <Integer, Integer[]> blackMoves = new HashMap <Integer, Integer[]>();
	HashMap <Integer, Integer[]> whiteMoves = new HashMap <Integer, Integer[]>();
	ComputerPlayer(boolean color, Random rand) {
		super(color);
		
		//Magical table of values for the first AI move, taken from Stockfish.  It seems cheaty, but my AI doesn't look far enough ahead to make a good choice for first move.
		whiteMoves.put((Integer)44234058, new Integer[]{12, 28});
		
		
		blackMoves.put((Integer)4483376, new Integer[]{62, 45});
		blackMoves.put((Integer)12233800, new Integer[]{51, 35});
		blackMoves.put((Integer)17957780, new Integer[]{62, 45});
		blackMoves.put((Integer)13015560, new Integer[]{62, 45});
		blackMoves.put((Integer)30846980, new Integer[]{62, 45});
		blackMoves.put((Integer)2623592, new Integer[]{52, 36});
		blackMoves.put((Integer)65256020, new Integer[]{62, 45});
		blackMoves.put((Integer)52361288, new Integer[]{62, 45});
		
		this.r = rand;
	}
	abstract boolean doMove();

	Move getHighestPieceTrade(ArrayList<Move> moves){
		Move highestMove = null;
		int highestVal=Integer.MIN_VALUE; //work around for compare
		for(Move m : moves){
			if(m.takingPiece != null && m.takingPiece.value() > highestVal && m.takingPiece.value() > m.piece.value()){
				highestMove = m;
				highestVal = m.takingPiece.value();
			}
		}
		return highestMove;
	}
	
	Move getHighestPieceTrade(){ //old code, hopefully won't ever use it again, but keeping it just in case.
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
		
		

		ArrayList<Double> tradeVals = getPieceTradeVals(b,color, color, 1, 0.0);
		
		double highestVal=Integer.MIN_VALUE;
		
		for(int i=0;i<tradeVals.size();i++){
			if(tradeVals.get(i)>highestVal){
				highestVal=tradeVals.get(i);
			}
		}
		System.out.println(highestVal);
		ArrayList<Move> highestMoves = new ArrayList<Move>();
		for(int i=0;i<tradeVals.size();i++){
			if(tradeVals.get(i) == highestVal){
				highestMoves.add(moves.get(i));
			}
		}
		if(highestMoves.size() != 0){
			Collections.shuffle(highestMoves, r);
			return highestMoves.get(0);
		}else{
			return null;
		}
		
	}
	
	ArrayList<Double> getPieceTradeVals(Board b, boolean color, boolean curColor, int depth, double lastTradeVal){
		ArrayList<Move> moves = b.getAllPossibleLocs(curColor);
		moves = b.validateMoves(moves);
		List<Double> tradeVals = Collections.synchronizedList(new ArrayList<Double>());
		for(int i=0;i<moves.size();i++){
			Move m = moves.get(i);
			if(depth <= 0 && ( (depth <= 2) ||
					((depth <= 4) && (lastTradeVal < -2 || lastTradeVal > 2 ||  depth % 2 == 0 || b.inCheck() || moves.get(i).takingPiece != null)) ||
					((depth <= 6) && (moves.get(i).takingPiece != null || b.inCheck())))){
				double offset = 0; //using an offset in case it does something that is considered possibly favorable and unfavorable (such as getting rid of castling options to capture a piece)
				if(m.piece.pieceID == Piece.PAWN && b.getRow(m.moveTo) == 0 || b.getRow(m.moveTo) == 8){ //if pawn is doing queen promotion
					offset+=9;
				}
				if(m.piece.pieceID == Piece.KING && Math.abs(m.moveTo - m.piece.pos) == 2){ //if it is castling
					offset+=0.5;
				}
				if(m.piece.pieceID == Piece.KING && m.piece.moved == false && Math.abs(m.moveTo - m.piece.pos) != 2){ //if a king is making a move that's not castling, it's forfeiting it's ability to castle, so not favorable.
					offset-=0.4;
				}
				if(b.getKing(color).moved == false && m.piece.pieceID == Piece.ROOK && m.piece.moved == false){ //losing ability to castle with that rook, slightly unfavorable
					offset-=0.2;
				}
				if(m.piece.pieceID != Piece.KING && m.piece.pieceID != Piece.QUEEN && m.piece.pieceID != Piece.PAWN && m.piece.moved == false && m.moveTo >= 16 && m.moveTo < 48){
					//This is a bit more of a tricky case that is going to need fine tuning.  If we are moving a piece (that we want to expose), that we haven't moved yet and we are moving it out into play, it is favorable
					offset+=0.05;
				}
				if(m.piece.pieceID != Piece.KING &&  m.piece.pieceID != Piece.PAWN){
					//The opposite of the last case- moving pieces back into (their own) homerow is generally considered not good unless needed.
					if((m.piece.color == Piece.WHITE && m.moveTo < 8) || (m.piece.color == Piece.BLACK && m.moveTo >=56)){
						offset-=0.03;
					}
				}
				/*int col = b.getCol(m.moveTo);
				for(int ii=0;ii<8;ii++){
					Piece p = b.pieceAt(col+ii*8);
					if(p != null && p.pieceID == Piece.PAWN && p != m.piece && p.color == m.piece.color){ // if there is another pawn of the same color in that column
						offset-=0.03;
						break;
					}
				}*/
				
				
				if(m.takingPiece==null){
					if(color == m.piece.color){
						tradeVals.add(offset);
					}else{
						tradeVals.add(-offset);
					}
				}else{
					if(color == m.piece.color){
						tradeVals.add((double)m.takingPiece.value()+offset);
					}else{
						tradeVals.add(-(double)m.takingPiece.value()-offset);
					}
				}
			}else{
				tradeVals.add(0.0); //Don't bother computing if those.
			}
		}
		if(depth < 4){
			Thread[] threads = new Thread[moves.size()];
			
			for(int i=0;i<moves.size();i++){
				threads[i] = new TradeValsThread(this, moves.get(i), tradeVals, i, b, color, curColor, depth);
				if(depth > 0){
					threads[i].run();
				}else{
					threads[i].start();
				}
			}
			if(depth == 0){
				for(int i=0;i<moves.size();i++){
					try {
						if(threads[i] != null)
							threads[i].join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		ArrayList<Double> ret = new ArrayList<Double>();
		
		for(double val : tradeVals){
			ret.add(val);
		}
		return ret;
	}
	/*Move getHighestPieceTrade(){ 
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
	
		ArrayList<Integer> tradeVals = getPieceTradeVals(b,color, color, 4);
		
		int highestVal=Integer.MIN_VALUE;
		
		for(int i=0;i<tradeVals.size();i++){
			if(tradeVals.get(i)>highestVal){
				highestVal=tradeVals.get(i);
			}
		}
		ArrayList<Move> highestMoves = new ArrayList<Move>();
		for(int i=0;i<tradeVals.size();i++){
			if(tradeVals.get(i) == highestVal){
				highestMoves.add(moves.get(i));
			}
		}
		if(highestMoves.size() != 0){
			Collections.shuffle(highestMoves, r);
			return highestMoves.get(0);
		}else{
			return null;
		}
		
	}
	
	ArrayList<Integer> getPieceTradeVals(Board b, boolean color, boolean curColor, int depth){
		ArrayList<Move> moves = b.getAllPossibleLocs(curColor);
		moves = b.validateMoves(moves);
		ArrayList<Integer> tradeVals = new ArrayList<Integer>();
		for(Move m : moves){
			if(m.piece.pieceID == Piece.PAWN && b.getRow(m.moveTo) == 0 || b.getRow(m.moveTo) == 8){ //if pawn is doing queen promotion
				if(color == m.piece.color){
					tradeVals.add(9);
				}else{
					tradeVals.add(-9);
				}
			}else if(m.takingPiece==null){
				tradeVals.add(0);
			}else{
				if(color == m.piece.color){
					tradeVals.add(m.takingPiece.value());
				}else{
					tradeVals.add(-m.takingPiece.value());
				}
			}
		}
		if(depth > 1){
			for(int i=0;i<moves.size();i++){
				if(moves.get(i).piece.color == color){
					tradeVals.set(i, tradeVals.get(i)+getSmallest(getPieceTradeVals(copyAndExecute(b,moves.get(i)), color, !curColor, depth-1)));
				}else{
					tradeVals.set(i, tradeVals.get(i)+getLargest(getPieceTradeVals(copyAndExecute(b,moves.get(i)), color, !curColor, depth-1)));
				}
			}
		}
		return tradeVals;
	}*/
	Integer getLargest(ArrayList<Integer> list){
		if(list.size() == 0){
			return 0;
		}
		int largestInt=Integer.MIN_VALUE;
		
		for(int i : list){
			if(i > largestInt){
				largestInt = i;
			}
		}
		return largestInt;
	}
	
	Integer getSmallest(ArrayList<Integer> list){
		if(list.size() == 0){
			return 0;
		}
		int smallestInt=Integer.MAX_VALUE;
		
		for(int i : list){
			if(i < smallestInt){
				smallestInt = i;
			}
		}
		return smallestInt;
	}
	
	Double getLargestDouble(ArrayList<Double> list){
		if(list.size() == 0){
			return 0.0;
		}
		double largestInt=Integer.MIN_VALUE;
		
		for(double i : list){
			if(i > largestInt){
				largestInt = i;
			}
		}
		return largestInt;
	}
	
	Double getSmallestDouble(ArrayList<Double> list){
		if(list.size() == 0){
			return 0.0;
		}
		double smallestInt=Integer.MAX_VALUE;
		
		for(double i : list){
			if(i < smallestInt){
				smallestInt = i;
			}
		}
		return smallestInt;
	}
	
	
	ArrayList<Move> getMovesThatCheck(ArrayList<Move> moves){ //checks moves to validate that they can be done (with check, checkmates, etc)
		ArrayList<Move> ret = new ArrayList<Move>();
		for(Move m : moves){
			if(testCheckWithMove(m)){
				ret.add(m);
			}
		}
		return ret;
	}
	
	Move getFirstMoveThatChecks(ArrayList<Move> moves){ //checks moves to validate that they can be done (with check, checkmates, etc)
		for(Move m : moves){
			if(testCheckWithMove(m)){
				return m;
			}
		}
		return null;
	}
	
	Move getFirstMoveThatCheckmates(ArrayList<Move> moves){ //checks moves to validate that they can be done (with check, checkmates, etc)
		for(Move m : moves){
			if(testCheckmateWithMove(m)){
				return m;
			}
		}
		return null;
	}
	
	boolean testCheckWithMove(Move m){ //copys the board, executes the move, and tests if its in check
		return copyAndExecute(m).inCheck(!color);
	}
	
	boolean testCheckmateWithMove(Move m){ //copys the board, executes the move, and tests if its in check still
		Board testBoard = copyAndExecute(m);
		if(testBoard.inCheck(!color)){
			ArrayList<Move> enemyLocs = testBoard.getAllPossibleLocs(!color);
			enemyLocs = testBoard.validateMoves(enemyLocs);
			return (enemyLocs.size() == 0);
		}else{
			return false;
		}
	}
	
	Board copyAndExecute(Move m){
		Board testBoard = b.clone();
		Move mc = m.clone();
		mc.piece = testBoard.pieceAt(mc.piece.pos); //copy the move, and switch pieces over to new board if applicable
		if(mc.takingPiece!=null){
			mc.takingPiece = testBoard.pieceAt(mc.takingPiece.pos);
		}
		testBoard.executeMove(mc);
		return testBoard;
	}
	
	Board copyAndExecute(Board b, Move m){
		Board testBoard = b.clone();
		Move mc = m.clone();
		mc.piece = testBoard.pieceAt(mc.piece.pos); //copy the move, and switch pieces over to new board if applicable
		if(mc.takingPiece!=null){
			mc.takingPiece = testBoard.pieceAt(mc.takingPiece.pos);
		}
		testBoard.executeMove(mc);
		return testBoard;
	}
	/**
	 * Tests all possible moves, and returns only the moves that can't cause check.
	 * Loops though every possible move, executes the move on a test board - it then gets all possible moves by the other player - if any of those moves include possible check, it doesn't include it.
	 * @param moves
	 * @return
	 */
	ArrayList<Move> getMovesThatCantCauseCheckmate(ArrayList<Move> moves){
		ArrayList<Move> ret = new ArrayList<Move>();
		for(Move m : moves){
			if(testMoveCantCauseCheckmate(m)){
				ret.add(m);
			}
		}
		return ret;
	}
	
	boolean testMoveCantCauseCheckmate(Move m){
		Board testBoard = copyAndExecute(m);
		
		ArrayList<Move> enemyMoves = testBoard.getAllPossibleLocs(!m.piece.color);
		enemyMoves = testBoard.validateMoves(enemyMoves);
		
		for(Move em : enemyMoves){
			if(doesMoveCauseCheckmate(testBoard, em))
				return false;
		}
		return true;
	}
	boolean doesMoveCauseCheckmate(Board tb, Move m){
		Board testBoard = tb.clone();
		Move mc = m.clone();
		mc.piece = testBoard.pieceAt(mc.piece.pos); //copy the move, and switch pieces over to new board if applicable
		if(mc.takingPiece!=null){
			mc.takingPiece = testBoard.pieceAt(mc.takingPiece.pos);
		}
		testBoard.executeMove(mc);
		
		if(testBoard.inCheck(!m.piece.color)){ //it can only be checkmate if its even in check
			ArrayList<Move> moves = testBoard.getAllPossibleLocs(!m.piece.color);
			moves = testBoard.validateMoves(moves);
			return (moves.size() == 0);
		}
		return false;
	}
	
	boolean executeMove(Move m){
		if (color == Piece.BLACK){
			((ComputerPlayer)g.blackPlayer).blackMoves.put(b.hashCode(), new Integer[]{m.piece.pos, m.moveTo});
		}else{
			((ComputerPlayer)g.whitePlayer).whiteMoves.put(b.hashCode(), new Integer[]{m.piece.pos, m.moveTo});
		}
		return b.executeMove(m);
	}
}
