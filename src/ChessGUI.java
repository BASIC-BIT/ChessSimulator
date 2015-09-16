import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ChessGUI implements ActionListener{
	public Color highlightColor=new Color(200,200,255);
	public Piece lastPieceHighlighted=null;
	public Board board = new Board();
	public Game game = null;
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private Image[][] chessPieceImages = new Image[2][6];
    private JPanel chessBoard;
    public JLabel message = new JLabel(
            "Chess is ready to play!");
    private static final String COLS = "ABCDEFGH";
    public static final int QUEEN = 0, KING = 1,
            ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    public static final int[] STARTING_ROW = {
        ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK
    };
    public static final int NEWGAME=1;
    public static final int BLACK = 0, WHITE = 1;

    ChessGUI() {
        initializeGui();
    }
  

    public final void initializeGui() {
        // create the images for the chess pieces
        createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        Action newTwoPlayerGame = new AbstractAction("New Game (2P)") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	game.endGame=true;
            }
        };
        Action newBlackHumanGame = new AbstractAction("New Game (Black)") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	game.endGame=true;
            }
        };
        Action newWhiteHumanGame = new AbstractAction("New Game (White)") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	game.endGame=true;
            }
        };
        Action newComputerGame = new AbstractAction("New Game (Comp Only)") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	game.endGame=true;
            }
        };
        Action resign = new AbstractAction("Resign") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	game.endGame=true;
            }
        };
        tools.add(newTwoPlayerGame);
        //tools.add(newBlackHumanGame);
        //tools.add(newWhiteHumanGame);
        tools.add(newComputerGame);
        tools.addSeparator();
        tools.add(resign);
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel(new GridLayout(0, 9)) {

            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int)d.getWidth(),(int)d.getHeight());
                } else if (c!=null &&
                        c.getWidth()>d.getWidth() &&
                        c.getHeight()>d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (w>h ? h : w);
                return new Dimension(s,s);
            }
        };
        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8,8,8,8),
                new LineBorder(Color.BLACK)
                ));
        // Set the BG 
        Color ochre = new Color(180,180,180);
        chessBoard.setBackground(ochre);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(ochre);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
                b.addActionListener(this);
                b.setName(Integer.toString(board.getPos(jj, 7-ii)));
            }
        }

        /*
         * fill the chess board
         */
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                    SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (9-(ii + 1)),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = ((JButton)e.getSource());
		if(b.getBackground() == highlightColor){
			Move move = new Move(lastPieceHighlighted, Integer.parseInt(b.getName()), board.pieceAt(Integer.parseInt(b.getName())));
			if(game.colorToMove == Piece.BLACK){ //actually reversed, so if we are currently playing for white
				((HumanPlayer)game.whitePlayer).moveToMake=move;
			}else{
				((HumanPlayer)game.blackPlayer).moveToMake=move;
			}
		}
		resetHighlight();
		int pos = Integer.parseInt(((JButton)e.getSource()).getName());
		Piece p = board.pieceAt(pos);
		if(p != null && game.waitingForPlayerInput && game.colorToMove != p.color){
			highlightMoves(board,p);
		}
	}

    public final JComponent getGui() {
        return gui;
    }

    private final void createImages() {
        try {
            //URL url = new URL("http://i.stack.imgur.com/memI0.png");
            URL url = getClass().getResource("/pieces.png");
            BufferedImage bi = ImageIO.read(url);
            for (int ii = 0; ii < 2; ii++) {
                for (int jj = 0; jj < 6; jj++) {
                    chessPieceImages[ii][jj] = bi.getSubimage(
                            jj * 64, ii * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the icons of the initial chess board piece places
     */
    /*private final void setupNewGame() {
        message.setText("Make your move!");
        // set up the black pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][0].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][STARTING_ROW[ii]]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][1].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][PAWN]));
        }
        // set up the white pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][6].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][PAWN]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][7].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][STARTING_ROW[ii]]));
        }
    }*/
    
    public void drawBoard(Board b){
    	resetHighlight();
    	for(int i=0;i<8;i++){
    		for(int ii=0;ii<8;ii++){
    			chessBoardSquares[i][ii].setIcon(null);
    		}
    	}
    	for(Piece p : b.pieces){
    		if(p.alive){
    			if(p.color == Piece.BLACK){
        			chessBoardSquares[b.getCol(p.pos)][7-b.getRow(p.pos)].setIcon(new ImageIcon(chessPieceImages[BLACK][p.pieceID]));
    			}else{
        			chessBoardSquares[b.getCol(p.pos)][7-b.getRow(p.pos)].setIcon(new ImageIcon(chessPieceImages[WHITE][p.pieceID]));
    			}
    		}
    	}
    }
    public void resetHighlight(){
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b=chessBoardSquares[jj][ii];
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
            }
        }

    }
    public void highlightMoves(Board b, Piece p){
    	lastPieceHighlighted = p;
    	ArrayList<Move> moves = p.getPossibleLocs();
    	moves = b.validateMoves(moves);
    	
    	for(Move m : moves){
    		chessBoardSquares[b.getCol(m.moveTo)][7-b.getRow(m.moveTo)].setBackground(highlightColor);
    	}
    }
}