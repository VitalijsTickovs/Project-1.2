package Physics;

import Data_storage.*;

public interface ICollisionSystem {

    public BallState modifyStateDueToCollisions(BallState state, BallState previousState, double ballRadius, Terrain terrain);

    public String getCollisionSystemName();
}
