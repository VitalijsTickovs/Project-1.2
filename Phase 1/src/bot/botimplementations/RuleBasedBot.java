package bot.botimplementations;

import bot.heuristics.Heuristic;
import datastorage.GameState;
import utility.math.Vector2;

public class RuleBasedBot implements IBot {

    private final Heuristic heuristic;

    public RuleBasedBot(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Vector2 findBestShot(GameState gameState) {
        return null;
    }
}
