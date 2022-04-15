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

    /**
     * 
     * @param vector translation vector. The vector will be changed by this offset
     * @return a new changed vector after modifying this vector
     */
    public Vector2 translate(Vector2 vector) {
        x += vector.x;
        y += vector.y;
        return new Vector2(x, y);
    }

    /**
     * 
     * @param vector translation vector. The vector will be changed by this offset
     * @return a new changed vector after modifying this vector
     */
    public Vector2 translate(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
        return new Vector2(x, y);
    }

    /**
     * reverses this vector and returns the result
     */
    public Vector2 reversed() {
        x *= -1;
        y *= -1;
        return this;
    }

    /**
     * reverses a copy of this vector and returns the result without modifying the original vector
     */
    public Vector2 reverse() {
        return new Vector2(-x, -y);
    }
    
    /**
     * scales this vector and returns the result
     */
    public Vector2 scale(double scale) {
        x*= scale;
        y*= scale;
        return this;
    }
    
    /**
     * scales a copy of this vector and returns the result without modifying the original vector
     */
    public Vector2 scaled(double scale) {
        return new Vector2(x * scale, y * scale);
    }

    /**
     * normalizes this vector and returns the result
     */
    public Vector2 normalize() {
        Vector2 normalizedVector = copy().scale(1 / length());
        x = normalizedVector.x;
        y = normalizedVector.y;
        
        return this;
    }

    /**
     * normalizes a copy of this vector and returns the result without modifying the original vector
     */
    public Vector2 normalized() {
        return copy().scale(1 / length());
    }

    /**
     * 
     * @param toVector
     * @return
     */
    public double distanceTo(Vector2 toVector){
        return Math.sqrt((x - toVector.x) * (x - toVector.x) + (y - toVector.y) * (y - toVector.y));
    }

    /**
     * Picture included
     * https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
     * @param normal
     * @return returns a copy of this vector after it is reflected by a normal
     */
    public Vector2 reflected(Vector2 normal) {
        Vector2 vector = copy();
        double dotProduct = dotProduct(vector, normal.normalized());
        // vector - 2 * (dotProduct) * normal;
        Vector2 reflection = vector.translate(normal.normalized().scale(2 * dotProduct).reversed());
        return reflection;
    }
/**
     * Picture included
     * https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
     * @param normal
     * @return reflects this vector by a normal and returns the result
     */
    public Vector2 reflect(Vector2 normal) {
        double dotProduct = dotProduct(this, normal.normalized());
        // vector - 2 * (dotProduct) * normal;
        return translate(normal.normalized().scale(2 * dotProduct).reversed());
    }

    /**
     * 
     * @return a new vector with the same values as the original
     */
    public Vector2 copy() {
        return new Vector2(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static double dotProduct(Vector2 vector1, Vector2 vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * 
     * @param vector1
     * @param vector2
     * @return angle in range (0; Pi)
     */
    public static double angleBetween(Vector2 vector1, Vector2 vector2) {
        double cos = dotProduct(vector1, vector2) / (vector1.length() * vector2.length());
        return Math.acos(cos);
    }
}
