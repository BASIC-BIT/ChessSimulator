import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Level 5 Computer expands on the Piece Trade functionality of Level 4, doing it at a depth > 1
 * Checkmate
 * check avoidance
 * Piece Trade
 * Check
 * @author kneislsj
 *
 */
public class Level5Comp extends ComputerPlayer{
	Level5Comp(boolean color, Random rand){
		super(color, rand);
	}
	
	boolean doMove(){
		Integer[] savedMove;
		if(color == Piece.WHITE){
			savedMove = whiteMoves.get(b.hashCode());
		}else{
			savedMove = blackMoves.get(b.hashCode());
		}
		
		if(savedMove != null){
			Move m = new Move(b.pieceAt(savedMove[0]), savedMove[1]);
			Piece temp = b.pieceAt(savedMove[1]);
			if(temp != null){
				m.takingPiece=temp;
			}
			return executeMove(m);
		}
		
		
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
		if(moves.size() == 0){
			return false;
		}
		Collections.shuffle(moves, r);
		Move checkmateMove = getFirstMoveThatCheckmates(moves);
		if(checkmateMove != null){
			return executeMove(checkmateMove);
		}
		/*
		ArrayList<Move> checkmateAvoidanceList = getMovesThatCantCauseCheckmate(moves);
		if(checkmateAvoidanceList.size() != 0 && checkmateAvoidanceList.size() != moves.size()){
			//return executeMove(checkmateAvoidanceList.get(0)); Old function- trying to use piece trade values even with checkmate avoidance
			moves = checkmateAvoidanceList;
		}
		*/
		
		
		Move highestTrade = getHighestPieceTrade();
		if(highestTrade != null){
			return executeMove(highestTrade);
		}
		
		Move checkMove = getFirstMoveThatChecks(moves);
		if(checkMove != null){
			return executeMove(checkMove);
		}else{
			int i = r.nextInt(moves.size());
			return executeMove(moves.get(i));
		}
	}
}