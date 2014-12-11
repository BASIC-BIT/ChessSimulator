import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Level1Comp extends ComputerPlayer{
	Level1Comp(boolean color, Random rand){
		super(color, rand);
	}
	
	boolean doMove(){
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
		if(moves.size() == 0){
			return false;
		}
		Collections.shuffle(moves, r);
		Move checkMove = getFirstMoveThatChecks(moves);
		if(checkMove != null){
			return b.executeMove(checkMove);
		}else{
			int i = r.nextInt(moves.size());
			return b.executeMove(moves.get(i));
		}
	}
	
}