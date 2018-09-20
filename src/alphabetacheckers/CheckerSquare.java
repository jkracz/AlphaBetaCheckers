/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

/**
 *
 * @author josephkracz
 */
public class CheckerSquare extends JButton {
    int x;
    int y;
    
    boolean highlighted;
    String pieceOn;

    public CheckerSquare(int xCoordinate, int yCoordinate) {
        super();
        x = xCoordinate;
        y = yCoordinate;
        highlighted = false;
        
        setOpaque(true);
        setBorderPainted(false);
    }
    
    CheckerSquare(CheckerSquare c) {
        super();
        x = c.x;
        y = c.y;
        highlighted = c.highlighted;
        pieceOn = c.pieceOn;
        this.setIcon(c.getIcon());
        this.setBackground(c.getBackground());
        setOpaque(true);
        setBorderPainted(false);
        
    }
    
    public void highlight() {
        if (highlighted) {
            setBorder(null);
            setBorderPainted(false);
            highlighted = false;
        }
        else {
            setBorderPainted(true);
            setBorder(new LineBorder(java.awt.Color.WHITE, 2));
            highlighted = true;
        }
    }
    
    
}


