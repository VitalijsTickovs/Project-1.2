package Physics;

import Data_storage.*;

public class PhysicsEngine {

    public double h = 0.05; // The step of the Euler's method
    public Terrain terrain;
    public Ball[] ballsToSimulate;

    private final double G = 9.81;

    public PhysicsEngine() {
        ballsToSimulate = new Ball[0];
        terrain = null;
    }

    public void addBall(Ball ball) {
        Ball[] temp = new Ball[ballsToSimulate.length+1];
        for (int i=0; i<ballsToSimulate.length; i++) {
            temp[i] = ballsToSimulate[i];
        }
        temp[temp.length-1] = ball;
        ballsToSimulate = temp;
    }

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
        Vector2 newVelocity = ball.state.velocity.copy();
        Vector2 ballPosition = ball.state.position;

        double xSlope = terrain.terrainFunction.xDerivativeAt(ballPosition.x, ballPosition.y);
        double ySlope = terrain.terrainFunction.yDerivativeAt(ballPosition.x, ballPosition.y);

        double xAcceleration = 0, yAcceleration = 0;

        boolean ballInMotion = newVelocity.length() > 0.01;

        Vector2 slope = new Vector2(xSlope, ySlope);

        if (ballInMotion) {
            xAcceleration = xAcceleration(ball, slope, ball.state.velocity, terrain.kineticFriction);
            yAcceleration = yAcceleration(ball, slope, ball.state.velocity, terrain.kineticFriction);
        } else {
            // Stop the ball first
            newVelocity = Vector2.zeroVector;

            boolean staticFrictionLessThanDownwardForce = terrain.staticFriction < slope.length();

            if (staticFrictionLessThanDownwardForce) {
                xAcceleration = xAcceleration(ball, slope, slope, terrain.staticFriction);
                yAcceleration = yAcceleration(ball, slope, slope, terrain.staticFriction);
            } else {
                xAcceleration = 0;
                yAcceleration = 0;
            }
        }

        newVelocity.translate(new Vector2(h * xAcceleration, h * yAcceleration));

        return newVelocity;
    }

    private double xAcceleration(Ball ball, Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction;

        frictionForce *= speed.x / speed.length();

        return (downHillForce - frictionForce);
    }

    private double yAcceleration(Ball ball, Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.y;
        double frictionForce = G * friction;

        frictionForce *= speed.y / speed.length();

        return (downHillForce - frictionForce);
    }
}
