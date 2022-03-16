package Data_storage;

import Physics.UtilityClass;

public class ObstacleBox extends Rectangle implements IObstacle {

    public double bounciness; // The percentage of momentum that the ball loses after bouncing.
    // This is basically friction for bounces

    @Override
    public boolean isBallColliding(Vector2 ballPos, double radius) {
        return isBallInside(ballPos, radius);
    }

    @Override
    public boolean isPositionColliding(Vector2 position) {
        return isPositionInside(position);
    }

    @Override
    public double getBounciness() {
        return bounciness;
    }

    @Override
    public void bounceVector(Vector2 position, Vector2 velocity, double h, double ballRadius) {
        Vector2[] wall = getWall(position);
        Vector2 intersectionPosition = UtilityClass.findLineIntersection(position,
        position.copy().translate(velocity.scale(h)), wall[0], wall[1]);
        Vector2 positionToBall = intersectionPosition.translate(position.getOppositeVector());
        double distanceToBall = positionToBall.length();
        double moveBall = distanceToBall - ballRadius;
        Vector2 moveToObstacleVector = positionToBall.normalized().scale(moveBall * h);
        position.translate(moveToObstacleVector);
        Vector2 normal = getCollisionNormal(position, velocity);
        // If bounciness equals 0.8, the returned velocity vector will be 20% shorter
        Vector2 bouncedVelocity = velocity.reflect(normal).scale(bounciness);
        velocity = bouncedVelocity;
        Vector2 movePastObstacleVector = velocity.normalized().scale((distanceToBall - moveBall) * h);
        position.translate(movePastObstacleVector);
    }

    private Vector2[] getWall(Vector2 position) {
        Vector2[] wall = new Vector2[2];
        boolean collidedFromLeft = position.x < bottomLeftCorner.x;
        if (collidedFromLeft) {
            wall[0] = bottomLeftCorner;
            wall[1] = bottomLeftCorner.translate(new Vector2(0, 1));
            return wall;
        }
        boolean collidedFromRight = position.x > topRightCorner.x;
        if (collidedFromRight) {
            wall[0] = topRightCorner;
            wall[1] = topRightCorner.translate(new Vector2(0, -1));
            return wall;
        }
        boolean collidedFromTop = position.y > topRightCorner.y;
        if (collidedFromTop) {
            wall[0] = topRightCorner;
            wall[1] = topRightCorner.translate(new Vector2(-1, 0));
            return wall;
        }
        // collidedFromBottom = position.y < downLeftCorner.y;
        wall[0] = bottomLeftCorner;
        wall[1] = bottomLeftCorner.translate(new Vector2(-1, 0));
        return wall;
    }

    public Vector2 getCollisionNormal(Vector2 position, Vector2 velocity) {
        boolean collidedFromLeft = position.x < bottomLeftCorner.x;
        if (collidedFromLeft) {
            return Vector2.leftVector;
        }
        boolean collidedFromRight = position.x > topRightCorner.x;
        if (collidedFromRight) {
            return Vector2.rightVector;
        }
        boolean collidedFromTop = position.y > topRightCorner.y;
        if (collidedFromTop) {
            return Vector2.forwardVector;
        }
        // collidedFromBottom = position.y < downLeftCorner.y;
        return Vector2.backwardVector;
    }

    @Override
    public void print() {
        System.out.println("Box: ");
        System.out.print("Down left corner: ");
        System.out.println(bottomLeftCorner);
        System.out.print("Top right corner: ");
        System.out.println(topRightCorner);
        System.out.print("Bounciness: ");
        System.out.println(bounciness);
    }
}
