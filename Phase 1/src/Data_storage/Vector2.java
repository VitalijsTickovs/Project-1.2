package Data_storage;

public class Vector2 {
    public double x;
    public double y;

    public Vector2() {
        x = 0;
        y = 0;
    }

    public Vector2(double newX, double newY) {
        x = newX;
        y = newY;
    }

    // readonly vectors
    public final static Vector2 zeroVector = new Vector2(0, 0);
    public final static Vector2 rightVector = new Vector2(1, 0);
    public final static Vector2 leftVector = new Vector2(-1, 0);
    public final static Vector2 forwardVector = new Vector2(0, 1);
    public final static Vector2 backwardVector = new Vector2(0, -1);
    public final static Vector2 unitVector = new Vector2(1, 1);

    // Helper methods
    public double length() {
        return Math.sqrt((x * x) + (y * y));
    }

    public void translate(Vector2 vector) {
        x += vector.x;
        y += vector.y;
    }

    public void translate(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public Vector2 getOppositeVector() {
        return new Vector2(-x, -y);
    }

    public Vector2 scale(double scale) {
        return new Vector2(x * scale, y * scale);
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }
}
