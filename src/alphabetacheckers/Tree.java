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
public class Tree {
    private final Node root;
    private int treeDepth;
    private int nodesGenerated;

    public Tree(CheckerBoard currState, Map<CheckerSquare, ArrayList<Move>> rootMoves) {
        root = new Node(null, currState, rootMoves, "red", null);
    }
    
    public void setDepth(int d) {treeDepth = d;}
    public int getDepth() {return treeDepth;}
    
    public void setNodesGen(int g) {nodesGenerated = g;}
    public int getNodesGen() {return nodesGenerated;}
    
    public Node getRoot() {return root;}

}
