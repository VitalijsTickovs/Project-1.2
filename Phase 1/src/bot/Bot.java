package bot;

import Data_storage.Vector2;
import Data_storage.GameState;

public interface Bot {
    public Vector2 findBestShot(GameState gameState);
}
