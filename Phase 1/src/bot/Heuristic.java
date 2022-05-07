package bot;

import Data_storage.GameState;
import Data_storage.Vector2;
import java.util.ArrayList;

public interface Heuristic {
    public double getShotValue(ArrayList<Vector2> shotPositions, GameState gameState);
    public boolean firstBetterThanSecond(double heuristic1, double heuristic2);
}
