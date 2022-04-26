package Physics;

import java.lang.Thread.State;
import java.util.ArrayList;

import Data_storage.*;

public class CollisionSystem {

    public static IObstacle[] obstacles;

    public static void main(String[] args) {
        setObstacles();
        
        
        Ball ball = new Ball(new Vector2(-2,-2), new Vector2(1,1));
        Vector2 previousPosition = new Vector2(-0.9,2);
        modifyPosition(ball.state);
        ArrayList<IObstacle> collidesWith = getTouchedObstacles(ball.state.position, ball.radius);
        CollisionData data = getClosestCollisionData(collidesWith, ball.state.position, previousPosition, ball.radius);
        System.out.println(data);
    }
    
    private static void modifyPosition(BallState state) {
        double h = 0.05; // The step of the Euler's method
        doEulerStep(state, h); // Modifies the ball state's position by one Euler step
    }

    private static void doEulerStep(BallState state, double h) {
        state.position.translate(new Vector2(h * state.velocity.x, h * state.velocity.y));
    }

    
    private static void setObstacles(){
        obstacles = new IObstacle[2];
        obstacles[0] = new ObstacleBox(new Vector2(-1,-1), new Vector2(1,1));
        ObstacleTree tree = new ObstacleTree();
        tree.bounciness = 1;
        tree.radius= 0.5;
        tree.originPosition = new Vector2(0.5, 1.5);
        obstacles[1] = tree;
    }

    public static BallState modifyStateDueToCollisions(BallState state, Vector2 previousPosition, double ballRadius){

        ArrayList<IObstacle> collidesWith = getTouchedObstacles(state.position, ballRadius);
        CollisionData collisionData = getClosestCollisionData(collidesWith, state.position, previousPosition, ballRadius);
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
        for (IObstacle obstacle : obstacles) {
            // if (obstacle.isBallColliding(position, radius)) {
                touchedObstacles.add(obstacle);
            // }
        }
        return touchedObstacles;
    }

    private static CollisionData getClosestCollisionData(ArrayList<IObstacle> collidesWith, Vector2 currentPosition, Vector2 previousPosition, double ballRadius){
        CollisionData closestCollisionData = null;
        for (IObstacle iObstacle : collidesWith) {
            if (iObstacle == null) {
                continue;
            }
            CollisionData collisionData = iObstacle.getCollisionData(currentPosition, previousPosition, ballRadius);
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
        // For eg. if bounciness equals 0.8, the returned velocity vector will be 20% shorter
        state.velocity.scale(collisionData.bounciness);

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
