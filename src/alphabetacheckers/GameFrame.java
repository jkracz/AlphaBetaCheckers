/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author josephkracz
 */
public class GameFrame {
    // initialize AI player and 2 booleans to prevent interruption while AI makes turn
    private AIPlayer robo;
    private boolean AIThinking;
    private boolean AIMoveDone;

    //frame and the panel that will determine layout
    private final JFrame jf;
    private final JPanel columnPanel;
    
    // tool bar options
    private final JToolBar menu;
    private final JButton newGame;
    private final JLabel difficulty;
    private final JComboBox difficultySelector;
    private final JLabel firstMove;
    private final JComboBox firstMoveSelector;
    
    // AI output area
    private final JPanel outputPanel;
    private final JLabel outputLabel;
    private final JTextArea outputArea;
    private final JScrollPane scroller;
    
    // checker board objects
    private JPanel checkerPanel;
    private CheckerBoard mainBoard;
    
    // variables that are used every turn
    private String whoseTurn;
    private final ArrayList<CheckerSquare> highlightedSquares; // highlighted square holder
    private CheckerSquare cs; // last clicked square
    Map<CheckerSquare, ArrayList<Move>> activeMoves; // holds moveset for active player
    boolean jumpPossible; // determines if jumping is possible
    private int turnsWithoutMoves;
    private String winner;
    private int turnCounter;
    
    
    public GameFrame() {
        jf = new JFrame("AB Checkers");
        jf.setSize(700,700);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // panel for the master layout of the GUI
        columnPanel = new JPanel(new BorderLayout());        
        
        // toolbar initializer
        menu = new JToolBar();
        menu.setFloatable(false);
        newGame = new JButton("Start New Game");
        newGame.addActionListener((java.awt.event.ActionEvent evt) -> {
            newGameActionPerformed(evt);
        });
        
        difficulty = new JLabel("Difficulty:");
        difficultySelector = new JComboBox();
        difficultySelector.addItem("Easy");
        difficultySelector.addItem("Medium");
        difficultySelector.addItem("Hard");
        firstMove = new JLabel("First Move:");
        firstMoveSelector = new JComboBox();
        firstMoveSelector.addItem("Player");
        firstMoveSelector.addItem("A.I.");
        
        menu.add(firstMove);
        menu.add(firstMoveSelector);
        menu.add(difficulty);
        menu.add(difficultySelector);
        menu.add(newGame);
        
        // adding tool bar to layout panel
        columnPanel.add(menu,BorderLayout.NORTH);
        jf.add(columnPanel);
        
        // initialize AI output area
        outputPanel = new JPanel(new BorderLayout());
        outputLabel = new JLabel("Output");
        outputArea = new JTextArea(7,20);
        outputArea.setEditable(false);
        outputArea.setAutoscrolls(true);
        scroller = new JScrollPane(outputArea);
        scroller.setAutoscrolls(true);
        
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(scroller, BorderLayout.CENTER);
        
        // add output panel, and a holder panel in place of the checker board for the moment
        columnPanel.add(outputPanel, BorderLayout.SOUTH);
        checkerPanel = new JPanel();
        columnPanel.add(checkerPanel, BorderLayout.CENTER);
        jf.setVisible(true);

        // initialize a single array to be used throughout program
        highlightedSquares = new ArrayList<>();
        
    }
    
    
    public String getWhoseTurn() {
        if (winner == null) {return whoseTurn;}
        else {return "game over";}
    }
    
    private void newBoard() {
        checkerPanel = new JPanel(new GridLayout(6,6));
        checkerPanel.setSize(new Dimension(600,550));
        
        CheckerSquare[][] squares = new CheckerSquare[6][6];
        int redPieces = 0;
        int blackPieces = 0;
        
        // gets the red player's icon and resize it for use
        ImageIcon tempIcon = new ImageIcon(getClass().getResource("/resources/red.png"));
        Image tempImg = tempIcon.getImage();
        Image scaled = tempImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon redPiece = new ImageIcon(scaled);
        
        // gets the black player's icon and resize it for use
        tempIcon = new ImageIcon(getClass().getResource("/resources/black.png"));
        tempImg = tempIcon.getImage();
        scaled = tempImg.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon blackPiece = new ImageIcon(scaled);
        
        Color holder;
        CheckerSquare tempButton;
        for (int i = 0; i < 6; ++i) {
            if (i % 2 == 0) {holder = Color.BLACK;}
            else {holder = Color.RED;}
            
            for (int j = 0; j < 6; ++j) {
                tempButton = new CheckerSquare(j,i);
                // sets background of square
                tempButton.setBackground(holder);
                
                // logic used to set pieces on board
                if (i < 2 && (j+i) % 2 == 1) {
                    tempButton.setIcon(redPiece);
                    tempButton.pieceOn = "red";
                    ++redPieces;
                }
                else if (i > 3 && (j+i) % 2 == 1) {
                    tempButton.setIcon(blackPiece);
                    tempButton.pieceOn = "black";
                    ++blackPieces;
                }
                else {tempButton.pieceOn = null;}
                // add functionality to square
                tempButton.addActionListener((java.awt.event.ActionEvent evt) -> {
                    squareClickActionPerformed(evt);
                });
                // add button to board, then keep track of button in matrix
                checkerPanel.add(tempButton);
                squares[j][i] = tempButton;
                
                if (holder.equals(Color.RED)) {holder = Color.BLACK;}
                else {holder = Color.RED;}
            }
        }
        mainBoard = new CheckerBoard(squares, redPieces, blackPieces);
        
        columnPanel.add(checkerPanel,BorderLayout.CENTER);
        
        jf.revalidate();
        jf.repaint();
    }
    
    
    // adds functionality to the "Start New Game" button
    private void newGameActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // remove current board on the layout panel and add a new checker game
        columnPanel.remove(checkerPanel);
        newBoard();
        
        // get who is going first from combo box
        String fm = (String) firstMoveSelector.getSelectedItem();
        
        // moveset of the player going first is calculated
        if (fm.equals("A.I.")) {
            whoseTurn = "red";
            activeMoves = AIMoves(mainBoard);
        }
        else {
            AIThinking = false;
            AIMoveDone = true;
            whoseTurn = "black";
            activeMoves = playerMoves(mainBoard);
        }
        winner = null;
        turnCounter = 1;
        outputArea.setText("");
        robo = new AIPlayer();
    }
    
    // functionality for square clicks
    public void squareClickActionPerformed(java.awt.event.ActionEvent evt) {
        CheckerSquare temp = (CheckerSquare) evt.getSource();
        
        // if a square is highlighted i.e. is a possible move
        if (temp.highlighted && !highlightedSquares.isEmpty() && winner == null && !AIThinking) {
            
            if (cs != null) {
                for (Move m : activeMoves.get(cs)) {
                    if (m.target == temp && m.src == cs) {
                        mainBoard.movePiece(m);
                        break;
                    }
                }
            }
            else {
                outterloop:
                for (CheckerSquare i : activeMoves.keySet()) {
                    for (Move j : activeMoves.get(i)) {
                        if (j.target == temp) {
                            mainBoard.movePiece(j);
                            break outterloop;
                        }
                    }
                }
            }
            jf.revalidate();
            jf.repaint();
            
            endTurn();
        }
        
        // if a new square is being selected and it is not a highlighted square
        else if (temp.pieceOn != null && temp.pieceOn.equals(whoseTurn) && !jumpPossible &&
                winner == null && !activeMoves.isEmpty() && !AIThinking) {
            if (!highlightedSquares.isEmpty()) {
                for (CheckerSquare i : highlightedSquares) {
                    i.highlight();
                }
                highlightedSquares.clear();
            }
            
            if (activeMoves.containsKey(temp)) {
                for (Move j : activeMoves.get(temp)) {
                    j.target.highlight();
                    highlightedSquares.add(j.target);
                }
            }
            // hold recently clicked square for future use
            cs = temp;
            jf.revalidate();
            jf.repaint();
        }
    }
    
    // function that initiates AI actions
    public void roboGo() {
        if (whoseTurn != null && whoseTurn.equals("red") && AIMoveDone && winner == null) {
            AIThinking = true;
            AIMoveDone = false;
            Move AIMove = robo.generateMove(mainBoard);
            outputArea.setText(outputArea.getText() + "\n \nTurn: " + turnCounter + 
                    "\nTree Depth=" + robo.tDepth()+
                    "\nNodes Generated=" + robo.nodes() + 
                    "\nNumber of Times Max Pruning Occurred=" + robo.maxPrune()
                    + "\nNumber of Times Min Pruning Occurred=" + robo.minPrune());
            AIThinking = false;
            
            mainBoard.getBoard()[AIMove.src.x][AIMove.src.y].doClick();
            long cutOffTime = System.currentTimeMillis() + 500;
            while (System.currentTimeMillis() < cutOffTime) {
                //busy waiting so that the player can see the AI make the move
            }
            
            mainBoard.getBoard()[AIMove.target.x][AIMove.target.y].doClick();
        }
    }
    
    // prints board to assist in debugging
    private void printBrd(CheckerSquare[][] board) {
        String line;
        for (int i = 0; i < 6; ++i) {
            line = "";
            for (int j = 0; j <6; ++j) {
                if (board[j][i].pieceOn != null && board[j][i].pieceOn.equals("black")) {
                    line += "B "; 
                }
                else if (board[j][i].pieceOn != null && board[j][i].pieceOn.equals("red")) {
                    line += "R ";
                }
                else {
                    line += "o ";
                }
            }
            System.out.println(line);
        }
    }
    
    
    private void endGame(String p) {
        if (p.equals("Draw")) {outputArea.setText(outputArea.getText()+"\n"+"Draw! Start a new game.");}
        else {outputArea.setText(outputArea.getText()+"\n \n"+p+" has won the game! Start a new game.");}
        winner = p;
        whoseTurn = "game over";
    }
    
    // switches whose turn it is
    private void endTurn() {
        // clearing all highlighted move squares
        for (CheckerSquare t : highlightedSquares) {
            t.highlight();
        }
        highlightedSquares.clear();
        cs = null;
        
        // change move to next active player and calculate their moveset
        if (whoseTurn.equals("black")) {
            whoseTurn = "red";
            activeMoves = AIMoves(mainBoard);
        } else {
            AIMoveDone = true;
            whoseTurn = "black";
            activeMoves = playerMoves(mainBoard);
        }
        
        // if one player has no pieces left, end the game
        if (mainBoard.getRP() == 0 && winner == null) {
            endGame("Player");
        } else if (mainBoard.getBP() == 0 && winner == null) {
            endGame("A.I.");
        }
        
        // used to help keep track of when a player has no moves left
        if (activeMoves.isEmpty() && winner == null) {
            turnsWithoutMoves++;
            if (turnsWithoutMoves > 1) {
                if (mainBoard.getRP() > mainBoard.getBP()) {
                    endGame("A.I.");
                } else if (mainBoard.getRP() < mainBoard.getBP()) {
                    endGame("Player");
                } else if (mainBoard.getRP() == mainBoard.getBP()) {
                    endGame("Draw");
                } else {endTurn();}
            }
            else{
                endTurn();
            }
        }
        else {turnsWithoutMoves = 0;}
        ++turnCounter;
        jf.revalidate();
        jf.repaint();
    }
    
    // computes all legal moves for the player
    private Map<CheckerSquare, ArrayList<Move>> playerMoves(CheckerBoard bo) {
        Map<CheckerSquare, ArrayList<Move>> playerLegalMoves = new ConcurrentHashMap<>();
        CheckerSquare[][] board = bo.getBoard();
        
        // temporary variables to hold squares and their moves
        CheckerSquare c;
        ArrayList<Move> moves;
        
        jumpPossible = false;
        Map<CheckerSquare, ArrayList<Move>> jumpMoves = new ConcurrentHashMap<>();
        ArrayList<Move> possibleJumps;
        
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++j) {
               c = board[i][j];
               if (c.pieceOn != null && c.pieceOn.equals("black")) {
                   moves = new ArrayList<>();
                   possibleJumps = new ArrayList<>();
                   if (c.x + 1 < 6 && c.y - 1 >= 0) {
                       if (board[c.x + 1][c.y - 1].pieceOn == null) {
                           moves.add(new Move(c, board[c.x + 1][c.y - 1], null, bo.getRP(), bo.getBP()));
                       } else if (board[c.x + 1][c.y - 1].pieceOn.equals("red") && c.x + 2 < 6 && c.y - 2 >= 0
                               && board[c.x + 2][c.y - 2].pieceOn == null) {
                           moves.add(new Move(c, board[c.x + 2][c.y - 2], board[c.x + 1][c.y - 1], bo.getRP(), bo.getBP()));
                           jumpPossible = true;
                           possibleJumps.add(new Move(c, board[c.x + 2][c.y - 2], board[c.x + 1][c.y - 1], bo.getRP(), bo.getBP()));
                       }
                   }
                   if (c.x - 1 >= 0 && c.y - 1 >= 0) {
                       if (board[c.x - 1][c.y - 1].pieceOn == null) {
                           moves.add(new Move(c, board[c.x - 1][c.y - 1], null, bo.getRP(), bo.getBP()));
                       } else if (board[c.x - 1][c.y - 1].pieceOn.equals("red") && c.x - 2 >= 0 && c.y - 2 >= 0
                               && board[c.x - 2][c.y - 2].pieceOn == null) {
                           moves.add(new Move(c, board[c.x - 2][c.y - 2], board[c.x - 1][c.y - 1], bo.getRP(), bo.getBP()));
                           jumpPossible = true;
                           possibleJumps.add(new Move(c, board[c.x - 2][c.y - 2], board[c.x - 1][c.y - 1], bo.getRP(), bo.getBP()));
                       }
                   }
                   if (!moves.isEmpty()) {playerLegalMoves.put(c, moves);}
                   if (!possibleJumps.isEmpty()) {
                       jumpMoves.put(c, possibleJumps);
                   }
               }
            }
        }
        
        // if jump moves are possible, it will highlight them and return the jump moves set
        if (!jumpMoves.isEmpty()) {
            ArrayList<Move> movs;
            for (CheckerSquare i : jumpMoves.keySet()) {
                movs = jumpMoves.get(i);
                for (Move j : movs) {
                    highlightedSquares.add(j.target);
                    j.target.highlight();
                }
            }
            return jumpMoves;
        }
        
        return playerLegalMoves;
    }
    
    // computes all legal moves for the AI
    private Map<CheckerSquare, ArrayList<Move>> AIMoves(CheckerBoard bo) {
        Map<CheckerSquare, ArrayList<Move>> AILegalMoves = new ConcurrentHashMap<>();
        CheckerSquare[][] board = bo.getBoard();

        // temporary variables to hold squares and their moves
        CheckerSquare c;
        ArrayList<Move> moves;
        jumpPossible = false;
        Map<CheckerSquare, ArrayList<Move>> jumpMoves = new ConcurrentHashMap<>();
        ArrayList<Move> possibleJumps;

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++j) {
                c = board[i][j];
                if (c.pieceOn != null && c.pieceOn.equals("red")) {
                    moves = new ArrayList<>();
                    possibleJumps = new ArrayList<>();
                    if (c.x + 1 < 6 && c.y + 1 < 6) {
                        if (board[c.x + 1][c.y + 1].pieceOn == null) {
                            moves.add(new Move(c, board[c.x + 1][c.y + 1], null, bo.getRP(), bo.getBP()));
                        } else if (board[c.x + 1][c.y + 1].pieceOn.equals("black") && c.x + 2 < 6 && c.y + 2 < 6
                                && board[c.x + 2][c.y + 2].pieceOn == null) {
                            moves.add(new Move(c, board[c.x + 2][c.y + 2], board[c.x + 1][c.y + 1], bo.getRP(), bo.getBP()));
                            jumpPossible = true;
                            possibleJumps.add(new Move(c, board[c.x + 2][c.y + 2], board[c.x + 1][c.y + 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (c.x - 1 >= 0 && c.y + 1 < 6) {
                        if (board[c.x - 1][c.y + 1].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 1][c.y + 1], null, bo.getRP(), bo.getBP()));
                        } else if (board[c.x - 1][c.y + 1].pieceOn.equals("black") && c.x - 2 >= 0 && c.y + 2 < 6
                                && board[c.x - 2][c.y + 2].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 2][c.y + 2], board[c.x - 1][c.y + 1], bo.getRP(), bo.getBP()));
                            jumpPossible = true;
                            possibleJumps.add(new Move(c, board[c.x - 2][c.y + 2], board[c.x - 1][c.y + 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (!moves.isEmpty()) {AILegalMoves.put(c, moves);}
                    if (!possibleJumps.isEmpty()) {
                        jumpMoves.put(c, possibleJumps);
                    }
                }
            }
        }
        
        // if jump moves are possible, it will highlight them and return the jump moves set
        if (!jumpMoves.isEmpty()) {
            ArrayList<Move> movs;
            for (CheckerSquare i : jumpMoves.keySet()) {
                movs = jumpMoves.get(i);
                for (Move j : movs) {
                    if (j.jump != null) {
                        highlightedSquares.add(j.target);
                        j.target.highlight();
                    }
                }
            }
            return jumpMoves;
        }
        
        return AILegalMoves;
    }
}
