import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ChessSimulator {
	final static boolean stopAfterMove = false;
	final static boolean stopAfterGame = false;
	final static boolean printBoard = false;
	static ChessGUI cg;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        cg = new ChessGUI();
        JFrame f = new JFrame("ChessChamp");
        f.add(cg.getGui());
        // Ensures JVM closes after frame(s) closed and
        // all non-daemon threads are finished
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // See http://stackoverflow.com/a/7143398/418556 for demo.
        f.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setVisible(true);
        
        
		run();
		
	}
	
	static void run(){
		int games=0;
		int whiteWins=0;
		int blackWins=0;
		int totalMoves=0;
		Random whiteRandom = new Random(System.currentTimeMillis());
		Random blackRandom = new Random(System.currentTimeMillis()+105234);
		long startTime = System.currentTimeMillis();
		GamePlayer blackLevel5 = new Level5Comp(Piece.BLACK, blackRandom);
		GamePlayer whiteLevel5 = new Level5Comp(Piece.WHITE, whiteRandom);
		while(true){
			games++;
			Game g;
			boolean playerWhite=true;
			if(playerWhite){
				g = new Game(new HumanPlayer(Piece.WHITE), blackLevel5, cg);
			}else{
				g = new Game(whiteLevel5, new HumanPlayer(Piece.BLACK), cg);
			}
			cg.game = g;
			cg.board=g.board;
			ChessSimulator.cg.drawBoard(g.board);
			int i=0;
			while(true){
				System.out.println(g.board.hashCode());
				i++;
				if(printBoard){
					g.board.printBoard();
					System.out.println("\n");
				}
				int ret = g.doMove();
				cg.getGui().repaint();
				try {
					Thread.sleep(300); //Just there to give the buffer some time to draw before computer player takes all the computation
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(stopAfterMove){
					Scanner s = new Scanner(System.in);
					s.nextLine();
				}
				if(ret == Game.STATUS_CHECKMATE_BLACK){
					System.out.println("CHECKMATE BLACK WINS!");
					JOptionPane.showMessageDialog(new JFrame(), "Checkmate, black wins!  New game starting.");
					blackWins++;
					break;
				}else if(ret == Game.STATUS_CHECKMATE_WHITE){
					System.out.println("CHECKMATE WHITE WINS!");
					JOptionPane.showMessageDialog(new JFrame(), "Checkmate, white wins! New game starting.");
					whiteWins++;
					break;
				}else if(ret == Game.STATUS_DRAW){
					//System.out.println("DRAW");
					JOptionPane.showMessageDialog(new JFrame(), "Draw game.  New game starting.");
					break;
				}else if(ret == Game.STATUS_FORCE_GAME_END){
					JOptionPane.showMessageDialog(new JFrame(), "Game forcibly ended.  New game starting.");
					break;
				}
				ChessSimulator.cg.drawBoard(g.board);
			}
			totalMoves+=i;
			
			if(whiteWins >= 100 || blackWins>= 100){
				break;
			}
			if(stopAfterGame){
				g.board.printBoard();
				System.out.println("\n");
				Scanner s = new Scanner(System.in);
				s.nextLine();
			}
		}
		long totalTime = System.currentTimeMillis()-startTime;
		System.out.println("Games: "+games);
		System.out.println("White Won: "+whiteWins);
		System.out.println("Black Won: "+blackWins);
		System.out.println("Total time: "+totalTime/1000+" seconds");
		System.out.println("Average time per game: "+totalTime/games+" milliseconds");
		System.out.println("Average time per move: "+totalTime/totalMoves+" milliseconds");
		System.out.println("Average moves per game: "+totalMoves/games);
	}

}
