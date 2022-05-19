package bot.botimplementations;

import java.util.ArrayList;
import java.util.Random;

import bot.heuristics.Heuristic;
import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;

public class RandomBot implements IBot {

    private final int numShots;
    private final Heuristic heuristic;

    public RandomBot(Heuristic heuristic, int numShots) {
        this.numShots = numShots;
        this.heuristic = heuristic;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        Vector2 bestShot = null;
        double bestHeuristic = 0;
        gameState = gameState.copy();
        // Take random shots and return the best one
        Random random = new Random();
        for (int i=0; i<numShots; i++) {
            Vector2 shot = new Vector2(
                    random.nextDouble()*2-1,
                    random.nextDouble()*2-1
            ).normalized().scaled(random.nextDouble()* Ball.maxSpeed);

            ArrayList<Vector2> shotPositions = gameState.simulateShot(shot);

            double heuristicVal = heuristic.getShotValue(shotPositions, gameState);

            if (bestShot == null || heuristic.firstBetterThanSecond(heuristicVal, bestHeuristic)) {
                bestHeuristic = heuristicVal;
                bestShot = shot;
            }
        }
        return bestShot;
    }
}
