import java.util.ArrayList;
import java.util.List;


public class TradeValsThread extends Thread {
	ComputerPlayer cp;
	Move m;
	List<Double> tradeVals;
	int i;
	Board b;
	boolean color;
	boolean curColor;
	int depth;
	TradeValsThread(ComputerPlayer cp, Move m, List<Double> tradeVals, int i, Board b, boolean color, boolean curColor, int depth){
		this.cp=cp;
		this.m=m;
		this.tradeVals=tradeVals;
		this.i=i;
		this.b=b;
		this.color=color;
		this.curColor=curColor;
		this.depth=depth;
	}
	
	public void run(){
		if(m.piece.color == color){
			ArrayList<Double> newBoardTradeVals = cp.getPieceTradeVals(cp.copyAndExecute(b,m), color, !curColor, depth+1, tradeVals.get(i).doubleValue());
			if(newBoardTradeVals.size() == 0){
				tradeVals.set(i, 200000.0); // a bigass number just to make it so it will always go for a checkmate if possible
			}else{
				tradeVals.set(i, tradeVals.get(i)+cp.getSmallestDouble(newBoardTradeVals));
			}
		}else{
			ArrayList<Double> newBoardTradeVals = cp.getPieceTradeVals(cp.copyAndExecute(b,m), color, !curColor, depth+1, tradeVals.get(i).doubleValue());
			if(newBoardTradeVals.size() == 0){
				tradeVals.set(i, -200000.0); // a bigass number just to make it so it will always avoid getting checkmated if possible
			}else{
				tradeVals.set(i, tradeVals.get(i)+cp.getLargestDouble(newBoardTradeVals));
			}
		}
	}
}
