package Data_storage;

public class Terrain {
    //This is generated after Terrain is created
    public double[][] meshGrid;

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
}