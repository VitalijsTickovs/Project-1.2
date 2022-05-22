package bot.botimplementations;

import java.util.ArrayList;

import bot.heuristics.Heuristic;
import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;

public class GradientDescentBot implements IBot {
    private final Heuristic heuristic;
    private final double learningRate;
    private final IBot initialShotTaker;

    public GradientDescentBot(Heuristic heuristic, double learningRate, IBot initialShotTaker) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
        this.initialShotTaker = initialShotTaker;
    }

    public Vector2 getGradient(GameState gameState, Vector2 shot, double stepSize) {
        // Calculate the x partial derivative
        double dx = 0;
        for (int i = -2; i <= 2; i++) {
            if (i != 0) {
                double h = i * stepSize;
                Vector2 xShot = new Vector2(
                        shot.x + h,
                        shot.y);
                ArrayList<Vector2> positions = gameState.simulateShot(xShot);
                double heuristicVal = heuristic.getShotValue(positions, gameState);
                switch (i) {
                    case -2: {
                        dx += heuristicVal;
                        break;
                    }

                    case 2: {
                        dx -= heuristicVal;
                        break;
                    }
                    case -1: {
                        dx -= 8 * heuristicVal;
                        break;
                    }
                    case 1: {
                        dx += 8 * heuristicVal;
                        break;
                    }
                }
            }
        }
        dx /= 12 * stepSize;

        // Calculate the y partial derivative
        double dy = 0;
        for (int i = -2; i <= 2; i++) {
            if (i != 0) {
                double h = i * stepSize;
                Vector2 yShot = new Vector2(
                        shot.x,
                        shot.y + h);
                ArrayList<Vector2> positions = gameState.simulateShot(yShot);
                double heuristicVal = heuristic.getShotValue(positions, gameState);
                switch (i) {
                    case -2: {
                        dy += heuristicVal;
                        break;
                    }
                    case 2: {
                        dy -= heuristicVal;
                        break;
                    }
                    case -1: {
                        dy -= 8 * heuristicVal;
                        break;
                    }
                    case 1: {
                        dy += 8 * heuristicVal;
                        break;
                    }
                }
            }
        }
        dy /= 12 * stepSize;

        return new Vector2(dx, dy);
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        gameState = gameState.copy();
        Vector2 currentShot;
        // Take an initial shot
        if (initialShotTaker == null) {
            currentShot = new Vector2(Math.random() * 2 - 1, Math.random() * 2 - 1);
            currentShot.normalize().scale(Ball.maxSpeed * Math.random());
        } else {
            currentShot = initialShotTaker.findBestShot(gameState);
        }
        double currentHeuristic = heuristic.getShotValue(gameState.simulateShot(currentShot), gameState);
        final double derivativeStep = 0.0001;
        Vector2 gradient;
        int numIterations = 0;
        do {
            // Calculate whether to do descent or ascent
            Vector2 xStepShot = new Vector2(currentShot.x + derivativeStep, currentShot.y);
            ArrayList<Vector2> xShotPositions = gameState.simulateShot(xStepShot);
            double xHeuristic = heuristic.getShotValue(xShotPositions, gameState);
            gradient = getGradient(gameState, currentShot, derivativeStep);
            int sign;
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
            currentShot.translate(gradient.scaled(sign * learningRate));
            // Clamp the velocity
            if (currentShot.length() > Ball.maxSpeed) {
                currentShot.normalize().scale(Ball.maxSpeed);
            }
            currentHeuristic = heuristic.getShotValue(gameState.simulateShot(currentShot), gameState);
            numIterations++;
        } while (numIterations < 100);

        return currentShot;
    }
}
