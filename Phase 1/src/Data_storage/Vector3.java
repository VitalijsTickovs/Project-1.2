package Data_storage;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }

    // readonly vectors
    public final static Vector3 zeroVector = new Vector3(0, 0, 0);
    public final static Vector3 rightVector = new Vector3(1, 0, 0);
    public final static Vector3 leftVector = new Vector3(-1, 0, 0);
    public final static Vector3 upVector = new Vector3(0, 0, 1);
    public final static Vector3 downVector = new Vector3(0, 0, -1);
    public final static Vector3 forwardVector = new Vector3(0, 1, 0);
    public final static Vector3 backwardVector = new Vector3(0, -1, 0);
    public final static Vector3 unitVector = new Vector3(1, 1, 1);

    // Helper methods
    public double length() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public Vector3 translate(Vector3 vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
        return new Vector3(x, y, z);
    }

    public Vector3 translate(double deltaX, double deltaY, double deltaZ) {
        x += deltaX;
        y += deltaY;
        z += deltaZ;
        return new Vector3(x, y, z);
    }

    public Vector3 getOppositeVector() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 scale(double scale) {
        return new Vector3(x * scale, y * scale, z * scale);
    }

    public Vector3 normalized() {
        Vector3 normalizedVector = copy().scale(1 / length());
        return normalizedVector;
    }

    public Vector3 reflect(Vector3 normal) {
        Vector3 vector = copy();
        double dotProduct = dotProduct(vector, normal.normalized());
        // vector - 2 * (dotProduct) * normal;
        Vector3 reflection = vector.translate(normal.normalized().scale(2 * dotProduct).getOppositeVector());
        return reflection;
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public static double dotProduct(Vector3 vector1, Vector3 vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z;
    }

    /**
     * 
     * @param vector1
     * @param vector2
     * @return angle in range (0; Pi)
     */
    public static double angleBetween(Vector3 vector1, Vector3 vector2) {
        double cos = dotProduct(vector1, vector2) / (vector1.length() * vector2.length());
        return Math.acos(cos);
    }

}
