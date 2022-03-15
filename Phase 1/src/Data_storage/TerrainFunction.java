package Data_storage;

public abstract class TerrainFunction {
    public abstract double valueAt(double x, double y);

    public abstract double xDerivativeAt(double x, double y);

    public abstract double yDerivativeAt(double x, double y);

    public abstract float[] getHeightMap(int numVertecesX, int numVertecesY, double normalFactor);
}
