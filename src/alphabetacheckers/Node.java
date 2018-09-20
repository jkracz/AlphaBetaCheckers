/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author josephkracz
 */

// Node class that makes up a move tree
public class Node {
    // game state info
    private final CheckerBoard currState;
    
    // player specific info
    private final Map<CheckerSquare, ArrayList<Move>> potentialMoves;
    private final String whoseTurn;
    
    int utilityValue;
    
    // standard tree info
    private final Node parent;
    private final ArrayList<Node> children;
    
    // holds last move made that yielded the current board state of the node
    private final Move lastMoveMade;

    public Node(Node p, CheckerBoard st, Map<CheckerSquare, ArrayList<Move>> pm, String wt, Move m) {
        currState = st;
        potentialMoves = pm;
        children = new ArrayList<>();
        parent = p;
        whoseTurn = wt;
        lastMoveMade = m;
    }
    
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
    
    public void setUtility(int v) {
        utilityValue = v;
    }
    public int getUtility() {
        return utilityValue;
    }
    
    public ArrayList<Node> getChildren() {
        return children;
    }
    
    public void addChild(Node c) {
        children.add(c);
    }
    
    public Move getLastMove() {
        return lastMoveMade;
    }
    
    public String whoseTurn() {return whoseTurn;}
    
    public Map<CheckerSquare, ArrayList<Move>> getMoves() {return potentialMoves;}
    
    public CheckerBoard getState() {return currState;}
}
