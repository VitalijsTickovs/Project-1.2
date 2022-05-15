package Physics;

import Data_storage.BallState;

public class SmallVelocityStoppingCondition implements StoppingCondition {

    @Override
    public boolean shouldStop(BallState newState, BallState previousState, double h) {
        return newState.velocity.length() < h;
    }
}
