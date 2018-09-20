/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

/**
 *
 * @author josephkracz
 */

public class Move {
    // move  utility value
    private int utility;
    
    // keeps track of the source, target, and jump squares
    CheckerSquare src;
    CheckerSquare jump;
    CheckerSquare target;
    
    Move(CheckerSquare s, CheckerSquare t, CheckerSquare j, int r, int b) {
        src = s;
        target = t;
        jump = j;
        
        if (jump != null) {
            if (jump.pieceOn.equals("red")) {
                utility = (r-1)-(b+10);
            }
            else if (jump.pieceOn.equals("black")) {
                utility = (r+10)-(b-1);
            }
        }
        else {utility = r - b;}
    }
    
    // copy constructor
    Move(Move m) {
        src = new CheckerSquare(m.src);
        target = new CheckerSquare(m.target);
        jump = new CheckerSquare(m.jump);
        utility = m.utility;
    }
    
    // used to replicate a move for a copied board
    Move(CheckerSquare[][] brd, Move m) {
        src = brd[m.src.x][m.src.y];
        target = brd[m.target.x][m.target.y];
        if (m.jump != null) {
            jump = brd[m.jump.x][m.jump.y];
        } else {
            jump = null;
        }
        utility = m.utility;
    }
    
    public int getUtility() {return utility;}
}
