package bot.botimplementations;

import datastorage.GameState;
import utility.math.Vector2;

public interface Bot {
    public Vector2 findBestShot(GameState gameState);
}
