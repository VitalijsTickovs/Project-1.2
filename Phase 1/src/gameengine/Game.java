package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

import Data_storage.*;
import Data_storage.Rectangle;
import Physics.PhysicsEngine;
import Reader.*;

public class Game extends Canvas implements Runnable, GameObject {
    private final int FPS;
    private boolean running;
    private Thread thread;
    private BufferedImage terrainImage;
    private JFrame frame;
    private Terrain terrain;
    private Ball ball;
    private PhysicsEngine engine;
    private int numVerteces;
    private int unitSizePixels;

    private int cameraWidth, cameraHeight;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        // Your path to GitHub here
        String csvFile = "C:/Users/staso/Documents/GitHub/Project-1.2/Phase 1/src/Reader/UserInput.csv";
        Terrain terrainT = Reader.readFile(csvFile);
        this.FPS = fps;
        this.running = false;

        cameraWidth = 10;
        cameraHeight = 10;

        numVerteces = 512;

        unitSizePixels = 40;

        terrain = new Terrain("0.1*sin(e**(-(x**2+y**2)/40)*x*y)", 0.2, 0.1, new Vector2(-20, -20), new Vector2(20, 20));
        terrain.calculateHeightMap(numVerteces, 1.0);

        int width = cameraWidth*unitSizePixels;//(int) (terrain.limitingCorner.x - terrain.startingCorner.x)*unitSizePixels;//
        int height = cameraHeight*unitSizePixels;//;//(int) (terrain.limitingCorner.y - terrain.startingCorner.y)*unitSizePixels;//

        terrainImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        //System.out.println(terrain.terrainFunction);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);
        ball = new Ball(new Vector2(0, 0), new Vector2(3, -5));
        engine = new PhysicsEngine();
        engine.terrain = terrain;
        engine.addBall(ball);
    }

    /**
     * Main method to run the game loop
     */
    public void run() {
        long last = System.nanoTime();
        final double ns = 1000000000.0 / FPS; // How many nanoseconds should pass between frames
        long now = System.nanoTime();

        int fps = 0;

        double numUpdates = 0;

        double timer = 0;

        while (running) {
            now = System.nanoTime();

            numUpdates += (now - last) / ns;

            timer += now - last;

            if (numUpdates >= 1) {
                update();
                render();
                //gui.renderBall();
                fps++;
                numUpdates--;
            }

            last = now;

            if (timer > 1000000000.0) {
                System.out.println("FPS: "+fps);
                timer = 0;
                fps = 0;
            }
        }
    }

    public void fillTerrain(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, terrainImage.getWidth(), terrainImage.getHeight());

        double heightStep = 0.1;
        double heightStepOffset = 0.001;

        double xOff = (terrain.limitingCorner.x-terrain.startingCorner.x)/numVerteces;
        double yOff = (terrain.limitingCorner.y-terrain.startingCorner.y)/numVerteces;

        Rectangle cameraBox = new Rectangle();
        cameraBox.downLeftCorner = new Vector2(ball.state.position.x-cameraWidth/2.0, ball.state.position.y-cameraHeight/2.0);
        cameraBox.topRightCorner = new Vector2(ball.state.position.x+cameraWidth/2.0, ball.state.position.y+cameraHeight/2.0);

        // Find the min and max value
        double minHeight = terrain.heightmap[0];
        double maxHeight = terrain.heightmap[0];
        for (int i=0; i<terrain.heightmap.length; i++) {
            if (terrain.heightmap[i] < minHeight) {
                minHeight = terrain.heightmap[i];
            }
            if (terrain.heightmap[i] > maxHeight) {
                maxHeight = terrain.heightmap[i];
            }
        }
        minHeight = Math.max(0, minHeight-0.005);

        for (int i=0; i<terrain.heightmap.length-numVerteces; i++) {
            if ((i+1)%numVerteces == 0) {
                // Go next
            } else {
                int xx = i/numVerteces;
                int yy = i%numVerteces;

                // Get the actual function coordinates of the rectangle
                double x1 = terrain.startingCorner.x + xx*xOff;
                double y1 = terrain.startingCorner.y + yy*yOff;
                double x2 = x1+xOff;
                double y2 = y1;
                double x3 = x1;
                double y3 = y1+yOff;
                double x4 = x2;
                double y4 = y3;

                Vector2 point1 = new Vector2(x1, y1);
                Vector2 point2 = new Vector2(x2, y2);
                Vector2 point3 = new Vector2(x3, y3);
                Vector2 point4 = new Vector2(x4, y4);

                // Render if on screen
                if (cameraBox.isPositionInside(point1) || cameraBox.isPositionInside(point2) ||
                    cameraBox.isPositionInside(point3) || cameraBox.isPositionInside(point4)) {

                    float val1 = terrain.heightmap[i];
                    float val2 = terrain.heightmap[i + 1];
                    float val3 = terrain.heightmap[i + numVerteces];
                    float val4 = terrain.heightmap[i + 1 + numVerteces];

                    float val = Math.max(val1, val2);
                    val = Math.max(val, val3);
                    val = Math.max(val, val4);

                    float valMin = Math.min(val1, val2);
                    valMin = Math.min(valMin, val3);
                    valMin = Math.min(valMin, val4);

                    Rectangle renderRect = new Rectangle();
                    renderRect.downLeftCorner = new Vector2(x3, y3);
                    renderRect.topRightCorner = new Vector2(x2, y2);

                    float borderVal = (float) (Math.floor(valMin/(heightStep/20.0))*(heightStep/20.0)+(heightStep/20.0));
                    //System.out.println(borderVal);

                    /*if (borderVal > valMin && borderVal < val) {
                        g2.setColor(new Color(0, 0.9f*val, 0));
                    } else */
                    float col = (float) ((val-minHeight)/(maxHeight-minHeight));

                    if (val >= 0.5) {
                        g2.setColor(new Color(0, col, 0));
                    } else {
                        g2.setColor(new Color(0, col, col));
                    }

                    int xRender = (int) ((x1-(ball.state.position.x - cameraWidth/2.0))*unitSizePixels);
                    int yRender = (int) ((y1-(ball.state.position.y - cameraHeight/2.0))*unitSizePixels);

                    g2.fillRect(xRender, yRender, Math.max(unitSizePixels, (int) (xOff * unitSizePixels)), Math.max(unitSizePixels, (int) (yOff * unitSizePixels)));
                }
            }
        }
    }

    private ArrayList<Vector2> points = new ArrayList<Vector2>();

    /**
     * Updates the state of the game each step
     */
    public void update() {
        if (points.size() == 0) {
            points = engine.simulateShot(new Vector2(-5, 3), ball);
        } else {
            ball.state.position = points.get(0);
            points.remove(0);
        }
    }

    /**
     * Renders the game
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();
        g2.drawImage(terrainImage, 0, 0, terrainImage.getWidth(), terrainImage.getHeight(), null);

        fillTerrain(g2);

        // Render ball
        int ballWidth = unitSizePixels/4;
        int ballHeight = unitSizePixels/4;
        int xx = cameraWidth*unitSizePixels/2 - ballWidth/2;//(int) ((ball.state.position.x - terrain.startingCorner.x)*unitSizePixels);////
        int yy = cameraHeight*unitSizePixels/2 - ballHeight/2;//(int) ((ball.state.position.y - terrain.startingCorner.y)*unitSizePixels);////
        g2.setColor(Color.WHITE);
        g2.fillArc(xx, yy, ballWidth, ballHeight, 0, 360);

        g2.dispose();
        bs.show();
    }

    /**
     * Starts the game.
     * WARNING: Uses a sepparate thread
     */
    public void start() {
        running = true;
        thread = new Thread(this, "Game loop thread");
        thread.start();
    }

    public static void main(String[] args) {
        Game g = new Game(60);
        g.start();
    }
}