package bot;

import Data_storage.Ball;
import Data_storage.GameState;
import Data_storage.Vector2;

import java.util.ArrayList;

public class HillClimbingBot implements Bot {

    private final Heuristic heuristic;
    private final double learningRate;

    public HillClimbingBot(Heuristic heuristic, double learningRate) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        gameState = gameState.copy();
        Vector2 bestShot = new Vector2((Math.random()*2-1)*Ball.maxSpeed, (Math.random()*2-1)*Ball.maxSpeed);//Vector2.zeroVector.copy();
        bestShot = bestShot.normalized().scale(Math.random()*Ball.maxSpeed);
        double bestHeuristicVal = 0;
        //Thread[] threads = new Thread[5];
        //double[] distances = new double[threads.length];
        //Vector2[] velocities = new Vector2[threads.length];
        boolean bestShotUpdated = true;

        // Initial heuristic calculation
        ArrayList<Vector2> positions = gameState.simulateShot(bestShot);
        bestHeuristicVal = heuristic.getShotValue(positions, gameState);


        while (bestShotUpdated) {
            bestShotUpdated = false;
            for (double degree = 0; degree < 360; degree += 45) {

                Vector2 changeVector = new Vector2(Math.cos(degree * Math.PI / 180) * learningRate, Math.sin(degree * Math.PI / 180) * learningRate);

                Vector2 velocity = bestShot.translate(changeVector);

                if (velocity.length() <= Ball.maxSpeed) {
                    positions = gameState.simulateShot(velocity);

                    double heuristicVal = heuristic.getShotValue(positions, gameState);

                    if (heuristic.firstBetterThanSecond(heuristicVal, bestHeuristicVal)) {
                        bestShot = velocity.copy();
                        bestHeuristicVal = heuristicVal;
                        bestShotUpdated = true;
                    }
                }
            }
        }

        System.out.println("v(x)="+bestShot.x+" v(y)="+bestShot.y);
        System.out.println("Best Distance:  "+bestHeuristicVal);
        return bestShot;
    }
}
