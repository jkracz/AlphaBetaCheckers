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
// keeps track of CheckerPiece matrix and how many pieces are left
public class CheckerBoard {
    private int redPieces;
    private int blackPieces;
    private CheckerSquare[][] board;
    
    CheckerBoard(CheckerSquare[][] b, int rp, int bp) {
        redPieces = rp;
        blackPieces = bp;
        board = b;
    }
    
    CheckerBoard(CheckerBoard b) {
        redPieces = b.getRP();
        blackPieces = b.getBP();
        CheckerSquare[][] toBeCopied = b.getBoard();
        
        board = new CheckerSquare[6][6];
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++j) {
                board[j][i] = new CheckerSquare(toBeCopied[j][i]);
            }
        }
    }
    
    public void movePiece(Move m) {
        m.target.pieceOn = m.src.pieceOn;
        m.target.setIcon(m.src.getIcon());

        // removing piece from source
        m.src.pieceOn = null;
        m.src.setIcon(null);

        // remove jump piece if it exists
        if (m.jump != null) {
            if (m.jump.pieceOn.equals("red")) {
                redPieces = redPieces-1;
            }
            else if (m.jump.pieceOn.equals("black")) {
                blackPieces = blackPieces-1;
            }
            m.jump.pieceOn = null;
            m.jump.setIcon(null);
        }
    }
    
    public CheckerSquare[][] getBoard() {return board;}
    public int getRP() {return redPieces;}
    public int getBP() {return blackPieces;}
}
