import java.util.ArrayList;
import java.util.Scanner;


public class HumanPlayer extends GamePlayer {
	Move moveToMake=null;
	HumanPlayer(boolean color) {
		super(color);
	}

	boolean doMove() {
		while(true){
			try{
				ArrayList<Move> moves = b.getAllPossibleLocs(color);
				moves = b.validateMoves(moves);
				if(moves.size()==0){return false;}
				 g.waitingForPlayerInput=true;
				
				moveToMake=null;
				while(moveToMake==null){
					if(g.endGame){
						break;
					}
					Thread.sleep(100);
				}
				g.waitingForPlayerInput=false;
				/*if(moveIn.length() != 5 || !(moveIn.charAt(2) == ' ' || moveIn.charAt(2) == 'x')){
					System.out.println("Invalid entry!\n");
					continue;
				}
				moveIn = moveIn.toLowerCase();
				int moveFrom = b.getPos((int)(moveIn.charAt(0))-97, ((int)(moveIn.charAt(1))-49));
				int moveTo = b.getPos((int)(moveIn.charAt(3))-97, ((int)(moveIn.charAt(4))-49));
				
				System.out.println(moveFrom);
				System.out.println(moveTo);
				
				boolean good=false;
				ArrayList<Move> moves = b.getAllPossibleLocs(color);
				moves = b.validateMoves(moves);
				
				for(Move m : moves){
					if(m.piece.pos == moveFrom && m.moveTo == moveTo){
						good=true;
					}
				}
				
				if(!good){
					System.out.println("Invalid entry!\n");
					continue;
				}
				
				Piece p =  b.pieceAt(moveFrom);
				if(p == null || p.color != color){
					System.out.println("no piece");
					System.out.println("Invalid entry!\n");
					continue;
				}
				Move m = new Move(p,moveTo);
				
				if(moveIn.charAt(2) == 'x'){
					m.takingPiece = b.pieceAt(moveTo);
					if(m.takingPiece == null || m.takingPiece.color == color){
						System.out.println("no piece");
						System.out.println("Invalid entry!\n");
						continue;
					}
				}*/
				/*if(moveIn.charAt(2) == ' ' && b.pieceAt(moveTo) == null){
					System.out.println("no piece");
					System.out.println("Invalid entry!\n");
					continue;
				}*/
				
				//Now test if move m is valid (if it is equal to something in the validated moves list)
				
				
				b.executeMove(moveToMake);
				return true;
				
			}catch(Exception e){
				
			}
			return false;
		}
		
	}
	

}
