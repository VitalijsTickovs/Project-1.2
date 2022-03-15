package Data_storage;

public class ObstacleTree extends Circle implements IObstacle {

    public double bounciness; // The percentage of momentum that the ball loses after bouncing.
    // This is basically friction for bounces

    @Override
    public boolean isColliding(Vector2 ballPos) {
        return isPositionInside(ballPos);
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
    public Vector2 getCollisionNormal(Vector2 positiono, Vector2 velocity) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void print(){
        System.out.println("Tree: ");
        System.out.print("Position: ");
        System.out.println(originPosition);
        System.out.print("Radius: ");
        System.out.println(radius);
        System.out.print("Bounciness: ");
        System.out.println(bounciness);
    }
}
