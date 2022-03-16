package Data_storage;

import function.Function;

public class TerrainFunction1 extends TerrainFunction {
    /*
     * private Function f;
     * private Function dfx;
     * private Function dfy;
     */

    private function.Function f;
    private function.Function dfx;
    private function.Function dfy;

    public TerrainFunction1(String function) {
        /*
         * f = new Function("f(x, y) = "+function);
         * dfx = new Function("dfx(x, y) = der("+function+", x)");
         * dfy = new Function("dfy(x, y) = der("+function+", y)");
         */
        f = new Function(function);
        dfx = f.getDerivative("x");
        dfy = f.getDerivative("y");
    }

    @Override
    public double valueAt(double x, double y) {
        /*
         * xx = new Argument("x = "+x);
         * Argument yy = new Argument("y = "+y);
         * Expression e = new Expression("f(x, y)", f, xx, yy);
         * return e.calculate();
         */
        return f.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }

    @Override
    public double xDerivativeAt(double x, double y) {
        /*
         * Argument xx = new Argument("x = "+x);
         * Argument yy = new Argument("y = "+y);
         * Expression e = new Expression("dfx(x, y)", dfx, xx, yy);
         * return e.calculate();
         */
        return dfx.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }

    @Override
    public double yDerivativeAt(double x, double y) {
        /*
         * Argument xx = new Argument("x = "+x);
         * Argument yy = new Argument("y = "+y);
         * Expression e = new Expression("dfy(x, y)", dfy, xx, yy);
         * return e.calculate();
         */
        return dfy.evaluate(new String[] { "x", "y" }, new double[] { x, y });
    }

    @Override
    public String toString() {
        return "h(x,y) = " + f.toString() + "\n" +
                "dh/dx = " + dfx.toString() + "\n" +
                "dh/dy = " + dfy.toString();
    }
}
