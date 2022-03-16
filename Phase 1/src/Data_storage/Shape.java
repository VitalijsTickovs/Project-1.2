package Data_storage;

public abstract class Shape {

    protected abstract boolean isPositionInside(Vector2 objectPosition);

    protected abstract boolean isBallInside(Vector2 objectPosition, double ballRadius);
}
