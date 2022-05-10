package bot;

import Data_storage.GameState;
import Data_storage.Vector2;

import java.util.ArrayList;

public class FinalEuclidianDistanceHeuristic implements Heuristic {

    @Override
    public double getShotValue(ArrayList<Vector2> shotPositions, GameState gameState) {
        Vector2 finalPosition = shotPositions.get(shotPositions.size()-1);
        Vector2 targetPosition = gameState.getTerrain().target.position;

        return finalPosition.copy().translate(targetPosition.copy().scale(-1)).length();
    }

    @Override
    public boolean firstBetterThanSecond(double heuristic1, double heuristic2) {
        return heuristic1 < heuristic2;
    }
}
