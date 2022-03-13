package Data_storage;

public interface IObstacle {

    public abstract double getBounciness();

    public abstract boolean isColliding(Vector2 ballPos);

    public abstract Vector2 getCollisionNormal(Vector2 positiono, Vector2 velocity);
}
