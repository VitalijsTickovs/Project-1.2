package Data_storage;

import function.Function;
// import org.mariuszgromada.math.mxparser.*;

public class TerrainFunction1 extends TerrainFunction {
    /*
     * private Function f;
     * private Function dfx;
     * private Function dfy;
     */

    public function.Function f;
    public function.Function dfx;
    public function.Function dfy;

    public TerrainFunction1(String function) {
        f = new Function(function);
        dfx = f.getDerivative("x");
        dfy = f.getDerivative("y");
    }

    @Override
    public double valueAt(double x, double y) {
        return f.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }


    @Override
    public double xDerivativeAt(double x, double y) {
        return dfx.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }

    @Override
    public double yDerivativeAt(double x, double y) {
        return dfy.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }

    @Override
    public String toString() {
        return "h(x,y) = " + f.toString() + "\n" +
                "dh/dx = " + dfx.toString() + "\n" +
                "dh/dy = " + dfy.toString();
    }
}
