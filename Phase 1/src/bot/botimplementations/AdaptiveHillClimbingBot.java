package bot.botimplementations;

import bot.heuristics.Heuristic;
import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;

public class AdaptiveHillClimbingBot implements IBot {

    private int numIterations, numSimulations;
    private final Heuristic heuristic;
    private final IBot initialShotTaker;

    public AdaptiveHillClimbingBot(Heuristic heuristic, IBot initialShotTaker) {
        numIterations = 0;
        numSimulations = 0;
        this.heuristic = heuristic;
        this.initialShotTaker = initialShotTaker;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        numSimulations = 0;
        numIterations = 0;
        gameState = gameState.copy();

        Vector2 bestShot;
        if (initialShotTaker == null) {
            bestShot = new Vector2(
                    Math.random()*2-1,
                    Math.random()*2-1
            ).normalize().scale(Math.random()* Ball.maxSpeed);
        } else {
            bestShot = initialShotTaker.findBestShot(gameState);
            numSimulations = initialShotTaker.getNumSimulations();
            numIterations = initialShotTaker.getNumIterations();
        }
        double bestHeuristicVal = heuristic.getShotValue(
                gameState.simulateShot(bestShot),
                gameState
        );
        numSimulations++;

        double learningRate = 1;
        boolean converged = false;
        while (!converged) {
            numIterations++;
            Vector2 tempBestShot = null;
            for (double dir=0; dir<360; dir+=90) {
                Vector2 newShot = bestShot.copy();
                Vector2 updateVector = new Vector2(
                        Math.sin(dir/180*Math.PI),
                        Math.cos(dir/180*Math.PI)
                ).scale(learningRate);
                newShot.translate(updateVector);
                if (newShot.length() > Ball.maxSpeed) {
                    newShot.normalize().scale(Ball.maxSpeed);
                }

                double heuristicVal = heuristic.getShotValue(
                        gameState.simulateShot(newShot),
                        gameState
                );
                numSimulations++;

                if (heuristic.firstBetterThanSecond(heuristicVal, bestHeuristicVal)) {
                    bestHeuristicVal = heuristicVal;
                    tempBestShot = newShot;
                }
            }

            if (tempBestShot != null) {
                bestShot = tempBestShot;
            } else {
                if (learningRate < 0.01) {
                    converged = true;
                }
                learningRate /= 2;
            }
        }
        return bestShot;
    }

    @Override
    public int getNumSimulations() {
        return numSimulations;
    }

    @Override
    public int getNumIterations() {
        return numIterations;
    }
}
