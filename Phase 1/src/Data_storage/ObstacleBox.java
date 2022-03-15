package Data_storage;

public class ObstacleBox extends Rectangle implements IObstacle {

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
    @Override
    public Vector2 getCollisionNormal(Vector2 positiono, Vector2 velocity) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void print(){
        System.out.println("Box: ");
        System.out.print("Down left corner: ");
        System.out.println(downLeftCorner);
        System.out.print("Top right corner: ");
        System.out.println(topRightCorner);
        System.out.print("Bounciness: ");
        System.out.println(bounciness);
    }
}
