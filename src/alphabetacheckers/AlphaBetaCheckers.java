/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabetacheckers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author josephkracz
 */
public class AlphaBetaCheckers {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // main function only actually exists to initialize the frame
       
        GameFrame gf = new GameFrame();
        
        // checks to see whose turn it is every 2 seconds. If it is the robot's turn, it will go
        ScheduledExecutorService roboDriver = Executors.newScheduledThreadPool(5);
        roboDriver.scheduleAtFixedRate(() -> {
            String whoseTurn = gf.getWhoseTurn();
            if (whoseTurn != null && whoseTurn.equals("red")) {
                gf.roboGo();
            }
        }, 0, 2, TimeUnit.SECONDS);
        
    }
    
}
