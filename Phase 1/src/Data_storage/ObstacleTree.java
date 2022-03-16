package Data_storage;

public class ObstacleTree extends Circle implements IObstacle {

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

    public ObstacleTree copy() {
        ObstacleTree newTree = new ObstacleTree();
        newTree.bounciness = bounciness;
        newTree.originPosition = originPosition;
        newTree.radius = radius;
        return newTree;
    }

    @Override
    public void bounceVector(Vector2 position, Vector2 velocity, double h, double ballRadius) {
        Vector2 normal = getCollisionNormal(position);
        // If bounciness equals 0.8, the returned velocity vector will be 20% shorter
        Vector2 bouncedVelocity = velocity.reflect(normal).scale(bounciness);
        velocity = bouncedVelocity;
        Vector2 positionToBall = originPosition.translate(position.getOppositeVector());
        double distanceToBall = positionToBall.length();
        double moveBall = distanceToBall - ballRadius - radius;
        Vector2 moveToObstacleVector = positionToBall.normalized().scale(moveBall * h);
        position.translate(moveToObstacleVector);
        Vector2 movePastObstacleVector = velocity.normalized().scale((distanceToBall - moveBall) * h);
        position.translate(movePastObstacleVector);
    }

    public Vector2 getCollisionNormal(Vector2 position) {
        return position.translate(originPosition.getOppositeVector());
    }

    @Override
    public void print() {
        System.out.println("Tree: ");
        System.out.print("Position: ");
        System.out.println(originPosition);
        System.out.print("Radius: ");
        System.out.println(radius);
        System.out.print("Bounciness: ");
        System.out.println(bounciness);
    }
}
