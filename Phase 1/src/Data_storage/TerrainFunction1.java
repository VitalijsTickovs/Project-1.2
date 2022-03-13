package Data_storage;

public class TerrainFunction1 extends TerrainFunction {
    @Override
    public double valueAt(double x, double y) {
        return Math.sin(x + y);
    }

    @Override
    public double xDerivativeAt(double x, double y) {
        return Math.cos(x + y);
    }

    @Override
    public double yDerivativeAt(double x, double y) {
        return Math.cos(x + y);
    }
}
