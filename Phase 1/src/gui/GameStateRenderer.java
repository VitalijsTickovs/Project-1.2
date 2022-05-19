package gui;

import datastorage.*;
import datastorage.obstacles.*;
import gameengine.Camera;
import utility.UtilityClass;
import utility.math.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GameStateRenderer {
    /**
     * Only contains the green. Displays the entire map.
     */
    private final GameState gameState;
    public final int PIXELS_PER_GAME_UNIT = 40;
    /**
     * Contains the green with all obstacles, zones and the target added. Displays
     * the entire map.
     */
    private final BufferedImage STATIC_TERRAIN_IMAGE;

    public GameStateRenderer(GameState gameState) {
        this.gameState = gameState;

        STATIC_TERRAIN_IMAGE = getGreenWithObstacles();
    }

    // region Startup
    private BufferedImage getGreenWithObstacles() {
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;

        BufferedImage imageOfGreenWithObstacles = getImageOfGreen();
        Graphics2D g2 = (Graphics2D) imageOfGreenWithObstacles.getGraphics();

        drawTarget(g2, heightRange);
        drawObstacles(g2);
        return imageOfGreenWithObstacles;
    }

    private void drawTarget(Graphics2D g2, double heightRange) {
        Terrain terrain = gameState.getTerrain();
        double radius = terrain.target.radius;
        drawCircle(g2, terrain.target.position.x, terrain.target.position.y, radius, Color.BLACK, false);

    }

    private void drawObstacles(Graphics2D g2) {
        Terrain terrain = gameState.getTerrain();
        Color obstacleColor = new Color(96, 69, 38);

        for (IObstacle o : terrain.obstacles) {
            // Trees
            if (o instanceof ObstacleTree) {
                ObstacleTree t = (ObstacleTree) o;
                drawCircle(g2, t.originPosition.x, t.originPosition.y, t.radius, obstacleColor, true);
            } else if (o instanceof ObstacleBox) {
                ObstacleBox b = (ObstacleBox) o;
                drawRectangle(g2, b.bottomLeftCorner, b.topRightCorner, obstacleColor, true);
            }
        }
    }

    public BufferedImage getImageOfGreen() {
        Terrain terrain = gameState.getTerrain();

        BufferedImage imageOfGreen = new BufferedImage(getTerrainWidthInPixels(), getTerrainHeightInPixels(),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) imageOfGreen.getGraphics();
        // Number of units in colored space
        // Clear screen
        Color brownBackground = new Color(75, 47, 26);
        g2.setColor(brownBackground);
        g2.fillRect(0, 0, imageOfGreen.getWidth(), imageOfGreen.getHeight());
        // Render the terrain
        int numVertices = (int) Math.sqrt(terrain.heightmap.length);
        double xStep = (terrain.bottomRightCorner.x - terrain.topLeftCorner.x) / numVertices;
        double yStep = (terrain.bottomRightCorner.y - terrain.topLeftCorner.y) / numVertices;
        // Find the max and min height in the terrain
        double[] minMax = getMaxAndMinTerrainHeight();
        double totalMinHeight = minMax[0];
        double totalMaxHeight = minMax[1];

        // Make sure that the deepest part isn't pitch black
        totalMinHeight -= 1.5;
        // Find the rendering coordinates
        for (int yy = 0; yy < numVertices - 1; yy++) {
            for (int xx = 0; xx < numVertices - 1; xx++) {
                // First point
                double x1 = terrain.topLeftCorner.x + xx * xStep;
                double y1 = terrain.topLeftCorner.y + yy * yStep;
                double h1 = UtilityClass.clamp(terrain.terrainFunction.valueAt(x1, y1), -10, 10);
                // Second point
                double x2 = terrain.topLeftCorner.x + (xx + 1) * xStep;
                double y2 = terrain.topLeftCorner.y + yy * yStep;
                double h2 = UtilityClass.clamp(terrain.terrainFunction.valueAt(x2, y2), -10, 10);
                // Third point
                double x3 = terrain.topLeftCorner.x + xx * xStep;
                double y3 = terrain.topLeftCorner.y + (yy + 1) * yStep;
                double h3 = UtilityClass.clamp(terrain.terrainFunction.valueAt(x3, y3), -10, 10);

                // Fourth point
                double x4 = terrain.topLeftCorner.x + (xx + 1) * xStep;
                double y4 = terrain.topLeftCorner.y + (yy + 1) * yStep;
                double h4 = UtilityClass.clamp(terrain.terrainFunction.valueAt(x4, y4), -10, 10);

                double heightRange = getTerrainHeightRange();
                double maxHeight = UtilityClass.getMaxValue(new double[] { h1, h2, h3, h4 });

                int renderPixelX1 = (int) ((x1 - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
                int renderPixelY1 = (int) ((y1 - h1 - terrain.topLeftCorner.y + heightRange / 2)
                        * PIXELS_PER_GAME_UNIT);

                int renderPixelX2 = (int) ((x2 - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
                int renderPixelY2 = (int) ((y2 - h2 - terrain.topLeftCorner.y + heightRange / 2)
                        * PIXELS_PER_GAME_UNIT);

                int renderPixelX3 = (int) ((x3 - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
                int renderPixelY3 = (int) ((y3 - h3 - terrain.topLeftCorner.y + heightRange / 2)
                        * PIXELS_PER_GAME_UNIT);

                int renderPixelX4 = (int) ((x4 - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
                int renderPixelY4 = (int) ((y4 - h4 - terrain.topLeftCorner.y + heightRange / 2)
                        * PIXELS_PER_GAME_UNIT);

                double lightLevel = (maxHeight - totalMinHeight) / (totalMaxHeight - totalMinHeight);

                Square square = new Square();
                square.lightLevel = lightLevel;
                square.maxHeight = maxHeight;
                square.squarePosition = new Vector2(x1, y1);

                square.pixel1X = renderPixelX1;
                square.pixel1Y = renderPixelY1;
                square.pixel2X = renderPixelX2;
                square.pixel2Y = renderPixelY2;
                square.pixel3X = renderPixelX3;
                square.pixel3Y = renderPixelY3;
                square.pixel4X = renderPixelX4;
                square.pixel4Y = renderPixelY4;

                drawSquare(g2, square);
            }
        }
        return imageOfGreen;
    }

    // region GetImageOfGreen helper methods
    private void drawSquare(Graphics2D g2, Square square) {
        g2.setColor(getSquareColor(square));
        g2.fillPolygon(new int[] { square.pixel1X, square.pixel2X, square.pixel4X, square.pixel3X },
                new int[] { square.pixel1Y, square.pixel2Y, square.pixel4Y, square.pixel3Y }, 4);
    }

    private Color getSquareColor(Square square) {
        Vector2 squarePosition = square.squarePosition;
        Terrain terrain = gameState.getTerrain();

        if (square.maxHeight < 0) {
            return calculateWaterColor(square.lightLevel);
        }
        if (terrain.isPointInZone(squarePosition.x, squarePosition.y)) {
            return calculateSandColor(square);
        }
        return calculateGrassColor(square);
    }

    private Color calculateSandColor(Square square) {
        Vector2 squarePosition = square.squarePosition;
        double lightLevel = square.lightLevel;

        if (isPixelTinted(squarePosition.x, squarePosition.y)) {
            return new Color((int) (150 * lightLevel), (int) (150 * lightLevel), 0);
        } else {
            return new Color((int) (100 * lightLevel), (int) (100 * lightLevel), 0);
        }
    }

    private Color calculateGrassColor(Square square) {
        Vector2 squarePosition = square.squarePosition;
        double lightLevel = square.lightLevel;

        if (isPixelTinted(squarePosition.x, squarePosition.y)) {
            return new Color(0, (int) (200 * lightLevel), 0);
        } else {
            return new Color(0, (int) (180 * lightLevel), 0);
        }
    }

    private Color calculateWaterColor(double lightLevel) {
        return new Color((int) (34 * lightLevel), (int) (124 * lightLevel), (int) (176 * lightLevel));
    }

    private boolean isPixelTinted(double xPos, double yPos) {
        Terrain terrain = gameState.getTerrain();
        // The lenfth of one of the sides of a tinted square in game units
        int TINTED_SQUARE_SIZE = 4;

        int numBlockX = (int) ((xPos - terrain.topLeftCorner.x) / TINTED_SQUARE_SIZE);
        int numBlockY = (int) ((yPos - terrain.topLeftCorner.y) / TINTED_SQUARE_SIZE);
        return (numBlockX + numBlockY) % 2 == 0;
    }

    private double[] getMaxAndMinTerrainHeight() {
        Terrain terrain = gameState.getTerrain();
        // First value is min height, second is max height
        double[] minMax = new double[2];

        int numVertices = (int) Math.sqrt(terrain.heightmap.length);
        double xStep = (terrain.bottomRightCorner.x - terrain.topLeftCorner.x) / numVertices;
        double yStep = (terrain.bottomRightCorner.y - terrain.topLeftCorner.y) / numVertices;
        // Find the max and min height in the terrain
        double totalMaxHeight = -10;
        double totalMinHeight = 10;
        for (int yy = 0; yy < numVertices; yy++) {
            for (int xx = 0; xx < numVertices; xx++) {
                double x = terrain.topLeftCorner.x + xx * xStep;
                double y = terrain.topLeftCorner.y + yy * yStep;
                double h = terrain.terrainFunction.valueAt(x, y);
                if (h > 10) {
                    h = 10;
                }
                if (h < -10) {
                    h = -10;
                }
                if (h > totalMaxHeight) {
                    totalMaxHeight = h;
                }
                if (h < totalMinHeight) {
                    totalMinHeight = h;
                }
            }
        }
        minMax[0] = totalMinHeight;
        minMax[1] = totalMaxHeight;
        return minMax;
    }
    // endregion
    // endregion

    public BufferedImage getSubimage(Camera camera) {
        BufferedImage image = getEmptyTerrainImage(camera);
        Graphics2D imageG2 = (Graphics2D) image.getGraphics();
        // Fill the given part of the Graphics2D object in brown
        imageG2.setColor(new Color(75, 47, 26));
        imageG2.fillRect(0, 0, (int) (camera.WIDTH * PIXELS_PER_GAME_UNIT),
                (int) (camera.HEIGHT * PIXELS_PER_GAME_UNIT));
        // Render a part of the image
        // Render the terrain
        double camTLx = camera.xPos - camera.WIDTH / 2;
        double camTLy = camera.yPos - camera.HEIGHT / 2;
        double camBRx = camera.xPos + camera.WIDTH / 2;
        double camBRy = camera.yPos + camera.HEIGHT / 2;

        int xTL = -1, yTL = -1, xBR = -1, yBR = -1;

        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;

        // Check if the terrain is on-screen and find the range of the drawing part
        if (camTLx < terrain.bottomRightCorner.x && camTLy < terrain.bottomRightCorner.y
                &&
                camBRx > terrain.topLeftCorner.x && camBRy > terrain.topLeftCorner.y) {

            if (camTLx < terrain.topLeftCorner.x) {
                xTL = 0;
            } else {
                // Calculate x top left
                xTL = (int) ((camTLx - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
            }

            if (camTLy < terrain.topLeftCorner.y - heightRange / 2) {
                yTL = 0;
            } else {
                // Calculate y top left
                yTL = (int) ((camTLy - (terrain.topLeftCorner.y - heightRange / 2)) * PIXELS_PER_GAME_UNIT);
            }

            if (camBRx > terrain.bottomRightCorner.x) {
                xBR = STATIC_TERRAIN_IMAGE.getWidth();
            } else {
                // Calculate x bottom right
                xBR = (int) ((camBRx - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
            }

            if (camBRy > terrain.bottomRightCorner.y + heightRange / 2) {
                yBR = STATIC_TERRAIN_IMAGE.getHeight();
            } else {
                // Calculate y bottom right
                yBR = (int) ((camBRy - (terrain.topLeftCorner.y - heightRange / 2)) * PIXELS_PER_GAME_UNIT);
            }
        }

        if (xTL != -1) {
            int xDraw = (int) ((terrain.topLeftCorner.x - camTLx) * PIXELS_PER_GAME_UNIT);
            int yDraw = (int) ((terrain.topLeftCorner.y - heightRange / 2 - camTLy) * PIXELS_PER_GAME_UNIT);
            if (xDraw < 0) {
                xDraw = 0;
            }
            if (yDraw < 0) {
                yDraw = 0;
            }
            BufferedImage subImage = STATIC_TERRAIN_IMAGE.getSubimage(xTL, yTL, xBR - xTL, yBR - yTL);
            imageG2.drawImage(subImage, null, 0, 0);
            subImage.flush();
        }
        // Render the flag and ball, order depending on position
        if (terrain.target.position.y > gameState.getBall().state.position.y) {
            renderBall(imageG2, camera);
            renderFlag(imageG2, camera);
        } else {
            renderFlag(imageG2, camera);
            renderBall(imageG2, camera);
        }
        // Draw ball position
        drawText(imageG2);

        return image;
    }

    private void drawText(Graphics2D imageG2) {
        imageG2.setFont(new Font("TimesRoman", Font.BOLD, PIXELS_PER_GAME_UNIT / 2));
        imageG2.setColor(Color.WHITE);
        BigDecimal xx = new BigDecimal(gameState.getBall().state.position.x);
        xx = xx.setScale(5, RoundingMode.HALF_UP);
        BigDecimal yy = new BigDecimal(gameState.getBall().state.position.y);
        yy = yy.setScale(5, RoundingMode.HALF_UP);
        imageG2.drawString("x = " + xx, PIXELS_PER_GAME_UNIT, PIXELS_PER_GAME_UNIT);
        imageG2.drawString("y = " + yy, PIXELS_PER_GAME_UNIT, 2 * PIXELS_PER_GAME_UNIT);
        // Dispose of the graphics
        imageG2.dispose();
    }

    public BufferedImage getEmptyTerrainImage(Camera camera) {
        return new BufferedImage((int) (camera.WIDTH * PIXELS_PER_GAME_UNIT),
                (int) (camera.HEIGHT * PIXELS_PER_GAME_UNIT),
                BufferedImage.TYPE_4BYTE_ABGR);
    }

    private void renderBall(Graphics2D g2, Camera camera) {
        Ball ball = gameState.getBall();
        double z = ball.getZCoordinate(gameState.getTerrain());
        int x = (int) ((ball.state.position.x - camera.xPos + camera.WIDTH / 2 - ball.radius) * PIXELS_PER_GAME_UNIT);
        int y = (int) ((ball.state.position.y - z - camera.yPos + camera.HEIGHT / 2 - ball.radius)
                * PIXELS_PER_GAME_UNIT);
        g2.setColor(Color.WHITE);
        g2.fillArc(x, y, (int) (ball.radius * PIXELS_PER_GAME_UNIT * 2), (int) (ball.radius * PIXELS_PER_GAME_UNIT * 2),
                0,
                360);
    }

    private void renderFlag(Graphics2D g2, Camera camera) {
        Target target = gameState.getTerrain().target;
        double z = gameState.getTerrain().terrainFunction.valueAt(target.position.x, target.position.y);
        int x = (int) ((target.position.x - camera.xPos + camera.WIDTH / 2) * PIXELS_PER_GAME_UNIT);
        int y = (int) ((target.position.y - z - camera.yPos + camera.HEIGHT / 2 - 2) * PIXELS_PER_GAME_UNIT);
        g2.setColor(Color.WHITE);
        g2.drawLine(x, y, x, y + 2 * PIXELS_PER_GAME_UNIT);
        g2.setColor(Color.RED);
        g2.fillPolygon(new int[] { x, x + PIXELS_PER_GAME_UNIT / 2, x },
                new int[] { y, y + PIXELS_PER_GAME_UNIT / 4, y + PIXELS_PER_GAME_UNIT / 2 }, 3);
    }

    /**
     * Draws a circle onto the function terrain
     * 
     * @param g2     The Graphics2D object that the function is being drawn to
     * @param x      The x coordinate of the center of the circle
     * @param y      The y coordinate of the center of the circle
     * @param radius The radius of the circle
     * @param color  The color of the circle
     * @param filled Whether it's a filled circle or just an outline
     */
    private void drawCircle(Graphics2D g2, double x, double y, double radius, Color color, boolean filled) {
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;
        int[] xPoints = new int[361];
        int[] yPoints = new int[361];
        g2.setColor(color);
        int firstPointX = -1, firstPointY = -1;
        // Loop through 360 degrees and add the points
        for (int deg = 0; deg <= 360; deg++) {
            double xx = x + radius * Math.cos(deg / (2 * Math.PI));
            double yy = y + radius * Math.sin(deg / (2 * Math.PI));
            double h = terrain.terrainFunction.valueAt(xx, yy);
            if (h > 10) {
                h = 10;
            }
            if (h < -10) {
                h = -10;
            }
            int renderX = (int) ((xx - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
            int renderY = (int) ((yy - h - terrain.topLeftCorner.y + heightRange / 2) * PIXELS_PER_GAME_UNIT);
            xPoints[deg] = renderX;
            yPoints[deg] = renderY;
            if (firstPointX == -1) {
                firstPointX = renderX;
                firstPointY = renderY;
            }
        }
        // Add last point
        xPoints[360] = firstPointX;
        yPoints[360] = firstPointY;
        // Draw the polygon
        if (filled) {
            g2.fillPolygon(xPoints, yPoints, 361);
        } else {
            g2.drawPolygon(xPoints, yPoints, 361);
        }
    }

    private void drawRectangle(Graphics2D g2, Vector2 bottomLeftCorner, Vector2 topRightCorner, Color color,
            boolean filled) {
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        g2.setColor(color);

        fillListsWithRectangleCorners(xPoints, yPoints, bottomLeftCorner, topRightCorner);

        if (filled) {
            g2.fillPolygon(xPoints, yPoints, 4);
        } else {
            g2.drawPolygon(xPoints, yPoints, 4);
        }
    }

    private void fillListsWithRectangleCorners(int[] xPoints, int[] yPoints, Vector2 bottomLeftCorner,
            Vector2 topRightCorner) {
        Vector2 bottomRightCornerTranslated = getPointValue(new Vector2(topRightCorner.x, bottomLeftCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 0, bottomRightCornerTranslated);

        Vector2 bottomLeftCornerTranslated = getPointValue(new Vector2(bottomLeftCorner.x, bottomLeftCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 1, bottomLeftCornerTranslated);

        Vector2 topLeftCornerTranslated = getPointValue(new Vector2(bottomLeftCorner.x, topRightCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 2, topLeftCornerTranslated);

        Vector2 topRightCornerTranslated = getPointValue(new Vector2(topRightCorner.x, topRightCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 3, topRightCornerTranslated);
    }

    private void assignTranslatedPoint(int[] xPoints, int[] yPoints, int index, Vector2 point) {
        xPoints[index] = (int) point.x;
        yPoints[index] = (int) point.y;
    }

    private Vector2 getPointValue(Vector2 point) {
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;
        double xx = point.x;
        double yy = point.y;
        double h = terrain.terrainFunction.valueAt(xx, yy);
        int renderX = (int) ((xx - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT);
        int renderY = (int) ((yy - h - terrain.topLeftCorner.y + heightRange / 2) * PIXELS_PER_GAME_UNIT);
        return new Vector2(renderX, renderY);
    }

    private int getTerrainWidthInPixels() {
        Terrain terrain = gameState.getTerrain();
        return (int) (terrain.bottomRightCorner.x - terrain.topLeftCorner.x) * PIXELS_PER_GAME_UNIT;

    }

    private int getTerrainHeightInPixels() {
        Terrain terrain = gameState.getTerrain();
        return (int) (terrain.bottomRightCorner.y - terrain.topLeftCorner.y) * PIXELS_PER_GAME_UNIT;

    }

    private double getTerrainHeightRange() {
        Terrain terrain = gameState.getTerrain();

        return terrain.maxVal - terrain.minVal;

    }

    public class Square {
        /**
         * Game units
         */
        public Vector2 squarePosition;

        // Pixel position in pixel units
        public int pixel1X;
        public int pixel1Y;
        public int pixel2X;
        public int pixel2Y;
        public int pixel3X;
        public int pixel3Y;
        public int pixel4X;
        public int pixel4Y;

        /**
         * The highest value of one of the corners
         */
        public double maxHeight;

        public double lightLevel;
    }
}
