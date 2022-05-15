package Physics;

import Data_storage.BallState;

public interface IStoppingCondition {
    public boolean shouldStop(BallState newState, BallState previousState, double h);
}
