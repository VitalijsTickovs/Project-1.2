package Physics;

import java.util.ArrayList;
import java.util.Arrays;

import Data_storage.*;

public class PhysicsEngine {

    public double h = 0.05; // The step of the Euler's method
    public Terrain terrain;
    public ArrayList<Ball> ballsToSimulate;
    private final double G = 9.81;

    public PhysicsEngine() {
        ballsToSimulate = new ArrayList<Ball>();
        terrain = null;
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
    public ArrayList<Vector2> simulateShot(Vector2 initialSpeed, Ball ball) {
        ArrayList<Vector2> coordinates = new ArrayList<Vector2>();
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
        newState.position = countNewPosition(ball.state);
        newState.velocity = countNewVelocity(ball);

        return newState;
    }

    /**
     * Gets the new position of the ball after the current iteration step
     * @param state The current state of the ball
     * @return The new position of the ball
     */
    private Vector2 countNewPosition(BallState state) {
        Vector2 newPosition = state.position.copy();
        double xVelocity = state.velocity.x;
        double yVelocity = state.velocity.y;
        // Move the ball
        newPosition.translate(new Vector2(h * xVelocity, h * yVelocity));
        if (isTouchingAnObstacle(newPosition)) {
            state.velocity = Vector2.zeroVector;
            return state.position;
        }
        // Check for out of bounds
        // On x-axis
        boolean reverseX = false;
        if (newPosition.x > terrain.limitingCorner.x) {
            reverseX = true;
            newPosition = state.position;
        } else if (newPosition.x < terrain.startingCorner.x) {
            reverseX = true;
            newPosition = state.position;
        }
        // On y-axis
        boolean reverseY = false;
        if (newPosition.y > terrain.limitingCorner.y) {
            reverseY = true;
            newPosition = state.position;
        } else if (newPosition.y < terrain.startingCorner.y) {
            reverseY = true;
            newPosition = state.position;
        }
        // Reverse the velocity if needed
        if (reverseX) {
            state.velocity.x = -state.velocity.x;
        }
        if (reverseY) {
            state.velocity.y = -state.velocity.y;
        }
        // Check for collision
        if (isTouchingAnObstacle(newPosition)) {
            newPosition = state.position.copy();
        }
        return newPosition;
    }

    /**
     * Checks if a position is colliding with an obstacle
     * @param position The position to check for
     * @return {@code true} if colliding and {@code false} otherwise
     */
    private boolean isTouchingAnObstacle(Vector2 position){
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isColliding(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the new velocity of a ball
     * @param ball The ball to determine the new velocity for
     * @return The new velocity vector
     */
    private Vector2 countNewVelocity(Ball ball) {
        Vector2 newVelocity = ball.state.velocity.copy();
        Vector2 ballPosition = ball.state.position;
        double xSlope = getXSlopeAt(ballPosition.x, ballPosition.y);
        double ySlope = getYSlopeAt(ballPosition.x, ballPosition.y);

        // Check if in water
        if (terrain.terrainFunction.valueAt(ball.state.position.x, ball.state.position.y) <= 0) {
            return Vector2.zeroVector;
        }

        double kineticFriction = getKineticFrictionAtPosition(ball.state.position);

        double xAcceleration = 0, yAcceleration = 0;

        boolean ballInMotion = newVelocity.length() > h;

        // Set velocity to max speed if too big
        if (newVelocity.length() > Ball.maxSpeed) {
            newVelocity = newVelocity.normalized().scale(Ball.maxSpeed);
        }

        Vector2 slope = new Vector2(xSlope, ySlope);

        if (ballInMotion) {
            xAcceleration = xAcceleration(slope, ball.state.velocity, kineticFriction);
            yAcceleration = yAcceleration(slope, ball.state.velocity, kineticFriction);
        } else {
            // Stop the ball first
            newVelocity = Vector2.zeroVector;

            double staticFriction = getStaticFrictionAtPosition(ball.state.position);
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
        if (newVelocity.length() > Ball.maxSpeed) {
            newVelocity = newVelocity.normalized().scale(Ball.maxSpeed);
        }

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

    public static void main(String[] args) {
        PhysicsEngine e = new PhysicsEngine();
        e.terrain = new Terrain("e**(-(x**2 + y**2)/40)", 0.2, 0.1, new Vector2(-10, -10), new Vector2(10, 10));
        System.out.println(e.terrain.terrainFunction);
        Ball ball = new Ball(new Vector2(-1, -0.5), new Vector2(1, 0));
        e.addBall(ball);
        ArrayList<Vector2> positions = e.simulateShot(new Vector2(3, 0), ball);

        System.out.println("Position: (x="+ball.state.position.x+", y="+ball.state.position.y+")");
        System.out.println("Velocity: (v(x)="+ball.state.velocity.x+", v(y)="+ball.state.velocity.y+")");
    }
}
