
public abstract class GamePlayer {
	Board b;
	Game g;
	boolean color;
	
	GamePlayer(boolean color){
		this.color = color;
	}
	
	abstract boolean doMove();
}
