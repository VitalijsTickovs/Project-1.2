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
    private int numSimulations;
    private int numIterations;

    public GradientDescentBot(Heuristic heuristic, double learningRate, IBot initialShotTaker) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
        this.initialShotTaker = initialShotTaker;
    }

    public Vector2 getGradient(GameState gameState, Vector2 shot, double heuristicVal, double stepSize) {
        // Calculate the x partial derivative
        ArrayList<Vector2> positionsX = gameState.simulateShot(
                new Vector2(shot.x + stepSize, shot.y)
        );
        double newHeuristicX = heuristic.getShotValue(positionsX, gameState);
        double dx = (newHeuristicX - heuristicVal)/stepSize;
        /*for (int i = -2; i <= 2; i++) {
            if (i != 0) {
                double h = i * stepSize;
                Vector2 xShot = new Vector2(
                        shot.x + h,
                        shot.y);
                ArrayList<Vector2> positions = gameState.simulateShot(xShot);
                numSimulations++;
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
        dx /= 12 * stepSize*/;

        // Calculate the y partial derivative
        ArrayList<Vector2> positionsY = gameState.simulateShot(
                new Vector2(shot.x, shot.y + stepSize)
        );
        double newHeuristicY = heuristic.getShotValue(positionsY, gameState);
        double dy = (newHeuristicY - heuristicVal)/stepSize;
        /*double dy = 0;
        for (int i = -2; i <= 2; i++) {
            if (i != 0) {
                double h = i * stepSize;
                Vector2 yShot = new Vector2(
                        shot.x,
                        shot.y + h);
                ArrayList<Vector2> positions = gameState.simulateShot(yShot);
                numSimulations++;
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
        dy /= 12 * stepSize;*/

        return new Vector2(dx, dy);
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        numIterations = 0;
        numSimulations = 0;
        gameState = gameState.copy();
        Vector2 currentShot;
        // Take an initial shot
        if (initialShotTaker == null) {
            currentShot = new Vector2(Math.random() * 2 - 1, Math.random() * 2 - 1);
            currentShot.normalize().scale(Ball.maxSpeed * Math.random());
        } else {
            currentShot = initialShotTaker.findBestShot(gameState);
            numSimulations = initialShotTaker.getNumSimulations();
            numIterations = initialShotTaker.getNumIterations();
        }
        ArrayList<Vector2> positions = gameState.simulateShot(currentShot);
        double currentHeuristic = heuristic.getShotValue(positions, gameState);
        final double derivativeStep = 0.0001;
        Vector2 gradient;
        int numShots = 0;
        boolean holeInOne = false;
        // Check for hole in one
        holeInOne = positions.get(positions.size()-1).distanceTo(gameState.getTerrain().target.position) <= gameState.getTerrain().target.radius;
        while (numShots < 1000 && !holeInOne) {
            numIterations++;
            // Calculate whether to do descent or ascent
            /*Vector2 xStepShot = new Vector2(currentShot.x + derivativeStep, currentShot.y);
            ArrayList<Vector2> xShotPositions = gameState.simulateShot(xStepShot);
            numSimulations++;
            double xHeuristic = heuristic.getShotValue(xShotPositions, gameState);*/
            // Calculate the x partial derivative
            ArrayList<Vector2> positionsX = gameState.simulateShot(
                    new Vector2(currentShot.x + derivativeStep, currentShot.y)
            );
            numSimulations++;
            double newHeuristicX = heuristic.getShotValue(positionsX, gameState);
            double dx = (newHeuristicX - currentHeuristic)/derivativeStep;
            // Calculate partial y derivative
            ArrayList<Vector2> positionsY= gameState.simulateShot(
                    new Vector2(currentShot.x, currentShot.y + derivativeStep)
            );
            numSimulations++;
            double newHeuristicY = heuristic.getShotValue(positionsY, gameState);
            double dy = (newHeuristicY - currentHeuristic)/derivativeStep;
            // Calculate gradient
            gradient = new Vector2(dx, dy);//getGradient(gameState, currentShot, currentHeuristic, derivativeStep);
            // Determine ascent/descent
            int sign;
            if (heuristic.firstBetterThanSecond(newHeuristicX, currentHeuristic)) {
                if (newHeuristicX > currentHeuristic) {
                    sign = 1;
                } else {
                    sign = -1;
                }
            } else {
                if (newHeuristicX > currentHeuristic) {
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
            positions = gameState.simulateShot(currentShot);
            numSimulations++;
            currentHeuristic = heuristic.getShotValue(positions, gameState);
            holeInOne = positions.get(positions.size()-1).distanceTo(gameState.getTerrain().target.position) <= gameState.getTerrain().target.radius;
            numShots++;
        }

        return currentShot;
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
