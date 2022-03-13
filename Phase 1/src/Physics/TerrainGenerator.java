package Physics;

import Data_storage.*;

public class TerrainGenerator {

    private static double step;
    private static int xRepetitions;
    private static int yRepetitions;
    public static TerrainFunction terrainFunction;

    public static void main(String[] args) {
        Terrain terrain = new Terrain();
        terrain.meshGrid = generateMeshGrid();
    }

    public static double[][] generateMeshGrid() {
        double[][] meshGrid = new double[xRepetitions][yRepetitions];

        for (int x = 0; x < xRepetitions; x++) {
            for (int y = 0; y < yRepetitions; y++) {

                double xCoord = x * step;
                double yCoord = y * step;
                meshGrid[x][y] = terrainFunction.valueAt(xCoord, yCoord);
            }
        }
        return meshGrid;
    }

}
