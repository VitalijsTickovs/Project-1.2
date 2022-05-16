package Physics;

import Data_storage.BallState;
import Data_storage.Vector2;

public class DotProductStoppingCondition implements IStoppingCondition {
    @Override
    public boolean shouldStop(BallState newState, BallState previousState, double h) {
        return Vector2.dotProduct(newState.velocity, previousState.velocity) < 0;
    }

    @Override
    public String getConditionName() {
        return "dotProduct";
    }
}
