package Physics;

import Data_storage.BallState;
import Data_storage.Terrain;

public interface StoppingCondition {
    public boolean shouldStop(BallState newState, BallState previousState, double h);
}
