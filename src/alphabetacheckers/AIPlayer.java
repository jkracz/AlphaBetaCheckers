/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author josephkracz
 */
public class AIPlayer {
    
    // holds all information from the most recent tree to be used in the output section on the GUI
    private int lastTreeDepth;
    private int lastNodesGenerated;
    private int lastMaxPrune;
    private int lastMinPrune;
    
    
    public AIPlayer() {
        lastMinPrune = 0;
        lastMaxPrune = 0;
        lastTreeDepth = 0;
        lastNodesGenerated = 0;
    }
    
    public int minPrune() {return lastMinPrune;}
    public int maxPrune() {return lastMaxPrune;}
    public int tDepth() {return lastTreeDepth;}
    public int nodes() {return lastNodesGenerated;}
    
    // returns a move for use against the player
    public Move generateMove(CheckerBoard currBoard) {
        
        Tree moveTree = buildTree(new CheckerBoard(currBoard));

        lastTreeDepth = moveTree.getDepth();
        lastNodesGenerated = moveTree.getNodesGen(); 
        return alphabeta(moveTree);
    }
    
    // AB function that begins recursion
    private Move alphabeta (Tree t) {
        lastMinPrune = 0;
        lastMaxPrune = 0;
        
        int maxUtility;
        Move maxUtilityMove = null;
        
        // goes through and sets maxUtilityMove to random move
        // it serves to initialize maxUtilityMove, and act a failsafe. the value assign should never be used
        for (CheckerSquare c : t.getRoot().getMoves().keySet()) {
            for (Move m : t.getRoot().getMoves().get(c)) {
                maxUtilityMove = m;
            }
        }
        
        if (t.getRoot().getChildren().size() <= 1) {
            for (CheckerSquare c : t.getRoot().getMoves().keySet()) {
                for (Move m : t.getRoot().getMoves().get(c)) {
                    maxUtilityMove = m;
                    return maxUtilityMove;
                }
            }
        }
        else {
            maxUtility = maxValue(t.getRoot(), t.getDepth(), -100, 100);
            for (CheckerSquare c : t.getRoot().getMoves().keySet()) {
                for (Move m : t.getRoot().getMoves().get(c)) {
                    if (m.getUtility() == maxUtility) {
                        maxUtilityMove = m;
                        return maxUtilityMove;
                    }
                }
            }
        }
        return maxUtilityMove;
    }
    
    // max value function for AB search
    private int maxValue(Node n, int depth, int alpha, int beta) {
        if (depth <= 0 || !n.hasChildren()) {
            return (n.getLastMove().getUtility());
        }
        n.utilityValue = -100;
        
        for (Node i : n.getChildren()) {
            n.utilityValue = Integer.max(n.utilityValue, minValue(i, --depth, alpha, beta));
            if (n.utilityValue >= beta) {
                ++lastMaxPrune;
                return n.utilityValue;
            }
            alpha = Integer.max(alpha, n.utilityValue);
        }
        return n.utilityValue;
        
    }
    // min value function for AB search
    private int minValue(Node n, int depth, int alpha, int beta) {
        if (depth <= 0 || !n.hasChildren()) {
            return (n.getLastMove().getUtility());
        }
        n.utilityValue = 100;
        
        for (Node i : n.getChildren()) {
            n.utilityValue = Integer.min(n.utilityValue, maxValue(i, --depth, alpha, beta));
            if (n.utilityValue <= alpha) {
                ++lastMinPrune;
                return n.utilityValue;
            }
            beta = Integer.min(beta, n.utilityValue);
        }
        return n.utilityValue;
    }
  
    // prints board to assist in debugging
    private void printBrd(CheckerSquare[][] board) {
        String line;
        for (int i = 0; i < 6; ++i) {
            line = "";
            for (int j = 0; j < 6; ++j) {
                if (board[j][i].pieceOn != null && board[j][i].pieceOn.equals("black")) {
                    line += "B ";
                } else if (board[j][i].pieceOn != null && board[j][i].pieceOn.equals("red")) {
                    line += "R ";
                } else {
                    line += "o ";
                }
            }
            System.out.println(line);
        }
        System.out.println("");
    }
    
    private Tree buildTree(CheckerBoard currState) {
        Map<CheckerSquare, ArrayList<Move>> moves = computeAIMoves(currState);
        ArrayList<Move> pieceSpecificMoves;
        Tree t = new Tree(currState, moves);
        
        int depth = 0;
        int nodesGenerated = 1;
        
        // if there is only one move to be made, it won't go through the trouble of building the tree
        if (moves.size() == 1) {
            lastTreeDepth = depth;
            lastNodesGenerated = nodesGenerated;
            return t;
        }
        
        Node nillNode = new Node(null, null, null, null, null); // used to denote end of a depth level
        Node looper; // used to traverse the tree, but shouldn't be modified
        
        Queue<Node> q = new LinkedList<>();
        q.add(t.getRoot());
        q.add(nillNode);
        
        Node newChild;
        CheckerBoard replicant;
        
        // set cutoff time for tree building to be 12 seconds
        long cutOffTime = System.currentTimeMillis() + 12000;
        while (System.currentTimeMillis() < cutOffTime && q.peek() != null) {
            
            looper = q.remove();
            if (looper.getState() == null) {
                ++depth;
            }
            else {
                moves = looper.getMoves();
                for (CheckerSquare c : moves.keySet()) {
                    pieceSpecificMoves = moves.get(c);
                    for (Move m : pieceSpecificMoves) {
                        replicant = new CheckerBoard(looper.getState());
                        
                        replicant.movePiece(new Move(replicant.getBoard(), m));
                        
                        if (looper.whoseTurn().equals("red")) {
                            newChild = new Node(looper, replicant, computeOpponentMoves(replicant), "black", m);
                            looper.addChild(newChild);
                            q.add(newChild);
                            ++nodesGenerated;
                        }
                        else {
                            newChild = new Node(looper, replicant, computeAIMoves(replicant), "red", m);
                            looper.addChild(newChild);
                            q.add(newChild);
                            ++nodesGenerated;
                        }
                    }
                }
                q.add(nillNode);
            }
        }
        t.setDepth(depth);
        t.setNodesGen(nodesGenerated);
        lastTreeDepth = depth;
        lastNodesGenerated = nodesGenerated;
        return t;
    }
    
    // generates moves of the opponent; nearly identical to the method in GameFrame.java
    private Map<CheckerSquare, ArrayList<Move>> computeOpponentMoves(CheckerBoard bo) {
        CheckerSquare[][] board = bo.getBoard();
        
        Map<CheckerSquare, ArrayList<Move>> playerLegalMoves = new ConcurrentHashMap<>();

        // temporary variables to hold squares and their moves
        CheckerSquare c;
        ArrayList<Move> moves;

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
                            possibleJumps.add(new Move(c, board[c.x + 2][c.y - 2], board[c.x + 1][c.y - 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (c.x - 1 >= 0 && c.y - 1 >= 0) {
                        if (board[c.x - 1][c.y - 1].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 1][c.y - 1], null, bo.getRP(), bo.getBP()));
                        } else if (board[c.x - 1][c.y - 1].pieceOn.equals("red") && c.x - 2 >= 0 && c.y - 2 >= 0
                                && board[c.x - 2][c.y - 2].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 2][c.y - 2], board[c.x - 1][c.y - 1], bo.getRP(), bo.getBP()));
                            possibleJumps.add(new Move(c, board[c.x - 2][c.y - 2], board[c.x - 1][c.y - 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (!moves.isEmpty()) {
                        playerLegalMoves.put(c, moves);
                    }
                    if (!possibleJumps.isEmpty()) {
                        jumpMoves.put(c, possibleJumps);
                    }
                }
            }
        }

        if (!jumpMoves.isEmpty()) {
            return jumpMoves;
        } else {return playerLegalMoves;}
    }
    
    // generates possible moves for the AI; nearly identical to the method in GameFrame.java
    private Map<CheckerSquare, ArrayList<Move>> computeAIMoves(CheckerBoard bo) {
        Map<CheckerSquare, ArrayList<Move>> AILegalMoves = new ConcurrentHashMap<>();
        CheckerSquare[][] board = bo.getBoard();

        // temporary variables to hold squares and their moves
        CheckerSquare c;
        ArrayList<Move> moves;
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
                            possibleJumps.add(new Move(c, board[c.x + 2][c.y + 2], board[c.x + 1][c.y + 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (c.x - 1 >= 0 && c.y + 1 < 6) {
                        if (board[c.x - 1][c.y + 1].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 1][c.y + 1], null, bo.getRP(), bo.getBP()));
                        } else if (board[c.x - 1][c.y + 1].pieceOn.equals("black") && c.x - 2 >= 0 && c.y + 2 < 6
                                && board[c.x - 2][c.y + 2].pieceOn == null) {
                            moves.add(new Move(c, board[c.x - 2][c.y + 2], board[c.x - 1][c.y + 1], bo.getRP(), bo.getBP()));
                            possibleJumps.add(new Move(c, board[c.x - 2][c.y + 2], board[c.x - 1][c.y + 1], bo.getRP(), bo.getBP()));
                        }
                    }
                    if (!moves.isEmpty()) {
                        AILegalMoves.put(c, moves);
                    }
                    if (!possibleJumps.isEmpty()) {
                        jumpMoves.put(c, possibleJumps);
                    }
                }
            }
        }

        if (!jumpMoves.isEmpty()) {
            return jumpMoves;
        } else {return AILegalMoves;}
    }

}