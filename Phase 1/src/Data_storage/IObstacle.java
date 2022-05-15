package Data_storage;

public interface IObstacle {

    public abstract double getBounciness();

    public abstract boolean isPositionColliding(Vector2 position);

    public abstract boolean isBallColliding(Vector2 ballPos, double radius);

    public abstract void print();

    public CollisionData getCollisionData(Vector2 currentPosition, Vector2 previousPosition, double ballRadius);
}
