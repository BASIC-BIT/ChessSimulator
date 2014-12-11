import java.util.ArrayList;
import java.util.Random;

/**
 * Level 0 AI is the absolute lowest level of automation I can provide for the computer to play.
 * It finds all possible moves it can make, and chooses a random one.
 * 
 * @author kneislsj
 *
 */
public class Level0Comp extends ComputerPlayer{
	Level0Comp(boolean color, Random rand){
		super(color, rand);
	}
	
	boolean doMove(){
		ArrayList<Move> moves = b.getAllPossibleLocs(color);
		moves = b.validateMoves(moves);
		if(moves.size() == 0){
			return false;
		}
		int i = r.nextInt(moves.size());
		return b.executeMove(moves.get(i));
	}
	
}
