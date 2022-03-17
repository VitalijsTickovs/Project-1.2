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
    public JFrame frame;
    private Terrain terrain;
    private Ball ball;
    private PhysicsEngine engine;
    private Renderer renderer;
    private Camera cam;
    private ShotInput shotInput;

    public Vector2 shot;

    public int numShots;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        numShots = 0;
        FPS = fps;
        running = false;
        shotInput = new ShotInput();
        shotInput.game = this;
        shot = null;
        // Set up terrain
        terrain = Reader.readFile();
        terrain.calculateHeightMap(1024, 1.0);
        // Set up the ball
        ball = new Ball(terrain.ballStartingPosition, Vector2.zeroVector);
        engine = new PhysicsEngine();
        engine.terrain = terrain;
        engine.addBall(ball);
        // Set up the camera
        cam = new Camera();
        cam.width = 40;
        cam.height = 40;
        cam.x = ball.state.position.x;
        cam.y = ball.state.position.y;
        // Setup the renderer
        renderer = new Renderer();
        renderer.heightRange = 20;
        renderer.terrain = terrain;
        renderer.cam = cam;
        renderer.ball = ball;
        renderer.unitSizePixels = 10;
        renderer.game = this;
        renderer.createTerrainImage();
        // Set up the terrain image
        terrainImage = new BufferedImage((int) (cam.width*renderer.unitSizePixels), (int) (cam.height*renderer.unitSizePixels), BufferedImage.TYPE_4BYTE_ABGR);
        // Set up the frame
        frame = new JFrame();
        frame.setTitle("Crazy Putting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) (cam.width*renderer.unitSizePixels), (int) (cam.height*renderer.unitSizePixels));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);
    }

    /**
     * Runs the game loop
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
                fps++;
                numUpdates--;
            }

            last = now;

            if (timer > 1000000000.0) {
                //System.out.println("FPS: "+fps);
                timer = 0;
                fps = 0;
            }
        }
    }

    private ArrayList<Vector2> points = new ArrayList<Vector2>();

    /**
     * Updates the state of the game each step
     */
    public void update() {
        if (shot == null && !shotInput.isOpen && points.size() == 0) {
            // Check if in water
            if (terrain.terrainFunction.valueAt(ball.state.position.x, ball.state.position.y) <= 0) {
                // Reset the shot
                ball.state.position = terrain.ballStartingPosition;
                numShots = 0;
            } else {
                shotInput.openWindow();
            }
        }
        // Find the positions after a shot
        if (points.size() == 0 && shot != null) {
            points = engine.simulateShot(shot, ball);
            numShots++;
            shot = null;
        }
        // Update the ball position if it has been calculated
        if (points.size() != 0) {
            ball.state.position = points.get(0);
            points.remove(0);
        }
        // Update camera position
        cam.x += (ball.state.position.x - cam.x)/10;
        cam.y += (ball.state.position.y - cam.y)/10;
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

        renderer.render(g2);

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