package Physics;

import Data_storage.BallState;
import Data_storage.Terrain;

public interface ODESolver {
    public BallState calculateNewBallState(BallState state, Terrain terrain, PhysicsEngine engine);
    public double getStepSize();
    public void setStepSize(double h);
}
