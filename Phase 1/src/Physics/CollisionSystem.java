package Physics;

import java.util.ArrayList;

import Data_storage.*;

public interface CollisionSystem {

    public BallState modifyStateDueToCollisions(BallState state, BallState previousState, double ballRadius, Terrain terrain);

}
