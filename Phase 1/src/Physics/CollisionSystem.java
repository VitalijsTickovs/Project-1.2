package Physics;

import java.util.ArrayList;

import Data_storage.*;

public class CollisionSystem {


    public static BallState modifyStateDueToCollisions(BallState state, Vector2 previousPosition, double ballRadius){

        ArrayList<IObstacle> collidesWith = getTouchedObstacles(state.position, ballRadius);
        CollisionData collisionData = getClosestCollisionData(collidesWith, state.position, previousPosition);
        if (collisionData != null) {
            bounceBall(state, previousPosition, collisionData);
        }

        state.position = checkBallOutOfBounds(state);
        return state;
    }

    /**
     * 
     * @param position
     * @return the obstacle that the ball collided with or null if it didn't
     */
    private static ArrayList<IObstacle> getTouchedObstacles(Vector2 position, double radius) {
        ArrayList<IObstacle> touchedObstacles = new ArrayList<>();
        for (IObstacle obstacle : PhysicsEngine.terrain.obstacles) {
            if (obstacle.isBallColliding(position, radius)) {
                touchedObstacles.add(obstacle);
            }
        }
        return touchedObstacles;
    }

    private static CollisionData getClosestCollisionData(ArrayList<IObstacle> collidesWith, Vector2 currentPosition, Vector2 previousPosition){
        CollisionData closestCollisionData = null;
        for (IObstacle iObstacle : collidesWith) {
            if (iObstacle == null) {
                continue;
            }
            CollisionData collisionData = iObstacle.getCollisionData(currentPosition, previousPosition);
            if (collisionData == null) {
                continue;
            }
            if (closestCollisionData == null) {
                closestCollisionData = collisionData;
                continue;
            }
            if (previousPosition.distanceTo(closestCollisionData.collisionPosition) > previousPosition.distanceTo(collisionData.collisionPosition)) {
                closestCollisionData = collisionData;
            }
        }
        return closestCollisionData;
    }

    private static void bounceBall(BallState state, Vector2 previousPosition, CollisionData collisionData) {
        double moveDistanceAfterCollision = state.position.distanceTo(collisionData.collisionPosition);

        state.velocity.reflect(collisionData.collisionNormal);
        state.velocity.scale(1 - collisionData.bounciness);

        Vector2 newVelocityDirection = state.velocity.normalized();
        state.position.translate(newVelocityDirection.scale(moveDistanceAfterCollision));

    }

    private static Vector2 checkBallOutOfBounds(BallState state){
        Vector2 newPosition = state.position.copy();
        boolean reverseX = false;
        if (newPosition.x > PhysicsEngine.terrain.limitingCorner.x) {
            reverseX = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(PhysicsEngine.terrain.limitingCorner.x, 0), new Vector2(PhysicsEngine.terrain.limitingCorner.x, 1));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.x < PhysicsEngine.terrain.startingCorner.x) {
            reverseX = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(PhysicsEngine.terrain.startingCorner.x, 0), new Vector2(PhysicsEngine.terrain.startingCorner.x, 1));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        }
        // On y-axis
        boolean reverseY = false;
        if (newPosition.y > PhysicsEngine.terrain.limitingCorner.y) {
            reverseY = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(0, PhysicsEngine.terrain.limitingCorner.y), new Vector2(0, PhysicsEngine.terrain.limitingCorner.y));
            if (intersectionPoint != null) {
                newPosition = intersectionPoint;
            } else {
                newPosition = state.position;
            }
        } else if (newPosition.y < PhysicsEngine.terrain.startingCorner.y) {
            reverseY = true;
            Vector2 intersectionPoint = UtilityClass.findLineIntersection(state.position, newPosition,
                    new Vector2(0, PhysicsEngine.terrain.startingCorner.y), new Vector2(0, PhysicsEngine.terrain.startingCorner.y));
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

}
