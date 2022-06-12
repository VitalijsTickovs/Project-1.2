package bot.heuristics;

import java.util.ArrayList;

import bot.AStar;
import datastorage.GameState;
import datastorage.Terrain;
import utility.math.Vector2;

public class FinalAStarDistanceHeuristic implements Heuristic {

    AStar aStar;

    public FinalAStarDistanceHeuristic(Terrain terrain) {
        aStar = new AStar(terrain);
    }

    @Override
    public double getShotValue(ArrayList<Vector2> shotPositions, GameState gameState) {
        Vector2 finalPosition = shotPositions.get(shotPositions.size() - 1);

        return aStar.getDistanceToTarget(finalPosition,2);
    }

    @Override
    public boolean firstBetterThanSecond(double heuristic1, double heuristic2) {
        return heuristic1 < heuristic2;
    }
}
