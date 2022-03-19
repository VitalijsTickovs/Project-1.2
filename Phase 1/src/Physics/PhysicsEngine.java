package Physics;

import java.util.ArrayList;

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
        newState.position = countNewPosition(newState, ball.radius);
        newState.velocity = countNewVelocity(ball);

        return newState;
    }

    private Vector2 countNewPosition(BallState state, double radius) {
        Vector2 newPosition = state.position.copy();
        doEulerPositionStep(state, h);

        IObstacle collidesWith = isTouchingAnObstacle(newPosition, radius);
        if (collidesWith != null) {
            bounceBall(state, collidesWith, h, radius);
            newPosition = state.position;
        }

        // Check out of bounds
        state.position = checkBallOutOfBounds(state);
        return state.position;
    }

    private void doEulerPositionStep(BallState state, double h) {
        state.position.translate(new Vector2(h * state.velocity.x, h * state.velocity.y));
    }

    /**
     * 
     * @param position
     * @return the obstacle that the ball collided with or null if it didn't
     */
    private IObstacle isTouchingAnObstacle(Vector2 position, double radius) {
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isBallColliding(position, radius)) {
                return obstacle;
            }
        }
        return null;
    }

    private void bounceBall(BallState state, IObstacle collidesWith, double h, double radius) {
        collidesWith.bounceVector(state.position, state.velocity, h, radius);
    }

    private Vector2 checkBallOutOfBounds(BallState state){
        Vector2 newPosition = state.position.copy();
        boolean reverseX = false;
        if (newPosition.x > terrain.limitingCorner.x) {
            reverseX = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(terrain.limitingCorner.x, 0), new Vector2(terrain.limitingCorner.x, 1));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.x < terrain.startingCorner.x) {
            reverseX = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(terrain.startingCorner.x, 0), new Vector2(terrain.startingCorner.x, 1));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        }
        // On y-axis
        boolean reverseY = false;
        if (newPosition.y > terrain.limitingCorner.y) {
            reverseY = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(0, terrain.limitingCorner.y), new Vector2(0, terrain.limitingCorner.y));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.y < terrain.startingCorner.y) {
            reverseY = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(0, terrain.startingCorner.y), new Vector2(0, terrain.startingCorner.y));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        }
        // Reverse the velocity if needed
        if (reverseX) {
            state.velocity.x = -state.velocity.x;
        }
        if (reverseY) {
            state.velocity.y = -state.velocity.y;
        }
        return newPosition;
    }

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
        clampBallVelocity(newVelocity);
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



    public static void main(String[] args) {
        PhysicsEngine e = new PhysicsEngine();
        e.terrain = new Terrain("e**(-(x**2 + y**2)/40)", 0.2, 0.1, new Vector2(-10, -10), new Vector2(10, 10));
        System.out.println(e.terrain.terrainFunction);
        Ball ball = new Ball(new Vector2(-1, -0.5), new Vector2(1, 0));
        e.addBall(ball);
        ArrayList<Vector2> positions = e.simulateShot(new Vector2(3, 0), ball);

        System.out.println("Position: (x=" + ball.state.position.x + ", y=" + ball.state.position.y + ")");
        System.out.println("Velocity: (v(x)=" + ball.state.velocity.x + ", v(y)=" + ball.state.velocity.y + ")");
    }
}
