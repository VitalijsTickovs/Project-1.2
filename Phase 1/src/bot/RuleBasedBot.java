package bot;

import Data_storage.GameState;
import Data_storage.Vector2;

public class RuleBasedBot implements Bot {

    private final Heuristic heuristic;

    public RuleBasedBot(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        return null;
    }
}
