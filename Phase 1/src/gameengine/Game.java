package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import Data_storage.*;
import Physics.PhysicsEngine;

public class Game extends Canvas implements Runnable, GameObject {
    private final int FPS;
    private boolean running;
    private int xTop, yTop, xBottom, yBottom;
    private Thread thread;
    private BufferedImage terrainImage;
    private JFrame frame;
    private Terrain terrain;
    private int xDim, yDim;
    private Ball ball;
    private PhysicsEngine engine;
    private int numVerteces;
    private double mapSize;
    private int unitSizePixels;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        this.FPS = fps;
        this.running = false;

        numVerteces = 512;

        unitSizePixels = 10;

        terrain = new Terrain("sin((x+y)/7)", 0.15, 0.07, new Vector2(-30, -15), new Vector2(30, 15));
        terrain.calculateHeightMap(numVerteces, 1.0);

        int width=(int) (terrain.limitingCorner.x - terrain.startingCorner.x)*unitSizePixels;
        int height=(int) (terrain.limitingCorner.y - terrain.startingCorner.y)*unitSizePixels;

        terrainImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        //System.out.println(terrain.terrainFunction);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);

        ball = new Ball(new Vector2(-10, -10), new Vector2(0, 1));
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
        double xOff = (terrain.limitingCorner.x-terrain.startingCorner.x)/numVerteces*unitSizePixels;
        double yOff = (terrain.limitingCorner.y-terrain.startingCorner.y)/numVerteces*unitSizePixels;
        for (int i=0; i<terrain.heightmap.length-numVerteces; i++) {
            if ((i+1)%numVerteces == 0) {
                // Go next
            } else {
                int xx = i/numVerteces;
                int yy = i%numVerteces;

                int x1 = (int) (xx*xOff);
                int y1 = (int) (yy*yOff);

                float val1 = terrain.heightmap[i];
                float val2 = terrain.heightmap[i+1];
                float val3 = terrain.heightmap[i+numVerteces];
                float val4 = terrain.heightmap[i+1+numVerteces];

                float val = Math.max(val1, val2);
                val = Math.max(val, val3);
                val = Math.max(val, val4);

                if (val >= 0.5) {
                    g2.setColor(new Color(0, val, 0));
                } else {
                    g2.setColor(new Color(0, val, 0));
                }
                g2.fillRect(x1, y1, unitSizePixels, unitSizePixels);
            }
        }
    }

    /**
     * Updates the state of the game each step
     */
    public void update() {
        engine.fixedUpdate();
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
        int xx = (int) ((ball.state.position.x - terrain.startingCorner.x)*unitSizePixels);
        int yy = (int) ((ball.state.position.y - terrain.startingCorner.y)*unitSizePixels);
        g2.setColor(Color.WHITE);
        g2.fillArc(xx, yy, (int) unitSizePixels, (int) unitSizePixels, 0, 360);

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