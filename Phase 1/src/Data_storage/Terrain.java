package Data_storage;

// import org.mariuszgromada.math.mxparser.Expression;
import function.Function;

public class Terrain {
    //This is generated after Terrain is created
    public double[][] meshGrid2;
    public float[] meshGrid;

    public float[] heightmap;

    //All of the data below should be included, when Terrain is created
    public Zone[] zones = new Zone[0];
    public IObstacle[] obstacles = new IObstacle[0];
    public Target target;
    public Vector2 ballStartingPosition;
    
    //The corners of the whole map. The function is evaluated in this rectangle
    public Vector2 topLeftCorner;
    public Vector2 bottomRightCorner;

    public double staticFriction;
    public double kineticFriction;

    public TerrainFunction1 terrainFunction;
    public double scaleFactor = 1;

    public int xRes = 500;
    public int yRes = 500;

    public Terrain(){
    }

    public Terrain(String function, double staticFriction, double kineticFriction, Vector2 startingCorner, Vector2 limitingCorner) {
        this.terrainFunction = new TerrainFunction1(function);
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
        this.topLeftCorner = startingCorner;
        this.bottomRightCorner = limitingCorner;
        this.heightmap = null;
    }

    public boolean isPointInZone(double x, double y) {
        for (Zone z : zones) {
            if (z.isPositionInside(new Vector2(x, y))) {
                return true;
            }
        }
        return false;
    }

    public boolean isPointsInObstacle(Vector2 point){
        for (IObstacle obstacle : obstacles) {
            if (obstacle.isPositionColliding(point)) {
                return true;
            }
        }
        return false;
    }

    public void addZone(Vector2 bottomLeft, Vector2 topRight, double zoneStaticFriction, double zoneKineticFriction) {
        Zone[] temp = new Zone[zones.length+1];
        for (int i=0; i<zones.length; i++) {
            temp[i] = zones[i];
        }
        Zone z = new Zone(bottomLeft.copy(),topRight.copy());
        z.staticFriction = zoneStaticFriction;
        z.kineticFriction = zoneKineticFriction;
        temp[temp.length-1] = z;
        zones = temp;
    }

    public float minVal = -10;
    public float maxVal = 10;
    public double xOff;
    public double yOff;

    public boolean isValid(int accuracy) {
        double xStep = (bottomRightCorner.x - topLeftCorner.x)/accuracy;
        double yStep = (bottomRightCorner.y - topLeftCorner.y)/accuracy;

        Function dfxx = terrainFunction.dfx.getDerivative("x");
        Function dfyy = terrainFunction.dfy.getDerivative("y");
        Function dfxy = terrainFunction.dfx.getDerivative("y");

        for (int xx=0; xx<accuracy; xx++) {
            for (int yy=0; yy<accuracy; yy++) {
                double x = topLeftCorner.x + xx*xStep;
                double y = topLeftCorner.y + yy*yStep;

                double fVal = Math.abs(terrainFunction.valueAt(x, y));
                double dfxVal = Math.abs(terrainFunction.xDerivativeAt(x, y));
                double dfyVal = Math.abs(terrainFunction.yDerivativeAt(x, y));
                double dfxxVal = Math.abs(dfxx.evaluate(new String[] {"x", "y"}, new double[] {x, y}));
                double dfyyVal = Math.abs(dfyy.evaluate(new String[] {"x", "y"}, new double[] {x, y}));
                double dfxyVal = Math.abs(dfxy.evaluate(new String[] {"x", "y"}, new double[] {x, y}));

                if (fVal > 10 || dfxVal > 0.15 || dfyVal > 0.15 ||
                    dfxxVal > 0.1 || dfyyVal > 0.1 || dfxyVal > 0.1) {
                    return false;
                }
            }
        }

        return true;
    }

    public void calculateHeightMap(int numVerteces, double normalFactor) {
        heightmap = new float[numVerteces * numVerteces];
        int pos = 0;
        this.xOff = (bottomRightCorner.x - topLeftCorner.x)/numVerteces;
        this.yOff = (bottomRightCorner.y - topLeftCorner.y)/numVerteces;
        for (int x = 0; x < numVerteces; x++) {
            for (int y = 0; y < numVerteces; y++) {

                double xx = topLeftCorner.x + x * this.xOff;
                double yy = topLeftCorner.y + y * this.yOff;
                float val = (float) terrainFunction.valueAt(xx, yy);
                if (val > maxVal) {
                    maxVal = val;
                }
                if (val < minVal) {
                    minVal = val;
                }
                heightmap[pos] = val;
                pos++;
            }
        }
        for (int i=0; i<heightmap.length; i++) {
            float val = heightmap[i];
            val += Math.abs(minVal);
            val /= maxVal - minVal;
            if (val < 0) {
                val = 0;
            }
            if (val > 1) {
                val = 1;
            }
            val *= normalFactor;
            heightmap[i] = val;
        }
    }

    public void print(){
        System.out.println("Mesh grid:");
        System.out.print("Starting position: ");
        System.out.println(ballStartingPosition);
        System.out.print("Starting corner: ");
        System.out.println(topLeftCorner);
        System.out.print("Limiting corner: ");
        System.out.println(bottomRightCorner);
        System.out.print("Kinetic friction: ");
        System.out.println(kineticFriction);
        System.out.print("Static friction: ");
        System.out.println(staticFriction);
        

        Print.printSquare(meshGrid);
        for (Zone zone : zones) {
            zone.print();
        }
        for (IObstacle obstacle : obstacles) {
            obstacle.print();
        }
        target.print();
    }
}