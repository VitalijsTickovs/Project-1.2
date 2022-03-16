package Physics;

import Data_storage.*;
import com.jme3.util.BufferUtils;

import java.util.Vector;

import com.jme3.scene.VertexBuffer.Type;

public class MeshGenerator {

    public static Vector3[][] generateVertices(Terrain terrain, double resolution) {
        double xSpan = terrain.limitingCorner.x - terrain.startingCorner.x;
        double ySpan = terrain.limitingCorner.y - terrain.startingCorner.y;
        int xRepetitions = (int) (xSpan / resolution) + 1;
        int yRepetitions = (int) (ySpan / resolution) + 1;
        Vector3[][] vertices = new Vector3[yRepetitions][xRepetitions];

        for (int x = 0; x < xRepetitions; x++) {
            for (int y = 0; y < yRepetitions; y++) {
                double xCoord = x * resolution;
                double yCoord = y * resolution;
                double zCoord = terrain.terrainFunction.valueAt(xCoord, yCoord);
                vertices[y][x] = new Vector3(xCoord, yCoord, zCoord);
            }
        }
        return vertices;
    }

    public static int[] generateTriangles(Vector3[][] vertices) {
        int xRepetitions = vertices[0].length - 1;
        int yRepetitions = vertices.length - 1;
        int triangleCount = (xRepetitions) * (yRepetitions) * 2;
        // Each triangle is saved as three vectors
        int[] triangles = new int[triangleCount * 3];

        int squareInsertPosition = 0;
        for (int y = 0; y < yRepetitions; y++) {
            for (int x = 0; x < xRepetitions; x++) {
                int[] square = createSquare(vertices, x, y);
                addSquare(triangles, square, squareInsertPosition);
                squareInsertPosition += 6;
            }
        }
        return triangles;
    }

    private static int[] createSquare(Vector3[][] vertices, int x, int y) {
        int xLength = vertices[0].length;
        int[] square = new int[6];
        // First triangle
        square[0] = xLength * y + x; // 3 -
        square[1] = xLength * y + x + 1;// 1 2
        square[2] = xLength * y + x + xLength;
        // Second triangle
        square[3] = xLength * y + x + 1; // 1 2
        square[4] = xLength * y + x + xLength + 1;
        square[5] = xLength * y + x + xLength;

        return square;
    }

    /**
     * 
     * @param square an array of 6 elements
     */
    private static void addSquare(int[] triangles, int[] square, int atPosition) {
        for (int i = 0; i < 6; i++) {
            triangles[atPosition + i] = square[i];
        }
    }

    private static Vector3[] make2DVectorArray(Vector3[][] matrix) {
        int xLength = matrix[0].length;
        int yLength = matrix.length;
        Vector3[] vectorArr = new Vector3[xLength * yLength];

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                vectorArr[xLength * y + x] = matrix[y][x];
            }
        }
        return vectorArr;
    }

    private static Vector3f[] translateTojMonke(Vector3[] vectorArr) {
        Vector3f[] vertices = new Vector3f[vectorArr.length];
        for (int i = 0; i < vectorArr.length; i++) {
            vertices[i] = new Vector3f(vectorArr[i].x, vectorArr[i].y, vectorArr[i].z);
        }
    }

    public static void main(String[] args) {
        TerrainFunction t = new TerrainFunction1("x + y + 1");
        Terrain terrain = new Terrain();
        terrain.terrainFunction = t;
        terrain.startingCorner = new Vector2(0, 0);
        terrain.limitingCorner = new Vector2(2, 3);

        Vector3[][] vertices = generateVertices(terrain, 1);

        int[] triangles = generateTriangles(vertices);
        Vector3[] vectorArr = make2DVectorArray(vertices);
        Print.printSquare(triangles);

        // Translate into jMonkey vectors
        Vector3f[] translatedVectors = translateTojMonke(vectorArr);

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(translatedVectors));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(triangles));
        mesh.updateBound();
    }
}
