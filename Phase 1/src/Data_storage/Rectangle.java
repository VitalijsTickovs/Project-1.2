package Data_storage;

public class Rectangle extends Shape {

    public Rectangle(Vector2 bottomLeftCorner, Vector2 topRightCorner){
        this.bottomLeftCorner = bottomLeftCorner;
        this.topRightCorner = topRightCorner;
    }

    public Vector2 bottomLeftCorner;
    public Vector2 topRightCorner;

    @Override
    public boolean isPositionInside(Vector2 objectPosition) {
        boolean isXInside = objectPosition.x > bottomLeftCorner.x && objectPosition.x < topRightCorner.x;
        boolean isYInside = objectPosition.y > bottomLeftCorner.y && objectPosition.y < topRightCorner.y;
        if (isXInside && isYInside) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isCircleInside(Vector2 objectPosition, double ballRadius) {
        boolean isXInside = objectPosition.x + ballRadius > bottomLeftCorner.x && objectPosition.x - ballRadius < topRightCorner.x;
        boolean isYInside = objectPosition.y + ballRadius > bottomLeftCorner.y && objectPosition.y - ballRadius < topRightCorner.y;
        if (isXInside && isYInside) {
            return true;
        }
        return false;
    }
}
