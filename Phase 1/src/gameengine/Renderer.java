package gameengine;

import Data_storage.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Renderer {
    public Camera cam;
    public Terrain terrain;
    public int unitSizePixels;
    public Ball ball;
    public BufferedImage terrainImage;
    public double heightRange;
    public Game game;

    /**
     * Renders the entire game
     * @param g2 The Graphics2D object to render to
     */
    public void render(Graphics2D g2) {
        g2.setColor(new Color(75, 47, 26));
        g2.fillRect(0, 0, (int) (cam.width*unitSizePixels), (int) (cam.width*unitSizePixels));
        // Render the terrain
        double camTLx = cam.x - cam.width/2;
        double camTLy = cam.y - cam.height/2;
        double camBRx = cam.x + cam.width/2;
        double camBRy = cam.y + cam.height/2;

        int xTL=-1, yTL=-1, xBR=-1, yBR=-1;

        // Check if the terrain is on-screen and find the range of the drawing part
        if (camTLx < terrain.limitingCorner.x && camTLy < terrain.limitingCorner.y
            &&
            camBRx > terrain.startingCorner.x && camBRy > terrain.startingCorner.y) {

            if (camTLx < terrain.startingCorner.x) {
                xTL = 0;
            } else {
                // Calculate x top left
                xTL = (int) ((camTLx - terrain.startingCorner.x)*unitSizePixels);
            }

            if (camTLy < terrain.startingCorner.y - heightRange/2) {
                yTL = 0;
            } else {
                // Calculate y top left
                yTL = (int) ((camTLy - (terrain.startingCorner.y - heightRange/2))*unitSizePixels);
            }

            if (camBRx > terrain.limitingCorner.x) {
                xBR = terrainImage.getWidth();
            } else {
                // Calculate x bottom right
                xBR = (int) ((camBRx - terrain.startingCorner.x)*unitSizePixels);
            }

            if (camBRy > terrain.limitingCorner.y+heightRange/2) {
                yBR = terrainImage.getHeight();
            } else {
                // Calculate y bottom right
                yBR = (int) ((camBRy - (terrain.startingCorner.y - heightRange/2))*unitSizePixels);
            }
        }

        if (xTL != -1) {
            int xDraw = (int) ((terrain.startingCorner.x - camTLx)*unitSizePixels);
            int yDraw = (int) ((terrain.startingCorner.y-heightRange/2 - camTLy)*unitSizePixels);
            if (xDraw < 0) {
                xDraw = 0;
            }
            if (yDraw < 0) {
                yDraw = 0;
            }
            g2.drawImage(terrainImage.getSubimage(xTL, yTL, xBR - xTL, yBR - yTL), null, xDraw, yDraw);
        }

        // Render the ball
        double ballZ = ball.getZCoordinate(terrain);

        // Calculate ball render position
        int ballRenderX = (int) ((ball.state.position.x - camTLx) * unitSizePixels);
        int ballRenderY = (int) ((ball.state.position.y - ballZ - camTLy) * unitSizePixels);
        int ballWidth = unitSizePixels;
        int ballHeight = unitSizePixels;
        // Ball shadow
        g2.setColor(new Color(50, 50, 50, 50));
        g2.fillArc(ballRenderX-3*ballWidth/4, ballRenderY-3*ballHeight/4, 3*ballWidth/2, 3*ballHeight/2, 0, 360);
        g2.setColor(Color.WHITE);
        g2.fillArc(ballRenderX - ballWidth / 2, ballRenderY - 4*ballHeight/5, ballWidth, ballHeight, 0, 360);

        // Draw ball position
        g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        BigDecimal xx = new BigDecimal(ball.state.position.x);
        xx = xx.setScale(2, RoundingMode.HALF_UP);
        BigDecimal yy = new BigDecimal(ball.state.position.y);
        yy = yy.setScale(2, RoundingMode.HALF_UP);
        g2.drawString("x = "+xx+" y = "+yy, unitSizePixels, 2*unitSizePixels);

        // Draw number of shots
        g2.drawString("Shots taken: "+game.numShots, unitSizePixels, 4*unitSizePixels);

    }

    /**
     * Creates a BufferedImage of the terrain
     */
    public void createTerrainImage() {
        terrainImage = new BufferedImage(
                (int) ((terrain.limitingCorner.x-terrain.startingCorner.x)*unitSizePixels),
                (int) ((terrain.limitingCorner.y-terrain.startingCorner.y)*unitSizePixels + heightRange*unitSizePixels),
                BufferedImage.TYPE_4BYTE_ABGR
        );
        Graphics2D g2 = (Graphics2D) terrainImage.getGraphics();
        // Number of units in colored space
        double sizeColored = 4;
        // Clear screen
        g2.setColor(new Color(75, 47, 26));
        g2.fillRect(0, 0, terrainImage.getWidth(), terrainImage.getHeight());
        // Render the terrain
        int numVertices = (int) Math.sqrt(terrain.heightmap.length);
        double xStep = (terrain.limitingCorner.x - terrain.startingCorner.x) / numVertices;
        double yStep = (terrain.limitingCorner.y - terrain.startingCorner.y) / numVertices;
        // Find the rendering coordinates
        for (int yy=0; yy<numVertices-1; yy++) {
            for (int xx=0; xx<numVertices-1; xx++) {
                int i1 = xx*numVertices + yy;
                int i2 = (xx+1)*numVertices + yy;
                int i3 = xx*numVertices + yy+1;
                int i4 = (xx+1)*numVertices + yy+1;
                // First point
                double x1 = terrain.startingCorner.x + xx * xStep;
                double y1 = terrain.startingCorner.y + yy * yStep;
                double h1 = terrain.heightmap[i1] * heightRange - heightRange/2;
                // Second point
                double x2 = terrain.startingCorner.x + (xx+1) * xStep;
                double y2 = terrain.startingCorner.y + yy * yStep;
                double h2 = terrain.heightmap[i2] * heightRange - heightRange/2;
                // Third point
                double x3 = terrain.startingCorner.x + xx * xStep;
                double y3 = terrain.startingCorner.y + (yy+1) * yStep;
                double h3 = terrain.heightmap[i3] * heightRange - heightRange/2;
                // Fourth point
                double x4 = terrain.startingCorner.x + (xx+1) * xStep;
                double y4 = terrain.startingCorner.y + (yy+1) * yStep;
                double h4 = terrain.heightmap[i4] * heightRange - heightRange/2;

                double maxHeight = Math.max(h1, h2);
                maxHeight = Math.max(maxHeight, h3);
                maxHeight = Math.max(maxHeight, h4);

                int renderPixelX1 = (int) ((x1 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY1 = (int) ((y1 - h1 - terrain.startingCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX2 = (int) ((x2 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY2 = (int) ((y2 - h2 - terrain.startingCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX3 = (int) ((x3 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY3 = (int) ((y3 - h3 - terrain.startingCorner.y + heightRange/2) * unitSizePixels);

                int renderPixelX4 = (int) ((x4 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY4 = (int) ((y4 - h4 - terrain.startingCorner.y + heightRange/2) * unitSizePixels);

                double lighting = (maxHeight+10)/20;
                int numBlockX = (int) ((x1-terrain.startingCorner.x)/sizeColored);
                int numBlockY = (int) ((y1-terrain.startingCorner.y)/sizeColored);
                // Land
                if (maxHeight >= 0) {
                    // Sand
                    if (terrain.isPointInZone(x1, y1)) {
                        if ((numBlockX + numBlockY) % 2 == 0) {
                            g2.setColor(new Color((int) (200 * lighting), (int) (200 * lighting), 0));
                        } else {
                            g2.setColor(new Color((int) (180 * lighting), (int) (150 * lighting), 0));
                        }
                        // Grass
                    } else {
                        if ((numBlockX + numBlockY) % 2 == 0) {
                            g2.setColor(new Color(0, (int) (200 * lighting), 0));
                        } else {
                            g2.setColor(new Color(0, (int) (150 * lighting), 0));
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
        int targetRenderX = (int) ((terrain.target.position.x - terrain.startingCorner.x)*unitSizePixels);
        int targetRenderY = (int) ((terrain.target.position.y - targetHeight - terrain.startingCorner.y + heightRange/2)*unitSizePixels);
        double radius = terrain.target.radius;
        drawCircle(g2, terrain.target.position.x, terrain.target.position.y, radius, Color.BLACK, false);
        // Draw flag
        g2.setColor(Color.WHITE);
        g2.drawLine(targetRenderX, targetRenderY, targetRenderX, targetRenderY-2*unitSizePixels);
        g2.setColor(Color.RED);
        g2.fillPolygon(
                new int[] {targetRenderX+1, targetRenderX+unitSizePixels+1, targetRenderX+1},
                new int[] {targetRenderY-2*unitSizePixels, targetRenderY-3*unitSizePixels/2, targetRenderY-unitSizePixels},
                3
        );

        // Draw obstacles
        for (IObstacle o : terrain.obstacles) {
            // Trees
            if (o instanceof ObstacleTree) {
                ObstacleTree t = (ObstacleTree) o;
                drawCircle(g2, t.originPosition.x, t.originPosition.y, t.radius, new Color(96, 69, 38), true);
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
        int[] xPoints = new int[361];
        int[] yPoints = new int[361];
        g2.setColor(color);
        int firstPointX = -1, firstPointY=-1;
        // Loop through 360 degrees and add the points
        for (int deg=0; deg<=360; deg++) {
            double xx = x+radius*Math.cos(deg/(2*Math.PI));
            double yy = y+radius*Math.sin(deg/(2*Math.PI));
            double h = terrain.terrainFunction.valueAt(xx, yy);
            int renderX = (int) ((xx - terrain.startingCorner.x)*unitSizePixels);
            int renderY = (int) ((yy - h - terrain.startingCorner.y + heightRange/2)*unitSizePixels);
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
}
