package Data_storage;

public interface IObstacle {

    public abstract double getBounciness();

    public abstract boolean isPositionColliding(Vector2 position);

    public abstract boolean isBallColliding(Vector2 ballPos, double radius);

    /**
     * Returns a velocity vector that is scaled down by the obstacle's bounciness
     * 
     * @param position
     * @param velocity
     * @return
     */
    public abstract CollisionData getCollisionData(Vector2 currentPosition, Vector2 previousPosition, double ballRadius);

    public abstract void print();
}
