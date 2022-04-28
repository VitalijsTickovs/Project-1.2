package Physics;

import java.util.ArrayList;
import java.util.LinkedList;

import Data_storage.*;

public class PhysicsEngine {

    public double h = 0.05; // The step of the Euler's method
    public static Terrain terrain;
    public ArrayList<Ball> ballsToSimulate;
    private final double G = 9.81;

    public PhysicsEngine(Terrain setTerrain) {
        ballsToSimulate = new ArrayList<Ball>();
        terrain = setTerrain;
        CollisionSystem.obstacles = terrain.obstacles;
    }

    /**
     * Adds a ball to the list of balls that should be processed
     * @param ball The new Ball to process
     */
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

    /**
     * Simulates a shot and stores the positions until the ball stops
     * @param initialSpeed The inital speed of the ball
     * @param ball The ball to shoot
     * @return ArrayList containing ball positions throughout the shot
     */
    public LinkedList<Vector2> simulateShot(Vector2 initialSpeed, Ball ball) {
        LinkedList<Vector2> coordinates = new LinkedList<Vector2>();
        ball.state.velocity = initialSpeed;
        do {
            ball.state = countNewBallState(ball);
            coordinates.add(new Vector2(ball.state.position.x, ball.state.position.y));
        } while (ball.state.velocity.length() != 0);
        return coordinates;
    }

    /**
     * Creates the new state of the ball after an iteration step
     * @param ball The ball to calculate the new state for
     * @return The new state of the ball
     */
    private BallState countNewBallState(Ball ball) {
        BallState newState = ball.state.copy();
        modifyPosition(newState);
        Vector2 startingPosition = ball.state.position;
        CollisionSystem.modifyStateDueToCollisions(newState, startingPosition, ball.radius);
        newState.velocity = countNewVelocity(newState);

        return newState;
    }

    private void modifyPosition(BallState state) {
        doEulerStep(state, h); // Modifies the ball state's position by one Euler step
    }

    private void doEulerStep(BallState state, double h) {
        state.position.translate(new Vector2(h * state.velocity.x, h * state.velocity.y));
    }

    private Vector2 countNewVelocity(BallState ballState) {
        Vector2 newVelocity = ballState.velocity.copy();
        Vector2 ballPosition = ballState.position;
        double xSlope = getXSlopeAt(ballPosition.x, ballPosition.y);
        double ySlope = getYSlopeAt(ballPosition.x, ballPosition.y);

        // Check if in water
        if (terrain.terrainFunction.valueAt(ballState.position.x, ballState.position.y) <= 0) {
            return Vector2.zeroVector;
        }

        double kineticFriction = getKineticFrictionAtPosition(ballState.position);

        double xAcceleration = 0, yAcceleration = 0;

        boolean ballInMotion = newVelocity.length() > h;

        // Set velocity to max speed if too big
        clampBallVelocity(newVelocity);

        Vector2 slope = new Vector2(xSlope, ySlope);

        if (ballInMotion) {
            xAcceleration = xAcceleration(slope, ballState.velocity, kineticFriction);
            yAcceleration = yAcceleration(slope, ballState.velocity, kineticFriction);
        } else {
            // Stop the ball first
            newVelocity = Vector2.zeroVector;

            double staticFriction = getStaticFrictionAtPosition(ballState.position);
            boolean staticFrictionLessThanDownwardForce = staticFriction < slope.length();

            if (staticFrictionLessThanDownwardForce) {
                xAcceleration = xAcceleration(slope, slope, kineticFriction);
                yAcceleration = yAcceleration(slope, slope, kineticFriction);
            } else {
                xAcceleration = 0;
                yAcceleration = 0;
            }
        }

        newVelocity.translate(new Vector2(h * xAcceleration, h * yAcceleration));

        // Set velocity to max speed if too big
        clampBallVelocity(newVelocity);

        return newVelocity;
    }

    /**
     * Gets the x derivative at a given position
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The derivative value
     */
    private double getXSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.xDerivativeAt(x, y);
        }
    }

    /**
     * Gets the y derivative at a given position
     * @param x The x coordinate
     * @param y THe y coordinate
     * @return The derivative value
     */
    private double getYSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.yDerivativeAt(x, y);
        }
    }

    /**
     * Gets the kinetic friction at a given position
     * @param position The position to check
     * @return The kinetic friction value
     */
    private double getKineticFrictionAtPosition(Vector2 position) {
        double friction = terrain.kineticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.kineticFriction;
            }
        }
        return friction;
    }

    /**
     * Gets the static friction at a given position
     * @param position The position to check
     * @return The static friction value
     */
    private double getStaticFrictionAtPosition(Vector2 position) {
        double friction = terrain.staticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.staticFriction;
            }
        }
        return friction;
    }

    /**
     * Gets the x-acceleration
     * @param slope The terrain derivative vector
     * @param speed The current velocity vector
     * @param friction The friction to use
     * @return The x-acceleration value
     */
    private double xAcceleration(Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction * speed.x / speed.length();
        return (downHillForce - frictionForce);
    }

    /**
     * Gets the y-acceleration
     * @param slope The terrain derivative vector
     * @param speed The current velocity vector
     * @param friction The friction to use
     * @return The y-acceleration value
     */
    private double yAcceleration(Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.y;
        double frictionForce = G * friction * speed.y / speed.length();
        return (downHillForce - frictionForce);
    }

    private void clampBallVelocity(Vector2 velocity) {
        if (velocity.length() > Ball.maxSpeed) {
            velocity = velocity.normalized().scale(Ball.maxSpeed);
        }
    }
}
