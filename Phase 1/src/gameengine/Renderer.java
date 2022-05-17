package gameengine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.imageio.ImageIO;

import datastorage.*;
import datastorage.obstacles.IObstacle;
import datastorage.obstacles.ObstacleBox;
import datastorage.obstacles.ObstacleTree;
import utility.math.Vector2;
import utility.math.Vector3;

import java.io.*;

public class Renderer {
    public Camera cam;
    public Terrain terrain;
    public int unitSizePixels;
    public Ball ball;
    public BufferedImage terrainImage;
    public double heightRange;
    public GameState gameState;
    //public Game game;

    /**
     * Renders the entire game
     * @param g2 The Graphics2D object to render to
     */
    public void render(Graphics2D g2) {
        g2.setColor(new Color(75, 47, 26));
        g2.fillRect(0, 0, (int) (cam.width*unitSizePixels), (int) (cam.height*unitSizePixels));
        // Render the terrain
        double camTLx = cam.x - cam.width/2;
        double camTLy = cam.y - cam.height/2;
        double camBRx = cam.x + cam.width/2;
        double camBRy = cam.y + cam.height/2;

        int xTL=-1, yTL=-1, xBR=-1, yBR=-1;

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
            BufferedImage bufferedImage = terrainImage.getSubimage(xTL, yTL, xBR - xTL, yBR - yTL);
            g2.drawImage(bufferedImage, null, xDraw, yDraw);
            bufferedImage.flush();
        }

        // Render the ball
        double ballZ = ball.getZCoordinate(terrain);
        if (ballZ > 10) {
            ballZ = 10;
        }
        if (ballZ < -10) {
            ballZ = -10;
        }

        // Calculate ball render position
        int ballRenderX = (int) ((ball.state.position.x - camTLx) * unitSizePixels);
        int ballRenderY = (int) ((ball.state.position.y - ballZ - camTLy) * unitSizePixels);
        int ballWidth = unitSizePixels/2;
        int ballHeight = unitSizePixels/2;
        // Ball shadow
        g2.setColor(new Color(50, 50, 50, 50));
        g2.fillArc(ballRenderX-3*ballWidth/4, ballRenderY-3*ballHeight/4, 3*ballWidth/2, 3*ballHeight/2, 0, 360);
        g2.setColor(Color.WHITE);
        g2.fillArc(ballRenderX - ballWidth / 2, ballRenderY - 4*ballHeight/5, ballWidth, ballHeight, 0, 360);

        // Draw ball position
        g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
        g2.setColor(Color.WHITE);
        BigDecimal xx = new BigDecimal(ball.state.position.x);
        xx = xx.setScale(5, RoundingMode.HALF_UP);
        BigDecimal yy = new BigDecimal(ball.state.position.y);
        yy = yy.setScale(5, RoundingMode.HALF_UP);
        g2.drawString("x = "+xx+" y = "+yy, unitSizePixels, 2*unitSizePixels);

        // Draw number of shots
        //g2.drawString("Shots taken: "+game.numShots, unitSizePixels, 4*unitSizePixels);

    }

    /**
     * Creates a BufferedImage of the terrain
     */
    public void createTerrainImage() {
        terrainImage = new BufferedImage(
                (int) ((terrain.bottomRightCorner.x-terrain.topLeftCorner.x)*unitSizePixels),
                (int) ((terrain.bottomRightCorner.y-terrain.topLeftCorner.y)*unitSizePixels + heightRange*unitSizePixels),
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
            } else if (o instanceof ObstacleBox) {
                ObstacleBox b = (ObstacleBox) o;
                drawRectangle(g2, b.bottomLeftCorner, b.topRightCorner, new Color(96, 69, 38), true);
            }
        }

        try {
            ImageIO.write(terrainImage, "png", new File("terrain.png"));
        } catch (Exception e) {
            e.printStackTrace();
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

    public BufferedImage generateNormalMap(Terrain terrain, int width, int height) {
        try {
            File f = new File("terrain_normal.png");
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int pixelX = 0; pixelX < width; pixelX++) {
                for (int pixelY = 0; pixelY < height; pixelY++) {
                    double x = terrain.topLeftCorner.x + (double) pixelX/width * (terrain.bottomRightCorner.x-terrain.topLeftCorner.x);
                    double y = terrain.topLeftCorner.y + (double) pixelY/height * (terrain.bottomRightCorner.y-terrain.topLeftCorner.y);

                    double z = terrain.terrainFunction.valueAt(x, y);

                    double dx = 0, dy = 0;
                    if (Math.abs(z) < 10) {
                        dx = terrain.terrainFunction.xDerivativeAt(x, y);
                        dy = terrain.terrainFunction.yDerivativeAt(x, y);
                    }

                    Vector3 color = new Vector3(-dx, -dy, 1);
                    color = color.normalized();

                    int alpha = 255 << 24;
                    int red = (int) ((color.x+1)/2*255) << 16; // -1 to 1 -> 0 to 255
                    int green = (int) ((color.y+1)/2*255) << 8; // -1 to 1 -> 0 to 255
                    int blue = (int) ((color.z+1)/2*255); // 0 to 1 -> 128 to 255

                    int rgb = 0;

                    rgb = rgb | alpha;
                    rgb = rgb | red;
                    rgb = rgb | green;
                    rgb = rgb | blue;

                    image.setRGB(pixelX, pixelY, rgb);
                }
            }
            ImageIO.write(image, "png", f);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage generateHeightMap(Terrain terrain, int width, int height) {
        try {
            File f = new File("terrain_height.png");
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int pixelX = 0; pixelX < width; pixelX++) {
                for (int pixelY = 0; pixelY < height; pixelY++) {
                    double x = terrain.topLeftCorner.x + (double) pixelX/width * (terrain.bottomRightCorner.x-terrain.topLeftCorner.x);
                    double y = terrain.topLeftCorner.y + (double) pixelY/height * (terrain.bottomRightCorner.y-terrain.topLeftCorner.y);

                    double z = terrain.terrainFunction.valueAt(x, y);
                    if (z > 10) {
                        z = 10;
                    }
                    if (z < -10) {
                        z = -10;
                    }

                    double brightness = (z+10)/20;

                    int alpha = 255 << (3*8);
                    int red = (int) (brightness*255) << (2*8);
                    int green = (int) (brightness*255) << 8;
                    int blue = (int) (brightness*255);

                    int rgb = 0;

                    rgb = rgb | alpha;
                    rgb = rgb | red;
                    rgb = rgb | green;
                    rgb = rgb | blue;

                    image.setRGB(pixelX, pixelY, rgb);
                }
            }
            ImageIO.write(image, "png", f);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateTerrainImage(Terrain terrain, int width, int height) {
        int unitSizePixelsY = (int) (height / (terrain.bottomRightCorner.y - terrain.topLeftCorner.y));

        int minZ = -10;
        int maxZ = 10;

        BufferedImage image = new BufferedImage(width, height + (maxZ-minZ)*unitSizePixelsY, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = (Graphics2D) image.getGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());

        BufferedImage heightMap = generateHeightMap(terrain, width, height);
        BufferedImage normalMap = generateNormalMap(terrain, width, height);

        Vector3 lightPosition = new Vector3(0, 40, 10);

        long start = System.nanoTime();

        for (int px=0; px<width; px++) {
            for (int py=0; py<height; py++) {
                int startZ = (maxZ-minZ)*unitSizePixelsY;

                double x = terrain.topLeftCorner.x + px/width * (terrain.bottomRightCorner.x - terrain.topLeftCorner.x); // X in-game coordinate
                double y = terrain.topLeftCorner.y + py/height * (terrain.bottomRightCorner.y - terrain.topLeftCorner.y); // Y in-game coordinate
                double z = (heightMap.getRGB(px, py) & 0x0000ff)/255.0 * 2 - 1; // z in [0, 1]
                z = z*(maxZ - minZ) + minZ; // get h in [minZ, maxZ] Z in-game coordinate

                if (z > maxZ) {
                    z = maxZ;
                } else if (z < minZ) {
                    z = minZ;
                }

                int pz = (int) (z*unitSizePixelsY); // Get the "z" value of the pixel

                int normal = normalMap.getRGB(px, py);
                double nx = ((normal & 0xff0000) >> 16)/255.0 * 2 - 1;
                double ny = ((normal & 0x00ff00) >> 8)/255.0 * 2 - 1;
                double nz = (normal & 0x0000ff)/255.0 * 2 - 1;

                Vector3 normalVector = new Vector3(nx, ny, nz);
                normalVector = normalVector.normalized();
                Vector3 positionVector = new Vector3(x, y, z);

                /*if (normalVector.length() > 1) {
                    System.out.println(normalVector);
                }*/

                double lighting = /*normalVector.normalized().dot(new Vector3(0, 0, 1));*/Vector3.dotProduct(lightPosition.copy().translate(positionVector.copy().scale(-1)).normalized(), normalVector);

                if (lighting > 1) {
                    lighting = 1;
                } else if (lighting < 0) {
                    lighting = 0;
                }

                // Modify the lighting based on height
                lighting *= (z-minZ)/(maxZ-minZ);

                // Set the lightinh between 0.1 and 1
                lighting = lighting*0.9 + 0.1;

                /*int red = 0;//(normal & 0xff0000) >> 16;//0
                int green = (int) (200*lighting);//(normal & 0x00ff00) >> 8;//(int) (200*lighting) << 8;
                int blue = 0;//normal & 0x0000ff;//0;*/

                int red = (normal & 0xff0000) >> 16;
                int green = (normal & 0x00ff00) >> 8;
                int blue = normal & 0x0000ff;//0;


                //System.out.println(green);

                //rgb = normal;

                g2.setColor(new Color(red, green, blue));
                g2.drawLine(px, py-pz, px, image.getHeight());

                //image.setRGB(px, py-pz, rgb);
            }
        }

        System.out.println("Time: "+(System.nanoTime()-start)/1000000.0+" ms");

        g2.dispose();

        try {
            ImageIO.write(image, "png", new File("terrain.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage generateWaterfallPlot(Terrain terrain, int width, int height) {
        int unitSizePixelsY = (int) (height / (terrain.bottomRightCorner.y - terrain.topLeftCorner.y));

        BufferedImage heightMap = generateHeightMap(terrain, width, height);

        double maxZ = 10;
        double minZ = -10;

        BufferedImage image = new BufferedImage(
                width,
                height + (int) (unitSizePixelsY*(maxZ - minZ)),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = (Graphics2D) image.getGraphics();

        for (int py=0; py<height; py+=5) {
            int[] xPoints = new int[width+2];
            int[] yPoints = new int[width+2];
            for (int px=0; px<width; px++) {
                int pz = (int) ((((heightMap.getRGB(px, py) & 0xff0000) >> 16)/255.0 * (maxZ - minZ))*unitSizePixelsY);

                //System.out.println((heightMap.getRGB(px, py) << 16));

                xPoints[px] = px;
                yPoints[px] = image.getHeight() - height + py - pz;
            }
            xPoints[xPoints.length-2] = width;
            yPoints[yPoints.length-2] = image.getHeight() - height + py;

            xPoints[xPoints.length-1] = 0;
            yPoints[yPoints.length-1] = image.getHeight() - height + py;

            Polygon p = new Polygon(xPoints, yPoints, width+2);

            g2.setColor(new Color(0, 200, 0));
            g2.fillPolygon(p);

            g2.setColor(new Color(0, 100, 0));
            g2.drawPolygon(p);
        }

        g2.dispose();

        try {
            ImageIO.write(image, "png", new File("terrain.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;

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
        double xx = point.x;
        double yy = point.y;
        double h = terrain.terrainFunction.valueAt(xx, yy);
        int renderX = (int) ((xx - terrain.topLeftCorner.x) * unitSizePixels);
        int renderY = (int) ((yy - h - terrain.topLeftCorner.y + heightRange / 2) * unitSizePixels);
        return new Vector2(renderX, renderY);
    }

    /*public static void main(String[] args) {
        Renderer r = new Renderer();
        //r.generateNormalMap(new Terrain("sin(x*y)", 0.1, 0.2, new Vector2(-50, -50), new Vector2(50, 50)), 1024, 1024);
        //r.generateHeightMap(new Terrain("sin(x*y)", 0.1, 0.2, new Vector2(-50, -50), new Vector2(50, 50)), 1024, 1024);
        //r.generateTerrainImage(new Terrain("e**(-(x**2 + y**2)/40)", 0.1, 0.2, new Vector2(-30, -30), new Vector2(30, 30)), 1024, 1024);
        r.generateWaterfallPlot(new Terrain("sin((x+y)/7)", 0.1, 0.2, new Vector2(-30, -30), new Vector2(30, 30)), 1024, 1024);
    }*/
}
