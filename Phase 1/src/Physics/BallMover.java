package Physics;

import Data_storage.*;

public class BallMover {

    public double h; // The step of the Eulet's method
    public TerrainFunction terrainFunction;

    public BallState countNewBallPosition(BallState state) {
        BallState newState = state.copy();
        newState.position = countNewPosition(state.position);
        newState.velocity = countNewVelocity(state.velocity);

        return newState;
    }

    private Vector2 countNewPosition(Vector2 pos) {
        Vector2 newPosition = pos.copy();
        double xSlope = terrainFunction.xDerivativeAt(pos.x, pos.y);
        double ySlope = terrainFunction.yDerivativeAt(pos.x, pos.y);

        newPosition.translate(new Vector2(h * xSlope, h * ySlope));
        return newPosition;
    }

    private Vector2 countNewVelocity(Vector2 pos) {
        Vector2 newPosition = new Vector2(pos.x, pos.y);
        double xSlope = terrainFunction.xDerivativeAt(pos.x, pos.y);
        double ySlope = terrainFunction.yDerivativeAt(pos.x, pos.y);

        newPosition.translate(new Vector2(h * xSlope, h * ySlope));
        return newPosition;
    }
}
