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
    public abstract void bounceVector(Vector2 position, Vector2 velocity, double h, double radius);

    public abstract void print();
}
