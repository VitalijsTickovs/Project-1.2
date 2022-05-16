package bot;

import Data_storage.GameState;
import Data_storage.Terrain;
import Data_storage.Vector2;

import java.util.ArrayList;

public class FinalAStarDistanceHeuristic implements Heuristic {

    AStar aStar;

    public FinalAStarDistanceHeuristic(Terrain terrain) {
        aStar = new AStar(terrain);
    }

    @Override
    public double getShotValue(ArrayList<Vector2> shotPositions, GameState gameState) {
        Vector2 finalPosition = shotPositions.get(shotPositions.size() - 1);

        return aStar.getDistanceToTarget(finalPosition);
    }

    @Override
    public boolean firstBetterThanSecond(double heuristic1, double heuristic2) {
        return heuristic1 < heuristic2;
    }
}
