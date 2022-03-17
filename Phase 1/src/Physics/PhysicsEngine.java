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

    public ArrayList<Vector2> simulateShot(Vector2 initialSpeed, Ball ball) {
        ArrayList<Vector2> coordinates = new ArrayList<Vector2>();
        ball.state.velocity = initialSpeed;
        do {
            ball.state = countNewBallState(ball);
            coordinates.add(new Vector2(ball.state.position.x, ball.state.position.y));
        } while (ball.state.velocity.length() != 0);
        return coordinates;
    }

    private BallState countNewBallState(Ball ball) {
        BallState newState = ball.state.copy();
        newState.position = countNewPosition(ball.state);
        newState.velocity = countNewVelocity(ball);
        System.out.println(newState.velocity);

        return newState;
    }

    private Vector2 countNewPosition(BallState state) {
        Vector2 newPosition = state.position.copy();
        double xVelocity = state.velocity.x;
        double yVelocity = state.velocity.y;

        newPosition.translate(new Vector2(h * xVelocity, h * yVelocity));
        if (isTouchingAnObstacle(newPosition)) {
            state.velocity = Vector2.zeroVector;
            return state.position;
        }
        // Check out of bounds
        boolean reverseX = false;

        if (newPosition.x > terrain.limitingCorner.x) {
            reverseX = true;
            Vector2 tempPosition = findLinesIntersect(state.position, newPosition,
                    new Vector2(terrain.limitingCorner.x, 0), new Vector2(terrain.limitingCorner.x, 1));
            if (tempPosition != null) {
                newPosition = tempPosition;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.x < terrain.startingCorner.x) {
            reverseX = true;
            Vector2 tempPosition = findLinesIntersect(state.position, newPosition,
                    new Vector2(terrain.startingCorner.x, 0), new Vector2(terrain.startingCorner.x, 1));
            if (tempPosition != null) {
                newPosition = tempPosition;
            } else {
                newPosition = state.position;
            }
        }

        boolean reverseY = false;
        if (newPosition.y > terrain.limitingCorner.y) {
            reverseY = true;
            Vector2 tempPosition = findLinesIntersect(state.position, newPosition,
                    new Vector2(0, terrain.limitingCorner.y), new Vector2(0, terrain.limitingCorner.y));
            if (tempPosition != null) {
                newPosition = tempPosition;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.y < terrain.startingCorner.y) {
            reverseY = true;
            Vector2 tempPosition = findLinesIntersect(state.position, newPosition,
                    new Vector2(0, terrain.startingCorner.y), new Vector2(0, terrain.startingCorner.y));
            if (tempPosition != null) {
                newPosition = tempPosition;
            } else {
                newPosition = state.position;
            }
        }

        if (reverseX) {
            state.velocity.x = -state.velocity.x;
        }
        if (reverseY) {
            state.velocity.y = -state.velocity.y;
        }
        return newPosition;
    }

    private Vector2 findLinesIntersect(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        // (line1 = (A1, B1, C1)) A1*x + B1*y + C1 = 0 | x = (C1 - B1*y)/A1 | y = (C1 - A1*x)/B1
        // (line2 = (A2, B2, C2)) A2*x + B2*y + C2 = 0 | x = (C2 - B2*y)/A2 | y = (C2 - A2*x)/B2
        // k1*x + l1 = k2*x + l2
        // (k1 - k2)*x = l2 - l1
        // x = (l2 - l1)/(k1 - k2)

        double detX = getDeterminant(
                getDeterminant(p1.x, p1.y, p2.x, p2.y),
                getDeterminant(p1.x, 1, p2.x, 1),
                getDeterminant(p3.x, p3.y, p4.x, p4.y),
                getDeterminant(p3.x, 1, p4.x, 1)
        );
        double detY = getDeterminant(
                getDeterminant(p1.x, p1.y, p2.x, p2.y),
                getDeterminant(p1.y, 1, p2.y, 1),
                getDeterminant(p3.x, p3.y, p4.x, p4.y),
                getDeterminant(p3.y, 1, p4.y, 1)
        );
        double detD = getDeterminant(
                getDeterminant(p1.x, 1, p2.x, 1),
                getDeterminant(p1.y, 1, p2.y, 1),
                getDeterminant(p3.x, 1, p4.x, 1),
                getDeterminant(p3.y, 1, p4.y, 1)
        );

        if (detD == 0) {
            return null;
        }

        return new Vector2(detX/detD, detY/detD);
    }

    private double getDeterminant(double p11, double p12, double p21, double p22) {
        return p11*p22 - p12*p21;
    }

    private boolean isTouchingAnObstacle(Vector2 position){
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

    private double getXSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.xDerivativeAt(x, y);
        }
    }

    private double getYSlopeAt(double x, double y) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.yDerivativeAt(x, y);
        }
    }

    private double getKineticFrictionAtPosition(Vector2 position) {
        double friction = terrain.kineticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.kineticFriction;
            }
        }
        return friction;
    }

    private double getStaticFrictionAtPosition(Vector2 position) {
        double friction = terrain.staticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.staticFriction;
            }
        }
        return friction;
    }

    private double xAcceleration(Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction * speed.x / speed.length();
        return (downHillForce - frictionForce);
    }

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
