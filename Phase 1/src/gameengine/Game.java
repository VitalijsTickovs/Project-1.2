package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import Data_storage.*;
import Physics.PhysicsEngine;

public class Game extends Canvas implements Runnable, GameObject {
    private final int FPS;
    private boolean running;
    private final int scale = 1;
    private int xTop, yTop, xBottom, yBottom;
    private Thread thread;
    private BufferedImage terrainImage;
    private JFrame frame;
    private Terrain terrain;
    private int xDim, yDim;
    private Ball ball;
    private PhysicsEngine engine;
    private double unitSizePixelsX, unitSizePixelsY;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        this.FPS = fps;
        this.running = false;
        xDim = 500;
        yDim = 500;
        xTop = -50;
        yTop = -50;
        xBottom = 50;
        yBottom = 50;
        unitSizePixelsX = (double)(xDim*scale)/(xBottom - xTop);
        unitSizePixelsY = (double)(yDim*scale)/(yBottom - yTop);
        terrainImage = new BufferedImage(xDim*scale, xDim*scale, BufferedImage.TYPE_4BYTE_ABGR);
        terrain = new Terrain("sin((x+y)/7)", new Vector2(xTop, yTop), new Vector2(xBottom, yBottom), 0.15, 0.07, xDim, yDim, 1);
        System.out.println(terrain.terrainFunction);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(xDim*scale, yDim*scale);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);

        ball = new Ball(new Vector2(0, 0), new Vector2(3, -10));
        engine = new PhysicsEngine();
        engine.terrain = terrain;
        engine.addBall(ball);
    }

    /**
     * Main method to run the game loop
     */
    public void run() {
        long last = System.nanoTime();
        final double nanosPerFrame = 1000000000.0 / FPS; // How many nanoseconds should pass between frames
        long now = System.nanoTime();

        double numUpdates = 0;

        while (running) {
            now = System.nanoTime();

            numUpdates += Math.floor((now - last) / nanosPerFrame);

            while (numUpdates >= 1) {
                update();
                render();
                //gui.renderBall();
                last += nanosPerFrame;
                numUpdates--;
            }
        }
    }

    public void fillTerrain(Graphics2D g2) {
        for (int i=0; i<terrain.heightmap.length-xDim; i++) {
            if ((i+1)%xDim == 0) {
                // Go next
            } else {
                int x1 = i/xDim;
                int y1 = i%yDim;

                float val1 = terrain.heightmap[i];
                float val2 = terrain.heightmap[i+1];
                float val3 = terrain.heightmap[i+xDim];
                float val4 = terrain.heightmap[i+1+xDim];

                float val = Math.max(val1, val2);
                val = Math.max(val, val3);
                val = Math.max(val, val4);

                if (val >= 0.5) {
                    g2.setColor(new Color(0, val, 0));
                } else {
                    g2.setColor(new Color(0, 0, val));
                }
                g2.fillRect(scale*x1, scale*y1, scale, scale);
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

        int xx = (int) ((ball.state.position.x - xTop)*unitSizePixelsX);
        int yy = (int) ((ball.state.position.y - yTop)*unitSizePixelsY);
        g2.setColor(Color.WHITE);
        g2.fillArc(xx, yy, (int) unitSizePixelsX, (int) unitSizePixelsY, 0, 360);

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