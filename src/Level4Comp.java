import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Level 4 Computer implements a next-move-checkmate avoidance, by (if it is possible) doing a move that won't cause a checkmate on the enemies direct next move.  The order of checks goes:
 * Checkmate
 * check avoidance
 * Piece Trade
 * Check
 * @author kneislsj
 *
 */
public class Level4Comp extends ComputerPlayer{
	Level4Comp(boolean color, Random rand){
		super(color, rand);
	}
	
	boolean doMove(){
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
		if(moves.size() == 0){
			return false;
		}
		Collections.shuffle(moves, r);
		Move checkmateMove = getFirstMoveThatCheckmates(moves);
		if(checkmateMove != null){
			return b.executeMove(checkmateMove);
		}
		
		ArrayList<Move> checkmateAvoidanceList = getMovesThatCantCauseCheckmate(moves);
		if(checkmateAvoidanceList.size() != 0 && checkmateAvoidanceList.size() != moves.size()){
			return b.executeMove(checkmateAvoidanceList.get(0));
			
		}
		
		
		Move highestTrade = getHighestPieceTrade(moves);
		if(highestTrade != null){
			return b.executeMove(highestTrade);
		}
		
		Move checkMove = getFirstMoveThatChecks(moves);
		if(checkMove != null){
			return b.executeMove(checkMove);
		}else{
			int i = r.nextInt(moves.size());
			return b.executeMove(moves.get(i));
		}
	}
}