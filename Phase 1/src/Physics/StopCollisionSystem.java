package Physics;

import Data_storage.BallState;
import Data_storage.IObstacle;
import Data_storage.Terrain;

public class StopCollisionSystem implements CollisionSystem {

    @Override
    public BallState modifyStateDueToCollisions(BallState state, BallState previousState, double ballRadius, Terrain terrain) {
        BallState newState = state.copy();
        // Check if inside an obstacle
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isPositionColliding(state.position)) {
                newState = previousState.copy();
                break;
            }
        }
        return newState;
    }
}
