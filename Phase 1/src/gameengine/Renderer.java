package gameengine;

import Data_storage.Ball;
import Data_storage.Terrain;
import Data_storage.Vector2;
import Data_storage.Vector3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Renderer {
    public Camera cam;
    public Terrain terrain;
    public int unitSizePixels;
    public Ball ball;
    public BufferedImage terrainImage;

    public void render(Graphics2D g2) {
        g2.setColor(Color.WHITE);
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

            if (camTLy < terrain.startingCorner.y) {
                yTL = 0;
            } else {
                // Calculate y top left
                yTL = (int) ((camTLy - terrain.startingCorner.y)*unitSizePixels);
            }

            if (camBRx > terrain.limitingCorner.x) {
                xBR = (int) terrainImage.getWidth();
            } else {
                // Calculate x bottom right
                xBR = (int) ((camBRx - terrain.startingCorner.x)*unitSizePixels);
            }

            if (camBRy > terrain.limitingCorner.y) {
                yBR = (int) terrainImage.getHeight();
            } else {
                // Calculate y bottom right
                yBR = (int) ((camBRy - terrain.startingCorner.y)*unitSizePixels);
            }
        }

        if (xTL != -1) {
            int xDraw = (int) ((terrain.startingCorner.x - camTLx)*unitSizePixels);
            int yDraw = (int) ((terrain.startingCorner.y - camTLy)*unitSizePixels);
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
        g2.setColor(Color.WHITE);
        g2.fillArc(ballRenderX - ballWidth / 2, ballRenderY - ballHeight / 2, ballWidth, ballHeight, 0, 360);

    }

    public void createTerrainImage() {
        terrainImage = new BufferedImage(
                (int) ((terrain.limitingCorner.x-terrain.startingCorner.x)*unitSizePixels),
                (int) ((terrain.limitingCorner.y-terrain.startingCorner.y)*unitSizePixels),
                BufferedImage.TYPE_4BYTE_ABGR
        );
        Graphics2D g2 = (Graphics2D) terrainImage.getGraphics();
        // Number of units in colored space
        double sizeColored = 4;
        // Clear screen
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, (int) (cam.width*unitSizePixels), (int) (cam.height*unitSizePixels));
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
                double h1 = terrain.heightmap[i1] * 20 - 10;
                // Second point
                double x2 = terrain.startingCorner.x + (xx+1) * xStep;
                double y2 = terrain.startingCorner.y + yy * yStep;
                double h2 = terrain.heightmap[i2] * 20 - 10;
                // Third point
                double x3 = terrain.startingCorner.x + xx * xStep;
                double y3 = terrain.startingCorner.y + (yy+1) * yStep;
                double h3 = terrain.heightmap[i3] * 20 - 10;
                // Fourth point
                double x4 = terrain.startingCorner.x + (xx+1) * xStep;
                double y4 = terrain.startingCorner.y + (yy+1) * yStep;
                double h4 = terrain.heightmap[i4] * 20 - 10;

                double maxHeight = Math.max(h1, h2);
                maxHeight = Math.max(maxHeight, h3);
                maxHeight = Math.max(maxHeight, h4);

                int renderPixelX1 = (int) ((x1 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY1 = (int) ((y1 - h1 - terrain.startingCorner.y) * unitSizePixels);

                int renderPixelX2 = (int) ((x2 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY2 = (int) ((y2 - h2 - terrain.startingCorner.y) * unitSizePixels);

                int renderPixelX3 = (int) ((x3 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY3 = (int) ((y3 - h3 - terrain.startingCorner.y) * unitSizePixels);

                int renderPixelX4 = (int) ((x4 - terrain.startingCorner.x) * unitSizePixels);
                int renderPixelY4 = (int) ((y4 - h4 - terrain.startingCorner.y) * unitSizePixels);

                float lighting = (float)((maxHeight+10)/20);
                int numBlockX = (int) ((x1-terrain.startingCorner.x)/sizeColored);
                int numBlockY = (int) ((y1-terrain.startingCorner.y)/sizeColored);
                if (maxHeight > 0) {
                    if ((numBlockX+numBlockY)%2 == 0) {
                        g2.setColor(new Color(0, 0.7f*lighting, 0));
                    } else {
                        g2.setColor(new Color(0, 0.5f*lighting, 0));
                    }
                } else {
                    //if ((numBlockX+numBlockY)%2 == 0) {
                        g2.setColor(new Color(0, 0, 0.7f*lighting));
                    //} else {
                        //g2.setColor(new Color(0, 0, 0.5f*lighting));
                    //}
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
        int targetRenderY = (int) ((terrain.target.position.y - targetHeight - terrain.startingCorner.y)*unitSizePixels);
        double radius = terrain.target.radius;
        g2.setColor(Color.BLACK);
        int prevPointX = -1, prevPointY=-1;
        for (double deg=0; deg<=360; deg+=1) {
            double xx = terrain.target.position.x+radius*Math.cos(deg/(2*Math.PI));
            double yy = terrain.target.position.y+radius*Math.sin(deg/(2*Math.PI));
            double h = terrain.terrainFunction.valueAt(xx, yy);
            int renderX = (int) ((xx - terrain.startingCorner.x)*unitSizePixels);
            int renderY = (int) ((yy - h - terrain.startingCorner.y)*unitSizePixels);
            if (prevPointX != -1) {
                g2.drawLine(prevPointX, prevPointY, renderX, renderY);
                prevPointX = renderX;
                prevPointY = renderY;
            } else {
                prevPointX = renderX;
                prevPointY = renderY;
            }
        }
        // Draw flag
        g2.setColor(Color.WHITE);
        g2.drawLine(targetRenderX, targetRenderY, targetRenderX, targetRenderY-2*unitSizePixels);
        g2.setColor(Color.RED);
        g2.fillPolygon(
                new int[] {targetRenderX+1, targetRenderX+unitSizePixels+1, targetRenderX+1},
                new int[] {targetRenderY-2*unitSizePixels, targetRenderY-3*unitSizePixels/2, targetRenderY-unitSizePixels},
                3
        );
    }
}
