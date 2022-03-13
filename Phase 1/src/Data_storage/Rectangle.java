package Data_storage;

public class Rectangle extends Shape {

    public Vector2 downLeftCorner;
    public Vector2 topRightCorner;

    @Override
    protected boolean isPositionInside(Vector2 objectPosition) {
        boolean isXInside = objectPosition.x > downLeftCorner.x && objectPosition.x < topRightCorner.x;
        boolean isYInside = objectPosition.y > downLeftCorner.y && objectPosition.y < topRightCorner.y;
        if (isXInside && isYInside) {
            return true;
        }
        return false;
    }
}
