package bot.botimplementations;

import bot.heuristics.Heuristic;
import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class SimulatedAnnealingBot implements IBot {
    private final Heuristic heuristic;
    private final double learningRate;
    private final int numIterations;
    private final IBot initialShotTaker;

    public SimulatedAnnealingBot(Heuristic heuristic, double learningRate, int numIterations, IBot initialShotTaker) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;
        this.numIterations = numIterations;
        this.initialShotTaker = initialShotTaker;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        gameState = gameState.copy();

        Random random = new Random();

        Vector2 shot;
        if (initialShotTaker == null) {
            shot = new Vector2(Math.random() * 2 - 1, Math.random() * 2 - 1);
            shot.normalize().scale(Math.random()* Ball.maxSpeed);
        } else {
            shot = initialShotTaker.findBestShot(gameState);
        }
        double currentHeuristicVal = heuristic.getShotValue(
                gameState.simulateShot(shot),
                gameState
        );

        for (int i=0; i<numIterations; i++) {
            double temperature = 1 - (double) (i+1)/numIterations;

            // Select random neighbour
            double degree = random.nextDouble()*360;

            Vector2 updateVector = new Vector2(
                    Math.cos(degree * Math.PI / 180),
                    Math.sin(degree * Math.PI / 180)
            ).scale(learningRate);

            Vector2 neighbourShot = shot.translated(updateVector);
            if (neighbourShot.length() > Ball.maxSpeed) {
                neighbourShot.normalize().scale(Ball.maxSpeed);
            }

            double heuristicVal = heuristic.getShotValue(
                    gameState.simulateShot(neighbourShot),
                    gameState
            );

            double selectProbability; // Calculate the probability of selecting this neighbour

            if (heuristic.firstBetterThanSecond(heuristicVal, currentHeuristicVal)) {
                selectProbability = 1;
            } else {
                selectProbability = Math.exp(-Math.abs(heuristicVal - currentHeuristicVal)/temperature);
            }

            if (selectProbability > random.nextDouble()) {
                shot = neighbourShot.copy();
                currentHeuristicVal = heuristicVal;
            }
        }

        return shot;
    }
}
