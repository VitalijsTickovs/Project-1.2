package gui;

import datastorage.*;
import datastorage.obstacles.*;
import utility.math.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GameStateRenderer {
    private BufferedImage terrainImage;
    private final GameState gameState;
    private final int unitSizePixels;

    public GameStateRenderer(GameState gameState, int unitSizePixels) {
        this.gameState = gameState;
        this.unitSizePixels = unitSizePixels;
        createTerrainImage();
    }

    public BufferedImage getSubimage(double cameraX, double cameraY, double cameraWidth, double cameraHeight) {
        BufferedImage image = new BufferedImage((int) (cameraWidth*unitSizePixels), (int) (cameraHeight*unitSizePixels), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D imageG2 = (Graphics2D) image.getGraphics();
        // Fill the given part of the Graphics2D object in brown
        imageG2.setColor(new Color(75, 47, 26));
        imageG2.fillRect(0, 0, (int) (cameraWidth*unitSizePixels), (int) (cameraHeight*unitSizePixels));
        // Render a part of the image
        // Render the terrain
        double camTLx = cameraX - cameraWidth/2;
        double camTLy = cameraY - cameraHeight/2;
        double camBRx = cameraX + cameraWidth/2;
        double camBRy = cameraY + cameraHeight/2;

        int xTL=-1, yTL=-1, xBR=-1, yBR=-1;

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
                xTL = (int) ((camTLx - terrain.topLeftCorner.x)*unitSizePixels);
            }

            if (camTLy < terrain.topLeftCorner.y - heightRange/2) {
                yTL = 0;
            } else {
                // Calculate y top left
                yTL = (int) ((camTLy - (terrain.topLeftCorner.y - heightRange/2))*unitSizePixels);
            }

            if (camBRx > terrain.bottomRightCorner.x) {
                xBR = terrainImage.getWidth();
            } else {
                // Calculate x bottom right
                xBR = (int) ((camBRx - terrain.topLeftCorner.x)*unitSizePixels);
            }

            if (camBRy > terrain.bottomRightCorner.y+heightRange/2) {
                yBR = terrainImage.getHeight();
            } else {
                // Calculate y bottom right
                yBR = (int) ((camBRy - (terrain.topLeftCorner.y - heightRange/2))*unitSizePixels);
            }
        }

        if (xTL != -1) {
            int xDraw = (int) ((terrain.topLeftCorner.x - camTLx)*unitSizePixels);
            int yDraw = (int) ((terrain.topLeftCorner.y-heightRange/2 - camTLy)*unitSizePixels);
            if (xDraw < 0) {
                xDraw = 0;
            }
            if (yDraw < 0) {
                yDraw = 0;
            }
            BufferedImage subImage = terrainImage.getSubimage(xTL, yTL, xBR - xTL, yBR - yTL);
            imageG2.drawImage(subImage, null, 0, 0);
            subImage.flush();
        }
        // Render the flag and ball, order depending on position
        if (terrain.target.position.y > gameState.getBall().state.position.y) {
            renderBall(imageG2, cameraX, cameraY, cameraWidth, cameraHeight);
            renderFlag(imageG2, cameraX, cameraY, cameraWidth, cameraHeight);
        } else {
            renderFlag(imageG2, cameraX, cameraY, cameraWidth, cameraHeight);
            renderBall(imageG2, cameraX, cameraY, cameraWidth, cameraHeight);
        }
        // Draw ball position
        imageG2.setFont(new Font("TimesRoman", Font.BOLD, unitSizePixels/2));
        imageG2.setColor(Color.WHITE);
        BigDecimal xx = new BigDecimal(gameState.getBall().state.position.x);
        xx = xx.setScale(5, RoundingMode.HALF_UP);
        BigDecimal yy = new BigDecimal(gameState.getBall().state.position.y);
        yy = yy.setScale(5, RoundingMode.HALF_UP);
        imageG2.drawString("x = "+xx, unitSizePixels, unitSizePixels);
        imageG2.drawString("y = "+yy, unitSizePixels, 2*unitSizePixels);
        // Dispose of the graphics
        imageG2.dispose();

        return image;
    }

    public void render(Graphics2D g2, double cameraX, double cameraY, double cameraWidth, double cameraHeight, int pixelX, int pixelY) {
        BufferedImage image = getSubimage(cameraX, cameraY, cameraWidth, cameraHeight);
        // Draw the image
        g2.drawImage(image, null, pixelX, pixelY);
        image.flush();
    }

    private void renderBall(Graphics2D g2, double cameraX, double cameraY, double cameraWidth, double cameraHeight) {
        Ball ball = gameState.getBall();
        double z = ball.getZCoordinate(gameState.getTerrain());
        int x = (int) ((ball.state.position.x - cameraX + cameraWidth/2 - ball.radius)*unitSizePixels);
        int y = (int) ((ball.state.position.y - z - cameraY + cameraHeight/2 - ball.radius)*unitSizePixels);
        g2.setColor(Color.WHITE);
        g2.fillArc(x, y, (int) (ball.radius*unitSizePixels*2), (int) (ball.radius*unitSizePixels*2), 0, 360);
    }

    private void renderFlag(Graphics2D g2, double cameraX, double cameraY, double cameraWidth, double cameraHeight) {
        Target target = gameState.getTerrain().target;
        double z = gameState.getTerrain().terrainFunction.valueAt(target.position.x, target.position.y);
        int x = (int) ((target.position.x - cameraX + cameraWidth/2)*unitSizePixels);
        int y = (int) ((target.position.y - z - cameraY + cameraHeight/2 - 2)*unitSizePixels);
        g2.setColor(Color.WHITE);
        g2.drawLine(x, y, x, y+2*unitSizePixels);
        g2.setColor(Color.RED);
        g2.fillPolygon(new int[] {x, x+unitSizePixels/2, x}, new int[] {y, y+unitSizePixels/4, y+unitSizePixels/2}, 3);
    }

    private void createTerrainImage() {
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;

        int width = (int) ((terrain.bottomRightCorner.x-terrain.topLeftCorner.x)*unitSizePixels);
        int height = (int) ((terrain.bottomRightCorner.y-terrain.topLeftCorner.y)*unitSizePixels + heightRange*unitSizePixels);
        terrainImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) terrainImage.getGraphics();
        // Number of units in colored space
        double sizeColored = 4;
        // Clear screen
        g2.setColor(new Color(75, 47, 26));
        g2.fillRect(0, 0, terrainImage.getWidth(), terrainImage.getHeight());
        // Render the terrain
        int numVertices = (int) Math.sqrt(terrain.heightmap.length);
        double xStep = (terrain.bottomRightCorner.x - terrain.topLeftCorner.x) / numVertices;
        double yStep = (terrain.bottomRightCorner.y - terrain.topLeftCorner.y) / numVertices;
        // Find the max and min height in the terrain
        double totalMaxHeight = -10;
        double totalMinHeight = 10;
        for (int yy=0; yy<numVertices; yy++) {
            for (int xx=0; xx<numVertices; xx++) {
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
        // Make sure that the deepest part isn't pitch black
        totalMinHeight -= 1.5;
        // Find the rendering coordinates
        for (int yy=0; yy<numVertices-1; yy++) {
            for (int xx=0; xx<numVertices-1; xx++) {
                // First point
                double x1 = terrain.topLeftCorner.x + xx * xStep;
                double y1 = terrain.topLeftCorner.y + yy * yStep;
                double h1 = terrain.terrainFunction.valueAt(x1, y1);//terrain.heightmap[i1] * heightRange - heightRange/2;
                if (h1 > 10) {
                    h1 = 10;
                }
                if (h1 < -10) {
                    h1 = -10;
                }
                // Second point
                double x2 = terrain.topLeftCorner.x + (xx+1) * xStep;
                double y2 = terrain.topLeftCorner.y + yy * yStep;
                double h2 = terrain.terrainFunction.valueAt(x2, y2);//terrain.heightmap[i2] * heightRange - heightRange/2;
                if (h2 > 10) {
                    h2 = 10;
                }
                if (h2 < -10) {
                    h2 = -10;
                }
                // Third point
                double x3 = terrain.topLeftCorner.x + xx * xStep;
                double y3 = terrain.topLeftCorner.y + (yy+1) * yStep;
                double h3 = terrain.terrainFunction.valueAt(x3, y3);//terrain.heightmap[i3] * heightRange - heightRange/2;
                if (h3 > 10) {
                    h3 = 10;
                }
                if (h3 < -10) {
                    h3 = -10;
                }
                // Fourth point
                double x4 = terrain.topLeftCorner.x + (xx+1) * xStep;
                double y4 = terrain.topLeftCorner.y + (yy+1) * yStep;
                double h4 = terrain.terrainFunction.valueAt(x4, y4);//terrain.heightmap[i4] * heightRange - heightRange/2;
                if (h4 > 10) {
                    h4 = 10;
                }
                if (h4 < -10) {
                    h4 = -10;
                }

                double maxHeight = Math.max(h1, h2);
                maxHeight = Math.max(maxHeight, h3);
                maxHeight = Math.max(maxHeight, h4);

                int renderPixelX1 = (int) ((x1 - terrain.topLeftCorner.x) * unitSizePixels);
                int renderPixelY1 = (int) ((y1 - h1 - terrain.topLeftCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX2 = (int) ((x2 - terrain.topLeftCorner.x) * unitSizePixels);
                int renderPixelY2 = (int) ((y2 - h2 - terrain.topLeftCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX3 = (int) ((x3 - terrain.topLeftCorner.x) * unitSizePixels);
                int renderPixelY3 = (int) ((y3 - h3 - terrain.topLeftCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX4 = (int) ((x4 - terrain.topLeftCorner.x) * unitSizePixels);
                int renderPixelY4 = (int) ((y4 - h4 - terrain.topLeftCorner.y + heightRange/2) * unitSizePixels);

                double lighting = (maxHeight-totalMinHeight)/(totalMaxHeight-totalMinHeight);
                int numBlockX = (int) ((x1-terrain.topLeftCorner.x)/sizeColored);
                int numBlockY = (int) ((y1-terrain.topLeftCorner.y)/sizeColored);

                // Land
                if (maxHeight >= 0) {
                    // Sand
                    if (terrain.isPointInZone(x1, y1)) {
                        if ((numBlockX + numBlockY) % 2 == 0) {
                            g2.setColor(new Color((int) (150 * lighting), (int) (150 * lighting), 0));
                        } else {
                            g2.setColor(new Color((int) (100 * lighting), (int) (100 * lighting), 0));
                        }
                        // Grass
                    } else {
                        if ((numBlockX + numBlockY) % 2 == 0) {
                            g2.setColor(new Color(0, (int) (200*lighting), 0));
                        } else {
                            g2.setColor(new Color(0, (int) (180*lighting), 0));
                        }
                    }
                } else {
                    // Water
                    g2.setColor(new Color((int) (34*lighting), (int) (124*lighting), (int) (176*lighting)));
                }

                g2.fillPolygon(
                        new int[]{renderPixelX1, renderPixelX2, renderPixelX4, renderPixelX3},
                        new int[]{renderPixelY1, renderPixelY2, renderPixelY4, renderPixelY3},
                        4
                );
            }
        }
        // Render target
        double targetHeight = terrain.terrainFunction.valueAt(terrain.target.position.x, terrain.target.position.y);
        int targetRenderX = (int) ((terrain.target.position.x - terrain.topLeftCorner.x)*unitSizePixels);
        int targetRenderY = (int) ((terrain.target.position.y - targetHeight - terrain.topLeftCorner.y + heightRange/2)*unitSizePixels);
        double radius = terrain.target.radius;
        drawCircle(g2, terrain.target.position.x, terrain.target.position.y, radius, Color.BLACK, false);

        // Draw obstacles
        for (IObstacle o : terrain.obstacles) {
            // Trees
            if (o instanceof ObstacleTree) {
                ObstacleTree t = (ObstacleTree) o;
                drawCircle(g2, t.originPosition.x, t.originPosition.y, t.radius, new Color(96, 69, 38), true);
            } else if (o instanceof ObstacleBox) {
                ObstacleBox b = (ObstacleBox) o;
                drawRectangle(g2, b.bottomLeftCorner, b.topRightCorner, new Color(96, 69, 38), true);
            }
        }
    }

    /**
     * Draws a circle onto the function terrain
     * @param g2 The Graphics2D object that the function is being drawn to
     * @param x The x coordinate of the center of the circle
     * @param y The y coordinate of the center of the circle
     * @param radius The radius of the circle
     * @param color The color of the circle
     * @param filled Whether it's a filled circle or just an outline
     */
    private void drawCircle(Graphics2D g2, double x, double y, double radius, Color color, boolean filled) {
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;
        int[] xPoints = new int[361];
        int[] yPoints = new int[361];
        g2.setColor(color);
        int firstPointX = -1, firstPointY=-1;
        // Loop through 360 degrees and add the points
        for (int deg=0; deg<=360; deg++) {
            double xx = x+radius*Math.cos(deg/(2*Math.PI));
            double yy = y+radius*Math.sin(deg/(2*Math.PI));
            double h = terrain.terrainFunction.valueAt(xx, yy);
            if (h > 10) {
                h = 10;
            }
            if (h < -10) {
                h = -10;
            }
            int renderX = (int) ((xx - terrain.topLeftCorner.x)*unitSizePixels);
            int renderY = (int) ((yy - h - terrain.topLeftCorner.y + heightRange/2)*unitSizePixels);
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

    private void drawRectangle(Graphics2D g2, Vector2 bottomLeftCorner, Vector2 topRightCorner, Color color, boolean filled) {
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

    private void fillListsWithRectangleCorners(int[] xPoints, int[] yPoints, Vector2 bottomLeftCorner, Vector2 topRightCorner){
        Vector2 bottomRightCornerTranslated = getPointValue(new Vector2(topRightCorner.x, bottomLeftCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 0, bottomRightCornerTranslated);

        Vector2 bottomLeftCornerTranslated = getPointValue(new Vector2(bottomLeftCorner.x, bottomLeftCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 1, bottomLeftCornerTranslated);

        Vector2 topLeftCornerTranslated = getPointValue(new Vector2(bottomLeftCorner.x, topRightCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 2, topLeftCornerTranslated);

        Vector2 topRightCornerTranslated = getPointValue(new Vector2(topRightCorner.x, topRightCorner.y));
        assignTranslatedPoint(xPoints, yPoints, 3, topRightCornerTranslated);
    }

    private void assignTranslatedPoint(int[] xPoints, int[] yPoints, int index, Vector2 point){
        xPoints[index] = (int) point.x;
        yPoints[index] = (int) point.y;
    }

    private Vector2 getPointValue(Vector2 point){
        Terrain terrain = gameState.getTerrain();
        double heightRange = terrain.maxVal - terrain.minVal;
        double xx = point.x;
        double yy = point.y;
        double h = terrain.terrainFunction.valueAt(xx, yy);
        int renderX = (int) ((xx - terrain.topLeftCorner.x) * unitSizePixels);
        int renderY = (int) ((yy - h - terrain.topLeftCorner.y + heightRange / 2) * unitSizePixels);
        return new Vector2(renderX, renderY);
    }

}
