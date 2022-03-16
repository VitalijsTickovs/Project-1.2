package Data_storage;

public class Terrain {
    //This is generated after Terrain is created
    public double[][] meshGrid2;
    public float[] meshGrid;

    public float[] heightmap;

    //All of the data below should be included, when Terrain is created
    public Zone zones[] = new Zone[0];
    public IObstacle[] obstacles = new IObstacle[0];
    public Target target;
    public Vector2 ballStartingPosition;
    
    //The corners of the whole map. The function is evaluated in this rectangle
    public Vector2 startingCorner;
    public Vector2 limitingCorner;

    public double staticFriction;
    public double kineticFriction;

    public TerrainFunction terrainFunction;
    public double scaleFactor;

    public int xRes, yRes;

    public Terrain(){

    }

    public Terrain(String function, double staticFriction, double kineticFriction, Vector2 startingCorner, Vector2 limitingCorner) {
        this.terrainFunction = new TerrainFunction1(function);
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
        this.startingCorner = startingCorner;
        this.limitingCorner = limitingCorner;
        this.heightmap = null;
    }

    public void calculateHeightMap(int numVerteces, double normalFactor) {
        heightmap = new float[numVerteces * numVerteces];
        int pos = 0;
        float minVal = -10;
        float maxVal = 10;
        double xOff = (limitingCorner.x - startingCorner.x)/numVerteces;
        double yOff = (limitingCorner.y - startingCorner.y)/numVerteces;
        for (int x = 0; x < numVerteces; x++) {
            for (int y = 0; y < numVerteces; y++) {
                double xx = startingCorner.x + x * xOff;
                double yy = startingCorner.y + y * yOff;
                float val = (float) terrainFunction.valueAt(xx, yy);
                if (val > maxVal) {
                    maxVal = val;
                }
                if (val < minVal) {
                    minVal = val;
                }

                val += Math.abs(minVal);
                val /= maxVal - minVal;
                if (val < 0) {
                    val = 0;
                }
                if (val > 1) {
                    val = 1;
                }
                val *= normalFactor;

                heightmap[pos] = val;
                pos++;
            }
        }
    }

    public void print(){
        System.out.println("Mesh grid:");
        System.out.print("Starting position: ");
        System.out.println(ballStartingPosition);
        System.out.print("Starting corner: ");
        System.out.println(startingCorner);
        System.out.print("Limiting corner: ");
        System.out.println(limitingCorner);
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