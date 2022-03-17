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
    //private int numVerteces;
    //private int unitSizePixels;

    //private int cameraWidth, cameraHeight;

    private Renderer renderer;

    private Camera cam;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        // Your path to GitHub here
        //String csvFile = "C:/Users/staso/Documents/GitHub/Project-1.2/Phase 1/src/Reader/UserInput.csv";
        //Terrain terrainT = Reader.readFile(csvFile);
        this.FPS = fps;
        this.running = false;

        //cameraWidth = 10;
        //cameraHeight = 10;

        //numVerteces = 512;

        //unitSizePixels = 40;



        //int width = cameraWidth*unitSizePixels;//(int) (terrain.limitingCorner.x - terrain.startingCorner.x)*unitSizePixels;//
        //int height = cameraHeight*unitSizePixels;//;//(int) (terrain.limitingCorner.y - terrain.startingCorner.y)*unitSizePixels;//
        //System.out.println(terrain.terrainFunction);
        // "0.1*sin(e**(-(x**2+y**2)/40)*x*y)"
        // Set up terrain
        terrain = new Terrain("sin((x+y)/7)+0.5", 0.2, 0.1, new Vector2(-15, -15), new Vector2(15, 15));
        terrain.calculateHeightMap(1024, 1.0);
        terrain.target = new Target();
        terrain.target.position = new Vector2(4, 4);
        terrain.target.radius = 1;
        // Set up the ball
        ball = new Ball(new Vector2(0, 0), new Vector2(3, -5));
        engine = new PhysicsEngine();
        engine.terrain = terrain;
        engine.addBall(ball);
        // Set up the camera
        cam = new Camera();
        cam.width = 40;
        cam.height = 40;
        cam.x = ball.state.position.x - cam.width/2;
        cam.y = ball.state.position.y - cam.height/2;
        // Setup the renderer
        renderer = new Renderer();
        renderer.terrain = terrain;
        renderer.cam = cam;
        renderer.ball = ball;
        renderer.unitSizePixels = 10;
        renderer.createTerrainImage();
        // Set up the terrain image
        terrainImage = new BufferedImage((int) (cam.width*renderer.unitSizePixels), (int) (cam.height*renderer.unitSizePixels), BufferedImage.TYPE_4BYTE_ABGR);
        // Set up the frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) (cam.width*renderer.unitSizePixels), (int) (cam.height*renderer.unitSizePixels));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(this);
        frame.setVisible(true);
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

    private ArrayList<Vector2> points = new ArrayList<Vector2>();

    /**
     * Updates the state of the game each step
     */
    public void update() {
        if (points.size() == 0) {
            points = engine.simulateShot(new Vector2(Math.random()*10-5, Math.random()*10-5), ball);
        }
        if (points.size() != 0) {
            ball.state.position = points.get(0);
            points.remove(0);
            // Update camera position
            cam.x += (ball.state.position.x - cam.x)/10;
            cam.y += (ball.state.position.y - cam.y)/10;
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

        renderer.render(g2);
        /*fillTerrain(g2);

        // Render ball
        int ballWidth = unitSizePixels/4;
        int ballHeight = unitSizePixels/4;
        int xx = cameraWidth*unitSizePixels/2 - ballWidth/2;//(int) ((ball.state.position.x - terrain.startingCorner.x)*unitSizePixels);////
        int yy = cameraHeight*unitSizePixels/2 - ballHeight/2;//(int) ((ball.state.position.y - terrain.startingCorner.y)*unitSizePixels);////
        g2.setColor(Color.WHITE);
        g2.fillArc(xx, yy, ballWidth, ballHeight, 0, 360);*/

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