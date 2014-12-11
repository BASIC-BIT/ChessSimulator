import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Level3Comp extends ComputerPlayer{
	Level3Comp(boolean color, Random rand){
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