package Physics;

import java.util.ArrayList;

import Data_storage.*;

public class PhysicsEngine {

    public double h = 0.05; // The step of the Euler's method
    public Terrain terrain;
    public ArrayList<Ball> ballsToSimulate;
    private final double G = 9.81;

    public PhysicsEngine() {
        ballsToSimulate = new ArrayList<>();
        terrain = null;
    }

    public void addBall(Ball ball) {
        ballsToSimulate.add(ball);
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
        if (isTouchingAnObstacle(newPosition)) {
            state.velocity = Vector2.zeroVector;
            return state.position;
        }
        return newPosition;
    }

    private boolean isTouchingAnObstacle(Vector2 position) {
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isColliding(position)) {
                return true;
            }
        }
        return false;
    }

    private Vector2 countNewVelocity(Ball ball) {
        Vector2 newVelocity = ball.state.velocity.copy();
        Vector2 ballPosition = ball.state.position;
        double xSlope = getXSlopeAt(ballPosition.x, ballPosition.y);
        double ySlope = getYSlopeAt(ballPosition.x, ballPosition.y);
        Vector2 slope = new Vector2(xSlope, ySlope);
        double xAcceleration;
        double yAcceleration;

        double velocity = ball.state.velocity.length();
        boolean ballInMotion = velocity > 0.1;
        if (ballInMotion) {
            double kineticFriction = getKineticFrictionAtPosition(ball.state.position);
            xAcceleration = xAcceleration(ball, slope, ball.state.velocity, kineticFriction);
            yAcceleration = yAcceleration(ball, slope, ball.state.velocity, kineticFriction);
        } else {
            // Stop the ball first
            newVelocity = Vector2.zeroVector;

            double staticFriction = getStaticFrictionAtPosition(ball.state.position);
            boolean staticFrictionLessThanDownwardForce = staticFriction < slope.length();

            if (staticFrictionLessThanDownwardForce) {
                xAcceleration = xAcceleration(ball, slope, slope, staticFriction);
                yAcceleration = yAcceleration(ball, slope, slope, staticFriction);
            } else {
                xAcceleration = 0;
                yAcceleration = 0;
            }
        }
        return newVelocity.translate(new Vector2(h * xAcceleration, h * yAcceleration));
    }

    private double getXSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y) * terrain.scaleFactor;
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.xDerivativeAt(x, y);
        }
    }

    private double getYSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y) * terrain.scaleFactor;
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.yDerivativeAt(x, y);
        }
    }

    private double getKineticFrictionAtPosition(Vector2 position) {
        double maxFriction = terrain.kineticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.kineticFriction > maxFriction) {
                maxFriction = zone.kineticFriction;
            }
        }
        return maxFriction;
    }

    private double getStaticFrictionAtPosition(Vector2 position) {
        double maxFriction = terrain.staticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.staticFriction > maxFriction) {
                maxFriction = zone.staticFriction;
            }
        }
        return maxFriction;
    }

    private double xAcceleration(Ball ball, Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction;
        frictionForce *= speed.x / speed.length();
        return (downHillForce - frictionForce) / ball.mass;
    }

    private double yAcceleration(Ball ball, Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.y;
        double frictionForce = G * friction;
        frictionForce *= speed.y / speed.length();
        return (downHillForce - frictionForce) / ball.mass;
    }
}
