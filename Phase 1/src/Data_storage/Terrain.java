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

    public Terrain(String function, Vector2 startingCorner, Vector2 limitingCorner, double staticFriction, double kineticFriction, int xRes, int yRes, double normalFactor) {
        this.terrainFunction = new TerrainFunction1(function);
        this.startingCorner = startingCorner;
        this.limitingCorner = limitingCorner;
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
        this.xRes = xRes;
        this.yRes = yRes;
        scaleFactor = normalFactor;
        calculateHeightMap(this.xRes, this.yRes, normalFactor);
    }

    public void calculateHeightMap(int numVertecesX, int numVertecesY, double normalFactor) {
        heightmap = new float[numVertecesX * numVertecesY];
        int pos = 0;
        float minVal = -10;
        float maxVal = 10;
        double xOff = (limitingCorner.x - startingCorner.x) / (double) numVertecesX;
        double yOff = (limitingCorner.y - startingCorner.y) / (double) numVertecesY;
        for (int x = 0; x < numVertecesX; x++) {
            for (int y = 0; y < numVertecesY; y++) {
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