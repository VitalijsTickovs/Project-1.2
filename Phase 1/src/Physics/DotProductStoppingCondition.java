package Physics;

import Data_storage.BallState;
import Data_storage.Vector2;

public class DotProductStoppingCondition implements StoppingCondition {
    @Override
    public boolean shouldStop(BallState newState, BallState previousState, double h) {
        return Vector2.dotProduct(newState.velocity, previousState.velocity) < 0;
    }
}
