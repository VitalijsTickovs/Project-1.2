package bot;

import Data_storage.GameState;
import Data_storage.Vector2;

public class GradientDescentBot implements Bot {
    private final Heuristic heuristic;
    private final double learningRate;

    public GradientDescentBot(Heuristic heuristic, double learningRate) {
        this.heuristic = heuristic;
        this.learningRate = learningRate;

    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        return null;
    }
}
