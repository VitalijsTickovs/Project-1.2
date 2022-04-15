package Data_storage;

public class ObstacleTree extends Circle implements IObstacle {

    public double bounciness; // The percentage of momentum that the ball loses after bouncing.
    // This is basically friction for bounces

    @Override
    public boolean isBallColliding(Vector2 ballPos, double radius) {
        return isCircleInside(ballPos, radius);
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
    public CollisionData getCollisionData(Vector2 currentPosition, Vector2 previousPosition, double ballRadius) {
        Vector2[] wall = getCollisionNormal(currentPosition, previousPosition);

        CollisionData collisionData = new CollisionData();

        collisionData.bounciness = bounciness;
        collisionData.collisionPosition = wall[2];
    }

    public Vector2 getCollisionNormal(Vector2 position) {
        return position.translate(originPosition.reversed());
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
