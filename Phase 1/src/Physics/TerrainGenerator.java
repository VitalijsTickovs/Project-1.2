package Physics;

import Data_storage.*;

public class TerrainGenerator {

    private static double step;
    private static int xRepetitions;
    private static int yRepetitions;

    public static void generateMeshGrid(Terrain terrain) {
        double[][] meshGrid = new double[xRepetitions][yRepetitions];

        for (int x = 0; x < xRepetitions; x++) {
            for (int y = 0; y < yRepetitions; y++) {

                double xCoord = x * step;
                double yCoord = y * step;
                meshGrid[x][y] = terrain.terrainFunction.valueAt(xCoord, yCoord);
            }
        }
        terrain.meshGrid = meshGrid;
    }

}
