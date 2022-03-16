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
        double xVelocity = state.velocity.x;
        double yVelocity = state.velocity.y;

        newPosition.translate(new Vector2(h * xVelocity, h * yVelocity));
        if (isTouchingAnObstacle(newPosition)) {
            state.velocity = Vector2.zeroVector;
            return state.position;
        }
        return newPosition;
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

        double xAcceleration = 0, yAcceleration = 0;

        boolean ballInMotion = newVelocity.length() > 0.01;

        // Set velocity to max speed if too big
        if (newVelocity.length() > Ball.maxSpeed) {
            newVelocity = newVelocity.normalized().scale(Ball.maxSpeed);
        }

        Vector2 slope = new Vector2(xSlope, ySlope);

        if (ballInMotion) {
            xAcceleration = xAcceleration(ball, slope, ball.state.velocity);
            yAcceleration = yAcceleration(ball, slope, ball.state.velocity);
        } else {
            // Stop the ball first
            newVelocity = Vector2.zeroVector;

            double staticFriction = getStaticFrictionAtPosition(ball.state.position);
            boolean staticFrictionLessThanDownwardForce = staticFriction < slope.length();

            if (staticFrictionLessThanDownwardForce) {
                xAcceleration = xAcceleration(ball, slope, slope);
                yAcceleration = yAcceleration(ball, slope, slope);
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

    private double xAcceleration(Ball ball, Vector2 slope, Vector2 speed) {
        double downHillForce = -G * slope.x; // /(1 + slope.x*slope.x + slope.y*slope.y);
        double frictionForce = G * terrain.kineticFriction * speed.x / speed.length();
        //frictionForce /= Math.sqrt(1 + slope.x*slope.x + slope.y*slope.y);
        //double expr = slope.x*speed.x + slope.y*speed.y;
        //frictionForce /= Math.sqrt(speed.x*speed.x + speed.y*speed.y + expr*expr);


        return (downHillForce - frictionForce);
    }

    private double yAcceleration(Ball ball, Vector2 slope, Vector2 speed) {
        double downHillForce = -G * slope.y; // /(1 + slope.x*slope.x + slope.y*slope.y);
        double frictionForce = G * terrain.kineticFriction * speed.y / speed.length();
        //frictionForce /= Math.sqrt(1 + slope.x*slope.x + slope.y*slope.y);
        //double expr = slope.x*speed.x + slope.y*speed.y;
        //frictionForce /= Math.sqrt(speed.x*speed.x + speed.y*speed.y + expr*expr);
        return (downHillForce - frictionForce);
    }

    public static void main(String[] args) {
        PhysicsEngine e = new PhysicsEngine();
        e.terrain = new Terrain("e**(-(x**2 + y**2)/40)", 0.2, 0.1, new Vector2(-10, -10), new Vector2(10, 10));
        System.out.println(e.terrain.terrainFunction);
        Ball ball = new Ball(new Vector2(-1, -0.5), new Vector2(3, 0));
        e.addBall(ball);
        e.fixedUpdate();

        System.out.println("Position: (x="+ball.state.position.x+", y="+ball.state.position.y+")");
        System.out.println("Velocity: (v(x)="+ball.state.velocity.x+", v(y)="+ball.state.velocity.y+")");
    }
}
