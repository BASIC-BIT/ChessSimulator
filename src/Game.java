import java.io.IOException;

public class Game {
	static int STATUS_OK=0;
	static int STATUS_CHECKMATE_WHITE=1;
	static int STATUS_CHECKMATE_BLACK=2;
	static int STATUS_DRAW=3;
	static int STATUS_FORCE_GAME_END=4;
	
	boolean endGame;
	
	int move=0;
	
	
	Board board;
	boolean colorToMove;
	GamePlayer whitePlayer;
	GamePlayer blackPlayer;
	ChessGUI gui;
	boolean waitingForPlayerInput=false;
	Game(GamePlayer whitePlayer, GamePlayer blackPlayer, ChessGUI gui){
		board = new Board();
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		
		this.whitePlayer.b=board;
		this.blackPlayer.b=board;
		
		this.whitePlayer.g=this;
		this.blackPlayer.g=this;
		colorToMove=Piece.WHITE;
		this.gui = gui;
	}
	
	int doMove(){
		move++;
		GamePlayer player;
		if(colorToMove == Piece.WHITE){
			player=whitePlayer;
		}else{
			player=blackPlayer;
		}
		colorToMove = !colorToMove; //switch player for next run through
		if(player instanceof ComputerPlayer){
			gui.message.setText("Waiting for computer player.");
		}else{
			gui.message.setText("Waiting for human input.");
		}
		int status = getStatus(player.doMove());
		return status;
	}
	
	int getStatus(boolean moveResult){
		if(endGame){
			return STATUS_FORCE_GAME_END;
		}else if(!moveResult){ //if it was either a checkmate or a draw
			if(board.inCheck(!colorToMove)){ // if it was a checkmate
				if(colorToMove == Piece.WHITE){
					return STATUS_CHECKMATE_WHITE; //white wins
				}else{
					return STATUS_CHECKMATE_BLACK; //black wins
				}
			}else{ //player couldn't move, but was not in check - draw
				return STATUS_DRAW;
			}
		}else if(board.piecesAlive() == 2){ //Force a draw if there are only 2 kings
			return STATUS_DRAW;
		}else{ //if everything was fine
			return STATUS_OK;
		}
	}
}
