package Physics;

import Data_storage.BallState;
import Data_storage.IObstacle;
import Data_storage.Terrain;
import Data_storage.Vector2;

public class StopCollisionSystem implements ICollisionSystem {

    @Override
    public BallState modifyStateDueToCollisions(BallState state, BallState previousState, double ballRadius, Terrain terrain) {
        BallState newState = state.copy();
        // Check if inside an obstacle
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isPositionColliding(state.position)) {
                newState.position = previousState.position.copy();
                newState.velocity = Vector2.zeroVector();
                break;
            }
        }
        return newState;
    }

    @Override
    public String getCollisionSystemName() {
        // TODO Auto-generated method stub
        return "stop";
    }
}
