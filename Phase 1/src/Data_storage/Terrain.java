package Data_storage;

public class Terrain {
    
    public double[][] meshGrid;
    public Zone zones[];
    public IObstacle[] obstacles;
    public Target target;

    public Vector2 ballStartingPoisition;

    public double staticFriction;
    public double kineticFriction;

    public TerrainFunction terrainFunction;
}