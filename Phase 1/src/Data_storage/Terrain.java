package Data_storage;

public class Terrain {
    
    public double[][] meshGrid;
    public Zone zones[];
    public IObstacle[] obstacles;

    public double staticFriction;
    public double kineticFriction;

    public TerrainFunction terrainFunction;
    
}