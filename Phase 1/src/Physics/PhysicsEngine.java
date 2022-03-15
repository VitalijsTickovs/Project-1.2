package Physics;

import Data_storage.*;

public class PhysicsEngine {

    public double h = 0.05; // The step of the Euler's method
    public Terrain terrain;
    public Ball[] ballsToSimulate;

    private final double G = 9.81;

    /**
     * Moves all balls by a timeframe h using Euler's integration method. Call this
     * at a regular (frame independent) rate.
     */
    public void fixedUpdate() {
        for (Ball ball : ballsToSimulate) {
            ball.state = countNewBallState(ball);
        }
    }

    private BallState countNewBallState(Ball ball) {
        BallState newState = ball.state.copy();
        newState.position = countNewPosition(ball.state);
        newState.velocity = countNewVelocity(ball);

        return newState;
    }

    private Vector2 countNewPosition(BallState state) {
        Vector2 newPosition = state.position.copy();
        double xVeolcity = state.velocity.x;
        double yVelocity = state.velocity.y;

        newPosition.translate(new Vector2(h * xVeolcity, h * yVelocity));
        return newPosition;
    }

    private Vector2 countNewVelocity(Ball ball) {
        Vector2 newVelocity = ball.state.velocity;
        Vector2 ballPosition = ball.state.position;
        double xSlope = getXSlopeAt(ballPosition.x, ballPosition.y);
        double ySlope = getYSlopeAt(ballPosition.x, ballPosition.y);

        boolean ballInMotion = newVelocity.length() != 0;
        if (ballInMotion) {
            return countBallMotion(ball, xSlope, ySlope);
        }

        return ballStopped(ball);
    }

    private double getXSlopeAt(double x, double y) {
        double value = terrain.terrainFunction.valueAt(x, y);
        if (value > 10 || value < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.xDerivativeAt(x, y);
        }
    }

    private double getYSlopeAt(double x, double y) {
        double value = terrain.terrainFunction.valueAt(x, y);
        if (value > 10 || value < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.yDerivativeAt(x, y);
        }
    }

    private Vector2 countBallMotion(Ball ball, double xSlope, double ySlope) {
        boolean staticFrictionLessThanDownwardForce = terrain.staticFriction < Math
                .sqrt(xSlope * xSlope + ySlope * ySlope);

        double xAcceleration = xAcceleration(ball, xSlope, ySlope, staticFrictionLessThanDownwardForce);
        double yAcceleration = yAcceleration(ball, xSlope, ySlope, staticFrictionLessThanDownwardForce);
        Vector2 newVelocity = ball.state.velocity.copy();
        newVelocity.translate(new Vector2(h * xAcceleration, h * yAcceleration));
        return newVelocity;
    }

    private double xAcceleration(Ball ball, double xSlope, double ySlope, boolean mode) {
        double downHillForce = -G * xSlope;
        double kineticFrictionForce = G * terrain.kineticFriction;

<<<<<<< Updated upstream
        if (mode) {
            kineticFrictionForce *= xSlope / (xSlope * xSlope + ySlope * ySlope);
        } else {
            kineticFrictionForce *= ball.state.velocity.x / ball.state.velocity.length();
        }
        return (downHillForce - kineticFrictionForce);
=======
        return (downHillForce - frictionForce) / ball.mass;
>>>>>>> Stashed changes
    }

    private double yAcceleration(Ball ball, double xSlope, double ySlope, boolean mode) {
        double downHillForce = -G * ySlope;
        double kineticFrictionForce = G * terrain.kineticFriction;

        if (mode) {
            kineticFrictionForce *= ySlope / (xSlope * xSlope + ySlope * ySlope);
        } else {
            kineticFrictionForce *= ball.state.velocity.y / ball.state.velocity.length();

        }
        return (downHillForce - kineticFrictionForce);
    }

<<<<<<< Updated upstream
    private Vector2 ballStopped(Ball ball) {
        ball.ballStopped = true;
        return Vector2.zeroVector;
=======
        return (downHillForce - frictionForce) / ball.mass;
>>>>>>> Stashed changes
    }
}
