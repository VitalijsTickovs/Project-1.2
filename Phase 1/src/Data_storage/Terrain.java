package Data_storage;

public class Terrain {
    //This is generated after Terrain is created
    public double[][] meshGrid2;
    public double[] meshGrid;

    //All of the data below should be included, when Terrain is created
    public Zone zones[];
    public IObstacle[] obstacles;
    public Target target;
    public Vector2 ballStartingPoisition;
    
    //The corners of the whole map. The function is evaluated in this rectangle
    public Vector2 startingCorner;
    public Vector2 limitingCorner;

    public double staticFriction;
    public double kineticFriction;

    public TerrainFunction terrainFunction;

    public void print(){
        System.out.println("Mesh grid:");
        System.out.print("Starting position: ");
        System.out.println(ballStartingPoisition);
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