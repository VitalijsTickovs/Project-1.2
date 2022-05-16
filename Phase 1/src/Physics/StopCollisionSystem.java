package Physics;

import Data_storage.BallState;
import Data_storage.IObstacle;
import Data_storage.Terrain;
import Data_storage.Vector2;

public class StopCollisionSystem implements ICollisionSystem {

    @Override
    public BallState modifyStateDueToCollisions(BallState state, BallState previousState, double ballRadius, Terrain terrain) {
        BallState newState = state.copy();
        // Check if out of map
        if (newState.position.x > terrain.bottomRightCorner.x ||
            newState.position.x < terrain.topLeftCorner.x ||
            newState.position.y > terrain.bottomRightCorner.y ||
            newState.position.y < terrain.topLeftCorner.y) {

            return previousState.copy();
        }
        // Check if inside an obstacle
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isPositionColliding(state.position)) {
                return previousState.copy();
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
