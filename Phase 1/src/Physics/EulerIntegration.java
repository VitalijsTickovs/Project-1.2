package Physics;

import Data_storage.*;

public class EulerIntegration extends PhysicsEngine {

    @Override
    protected BallState countNewBallState(Ball ball, Terrain terrain) {
        /*BallState newState = ball.state.copy();
        newState.position = countNewPosition(ball.state);
        newState.velocity = countNewVelocity(ball);

        return newState;*/

        return handleCollisions(ball.state, eulerStep(ball.state, F(ball.state, terrain), h, true, terrain), terrain);
    }

    /*@Override
    protected Vector2 countNewPosition(BallState state) {
        // Normalize the velocity if needed
        if (state.velocity.length() > Ball.maxSpeed) {
            state.velocity = state.velocity.normalized().scale(Ball.maxSpeed);
        }
        Vector2 newPosition = state.position.copy();
        double xVelocity = state.velocity.x;
        double yVelocity = state.velocity.y;
        // Move the ball
        newPosition.translate(new Vector2(h * xVelocity, h * yVelocity));
        // Collisions with obstacles
        if (isTouchingAnObstacle(newPosition)) {
            state.velocity = new Vector2(0, 0);
            return state.position;
        }
        // Check for out of bounds
        // On x-axis
        boolean reverseX = false;
        if (newPosition.x > terrain.limitingCorner.x) {
            reverseX = true;
            newPosition = state.position.copy();
        } else if (newPosition.x < terrain.startingCorner.x) {
            reverseX = true;
            newPosition = state.position.copy();
        }
        // On y-axis
        boolean reverseY = false;
        if (newPosition.y > terrain.limitingCorner.y) {
            reverseY = true;
            newPosition = state.position.copy();
        } else if (newPosition.y < terrain.startingCorner.y) {
            reverseY = true;
            newPosition = state.position.copy();
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

    @Override
    protected Vector2 countNewVelocity(Ball ball) {
        Vector2 newVelocity = ball.state.velocity.copy();
        Vector2 ballPosition = ball.state.position;
        double xSlope = getXSlopeAt(ballPosition.x, ballPosition.y);
        double ySlope = getYSlopeAt(ballPosition.x, ballPosition.y);

        // Check if in water
        if (!testing && terrain.terrainFunction.valueAt(ball.state.position.x, ball.state.position.y) < 0) {
            return new Vector2(0, 0);
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
            newVelocity = new Vector2(0, 0);//Vector2.zeroVector;

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
    }*/


    /**
     * Performs a single Euler step, returning a new ball state
     *
     * formulas:
     *     - any_state = (x, y, vx, vy)
     *     - update = F(any_state) = (vx, vy, fx(any_state), fy(any_state))
     *     - newState = state + h*update
     *
     * @param state The current ball state
     * @param update The ball state to modify the old one with (when using regular Euler method should be equal to F(state))
     * @param h The step size to use for this step
     * @return The new ball state
     */
    public BallState eulerStep(BallState state, UpdateVector update, double h, boolean stop, Terrain terrain) {
        UpdateVector _update = update.copy();
        // Initialize the new state
        BallState newState = state.copy();
        // Make sure the velocity is not too big
        if (newState.velocity.length() > Ball.maxSpeed) {
            newState.velocity = newState.velocity.normalized().scale(Ball.maxSpeed);
        }
        // Stop moving
        /*if (update.velocity.length() == 0) {
            //state.velocity = Vector2.zeroVector.copy();
            if (update.acceleration.length() == 0) {
                newState.velocity = Vector2.zeroVector.copy();
            }
        }*/
        Vector2 tempVelocity = newState.velocity.copy().translate(update.acceleration.copy().scale(h));
        if (stop && Vector2.dotProduct(tempVelocity, newState.velocity) <= 0 || update.velocity.length() < h) {
            //if (stop) {
            newState.velocity = Vector2.zeroVector.copy();
            //} else {
            //_update.velocity = Vector2.zeroVector.copy();
            //double length = _update.velocity.length();
            //_update.velocity = _update.velocity.normalized().scale(Math.min(length, tempVelocity.length()));
            //newState.velocity = Vector2.zeroVector.copy();
            //}
            Vector2 slope = new Vector2(
                getXSlopeAt(newState.position.x, newState.position.y, terrain),
                getYSlopeAt(newState.position.x, newState.position.y, terrain)
            );
            if (slope.length() > getStaticFrictionAtPosition(newState.position, terrain)) {
                double kineticFriction = getKineticFrictionAtPosition(newState.position, terrain);
                _update.acceleration = new Vector2(
                    xAcceleration(slope, slope, kineticFriction),
                    yAcceleration(slope, slope, kineticFriction)
                );
            } else {
                _update.acceleration = Vector2.zeroVector.copy();
            }
        }
        // Set the new position based on velocity
        newState.position.translate(_update.velocity.copy().scale(h));
        // Check if in water
        if (terrain.terrainFunction.valueAt(newState.position.x, newState.position.y) < 0) {
            newState.velocity = Vector2.zeroVector.copy();
            return newState;
        }
        // Set new velocity based on acceleration
        newState.velocity.translate(_update.acceleration.copy().scale(h));

        return newState;
    }

    /**
     * Gets the update vector used in an Euler step
     *
     * state = [x, y, vx, vy]
     *
     * fx(state) -> gets the x acceleration
     * fy(state) -> gets the y acceleration
     *
     * update(state) = [vx, vy, fx(state), fy(state)]
     *
     * @param state The state to calculate the update vector from
     * @return The update vector
     */
    protected UpdateVector F(BallState state, Terrain terrain) {
        // Get the kinetic and static friction based on the position of in the terrain
        // Depends on whether it is on grass or in a different field
        double kineticFriction = getKineticFrictionAtPosition(state.position, terrain);
        //double staticFriction = getStaticFrictionAtPosition(state.position);
        // Initialize acceleration to 0
        Vector2 acceleration = Vector2.zeroVector.copy();
        Vector2 velocity = state.velocity.copy();
        // Calculate the slope at the position of the state
        Vector2 slope = new Vector2(
                getXSlopeAt(state.position.x, state.position.y, terrain),
                getYSlopeAt(state.position.x, state.position.y, terrain)
        );
        // Make sure the velocity is not greater than the allowed max speed
        if (velocity.length() > Ball.maxSpeed) {
            velocity = velocity.normalized().scale(Ball.maxSpeed);
        }
        /*// Stop moving when slow enough (less than step size)
        if (velocity.length() < h) {
            velocity = Vector2.zeroVector.copy();
            // If derivative is greater than static friction, start moving, using the slope as the velocity
            if (slope.length() > staticFriction) {
                acceleration = new Vector2(
                    xAcceleration(slope, slope, kineticFriction),
                    yAcceleration(slope, slope, kineticFriction)
                );
            }
        // The ball keeps moving
        } else {*/
        if (velocity.length() != 0) {
            // Update the acceleration
            acceleration = new Vector2(
                    xAcceleration(slope, velocity, kineticFriction),
                    yAcceleration(slope, velocity, kineticFriction)
            );
        }
        // Create the vector that updates the ball state
        UpdateVector update = new UpdateVector(
                velocity,
                acceleration
        );
        return update;
    }

    /**
     * Class used for storing updating vectors in the euler step
     * update = [vx, vy, ax, ay]
     */
    protected class UpdateVector {
        Vector2 velocity;
        Vector2 acceleration;

        UpdateVector(Vector2 velocity, Vector2 acceleration) {
            this.velocity = velocity.copy();
            this.acceleration = acceleration.copy();
        }

        UpdateVector copy() {
            return new UpdateVector(velocity, acceleration);
        }

        UpdateVector translate(UpdateVector vector) {
            return new UpdateVector(
                    velocity.copy().translate(vector.velocity),
                    acceleration.copy().translate(vector.acceleration)
            );
        }

        UpdateVector scale(double scalar) {
            return new UpdateVector(
                    velocity.copy().scale(scalar),
                    acceleration.copy().scale(scalar)
            );
        }
    }
}
