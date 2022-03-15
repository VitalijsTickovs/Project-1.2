package Data_storage;

import function.Function;

public class TerrainFunction1 extends TerrainFunction {
    /*private Function f;
    private Function dfx;
    private Function dfy;*/

    private function.Function f;
    private function.Function dfx;
    private function.Function dfy;

    public TerrainFunction1(String function) {
        /*f = new Function("f(x, y) = "+function);
        dfx = new Function("dfx(x, y) = der("+function+", x)");
        dfy = new Function("dfy(x, y) = der("+function+", y)");*/
        f = new Function(function);
        dfx = f.getDerivative("x");
        dfy = f.getDerivative("y");
    }

    public float[] generateHeightMap(int numVerteces, double normalFactor){
        final double MINY = -10;
        final double MAXY = 10;
        float[] heightMap = new float[numVerteces*numVerteces];
        int pos = 0;
        // Loop through the points
        for (int x=-numVerteces/2; x<numVerteces/2; x++) {
            for (int y=-numVerteces/2; y<numVerteces/2; y++) {
                double val = valueAt(x, y);
                // Ensure min is 0
                val += Math.abs(MINY);
                // Normalize values
                val /= MAXY-MINY;
                // Clamp out of range
                if (val > 1) {
                    val = 1;
                }
                if (val < 0) {
                    val = 0;
                }
                // Scale with normal factor
                val *= normalFactor;
                heightMap[pos] = (float) val;
                pos++;
            }
        }
        return heightMap;
    }

    /**
     * Gets the height map of the terrain function
     * 
     * @param numVertecesX The number of verteces to include on the x-axis
     * @param numVertecesY The number of verteces to include on the y-axis
     * @param normalFactor The factor to scale the normalization by
     * @return The height map as a 1D array
     */
    @Override
    public float[] getHeightMap(int numVertecesX, int numVertecesY, double normalFactor) {
        final double MINY = -10;
        final double MAXY = 10;
        float[] heightMap = new float[numVertecesX * numVertecesY];
        int pos = 0;
        // Loop through the points
        for (int x = -numVertecesX / 2; x < numVertecesX / 2; x++) {
            for (int y = -numVertecesY / 2; y < numVertecesY / 2; y++) {
                double val = valueAt(x, y);
                // Ensure min is 0
                val += Math.abs(MINY);
                // Normalize values
                val /= MAXY - MINY;
                // Clamp out of range
                if (val > 1) {
                    val = 1;
                }
                if (val < 0) {
                    val = 0;
                }
                // Scale with normal factor
                val *= normalFactor;
                heightMap[pos] = (float) val;
                pos++;
            }
        }
        return heightMap;
    }

    @Override
    public double valueAt(double x, double y) {
        /* xx = new Argument("x = "+x);
        Argument yy = new Argument("y = "+y);
        Expression e = new Expression("f(x, y)", f, xx, yy);
        return e.calculate();*/
        return f.evaluate(new String[] {"x", "y"}, new double[] {x, y});
    }

    @Override
    public double xDerivativeAt(double x, double y) {
        /*Argument xx = new Argument("x = "+x);
        Argument yy = new Argument("y = "+y);
        Expression e = new Expression("dfx(x, y)", dfx, xx, yy);
        return e.calculate();*/
        return dfx.evaluate(new String[] {"x", "y"}, new double[] {x, y});
    }

    @Override
    public double yDerivativeAt(double x, double y) {
        /*Argument xx = new Argument("x = "+x);
        Argument yy = new Argument("y = "+y);
        Expression e = new Expression("dfy(x, y)", dfy, xx, yy);
        return e.calculate();*/
        return dfy.evaluate(new String[] {"x", "y"}, new double[] {x, y});
    }

    @Override
    public String toString() {
        return f.toString();
    }
}
