package bot.botimplementations;

import java.util.ArrayList;

import bot.heuristics.Heuristic;
import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;

public class GradientDescentBot implements Bot {
    private final Heuristic heuristic;
    private final double learningRate;
    private final Bot initialShotTaker;

    public GradientDescentBot(Heuristic heuristic, double learningRate, Bot initialShotTaker) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
        this.initialShotTaker = initialShotTaker;

    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        gameState = gameState.copy();
        Vector2 currentShot;
        // Take an initial shot
        if (initialShotTaker == null) {
            currentShot = new Vector2(Math.random() * 2 - 1, Math.random() * 2 - 1);
            currentShot.normalize().scale(Ball.maxSpeed*Math.random());
        } else {
            currentShot = initialShotTaker.findBestShot(gameState);
        }
        double currentHeuristic = heuristic.getShotValue(gameState.simulateShot(currentShot), gameState);
        final double derivativeStep = learningRate;
        Vector2 gradient;
        int numIterations = 0;
        do {
            Vector2 xStepShot = new Vector2(currentShot.x + derivativeStep, currentShot.y);
            ArrayList<Vector2> xShotPositions = gameState.simulateShot(xStepShot);
            double xHeuristic = heuristic.getShotValue(xShotPositions, gameState);
            Vector2 yStepShot = new Vector2(currentShot.x, currentShot.y + derivativeStep);
            ArrayList<Vector2> yShotPositions = gameState.simulateShot(yStepShot);
            double yHeuristic = heuristic.getShotValue(yShotPositions, gameState);
            // Calculate the gradient
            gradient = new Vector2(
                    (xHeuristic - currentHeuristic) / derivativeStep,
                    (yHeuristic - currentHeuristic) / derivativeStep
            );
            // Move in the direction of the gradient (either down or up depending on the heuristic
            // Check whether to move up or down
            int sign = 0;
            if (heuristic.firstBetterThanSecond(xHeuristic, currentHeuristic)) {
                if (xHeuristic > currentHeuristic) {
                    sign = 1;
                } else {
                    sign = -1;
                }
            } else {
                if (xHeuristic > currentHeuristic) {
                    sign = -1;
                } else {
                    sign = 1;
                }
            }
            currentShot.translate(gradient.scaled(sign*learningRate));
            currentHeuristic = heuristic.getShotValue(gameState.simulateShot(currentShot), gameState);
            numIterations++;
            System.out.println(gradient.length());
        } while (numIterations < 1000);

        return currentShot;
    }
}
