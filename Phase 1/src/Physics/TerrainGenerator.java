package Physics;

import Data_storage.*;

public class TerrainGenerator {

    private static double step = 0.05;
    private static int xRepetitions = 100;
    private static int yRepetitions = 100;

    public static void generateMeshGrid(Terrain terrain) {
        double[][] meshGrid = new double[xRepetitions][yRepetitions];

        for (int x = 0; x < xRepetitions; x++) {
            for (int y = 0; y < yRepetitions; y++) {

                double xCoord = x * step;
                double yCoord = y * step;
                meshGrid[x][y] = terrain.terrainFunction.valueAt(xCoord, yCoord);
            }
        }
        terrain.meshGrid2 = meshGrid;
    }

        

    /**
     * Gets the height map of the terrain function
     * @param numVertecesX The number of verteces to include on the x-axis
     * @param numVertecesY The number of verteces to include on the y-axis
     * @param normalFactor The factor to scale the normalization by
     * @return The height map as a 1D array
     */
    public float[] getHeightMap(Terrain terrain,int numVerteces, double normalFactor) {
        final double MINY = -10;
        final double MAXY = 10;
        float[] heightMap = new float[numVerteces*numVerteces];
        int pos = 0;
        // Loop through the points
        for (int x=-numVerteces/2; x<numVerteces/2; x++) {
            for (int y=-numVerteces/2; y<numVerteces/2; y++) {
                double val = terrain.terrainFunction.valueAt(x, y);
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

}
