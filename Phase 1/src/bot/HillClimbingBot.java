package bot;

import Data_storage.Ball;
import Data_storage.GameState;
import Data_storage.Vector2;

import java.util.ArrayList;

public class HillClimbingBot implements Bot {

    private final Heuristic heuristic;
    private final double learningRate;
    private final int numNeighbours;

    public HillClimbingBot(Heuristic heuristic, double learningRate, int numNeighbours) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
        this.numNeighbours = numNeighbours;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        System.out.println("Calculating shot...");
        gameState = gameState.copy();
        Vector2 bestShot = new Vector2(Math.random()*2-1, Math.random()*2-1);
        bestShot = bestShot.normalized().scale(Math.random()*Ball.maxSpeed);
        boolean bestShotUpdated = true;

        // Initial heuristic calculation
        ArrayList<Vector2> positions = gameState.simulateShot(bestShot);
        double bestHeuristicVal = heuristic.getShotValue(positions, gameState);


        while (bestShotUpdated) {
            bestShotUpdated = false;
            Vector2 tempBestShot = bestShot.copy();

            for (double degree = 0; degree < 360; degree += 360.0/numNeighbours) {

                Vector2 changeVector = new Vector2(Math.cos(degree * Math.PI / 180), Math.sin(degree * Math.PI / 180)).scale(learningRate);

                Vector2 velocity = bestShot.copy().translate(changeVector);

                if (velocity.length() > Ball.maxSpeed) {
                    velocity = velocity.normalized().scale(Ball.maxSpeed);
                }

                positions = gameState.simulateShot(velocity);

                double heuristicVal = heuristic.getShotValue(positions, gameState);

                if (heuristic.firstBetterThanSecond(heuristicVal, bestHeuristicVal)) {
                    tempBestShot = velocity.copy();
                    bestHeuristicVal = heuristicVal;
                    bestShotUpdated = true;
                }
            }

            bestShot = tempBestShot;
        }

        //System.out.println("v(x)="+bestShot.x+" v(y)="+bestShot.y);
        //System.out.println("Best Distance:  "+bestHeuristicVal);
        return bestShot;
    }
}
