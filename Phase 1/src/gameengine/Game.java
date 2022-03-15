package gameengine;

import java.util.ArrayList;

public class Game implements Runnable, GameObject {
    private int FPS;
    private boolean running;
    private ArrayList<GameObject> gameObjects;

    public static void main(String[] args) {
        Game game = new Game(2);
        game.start();
    }

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        this.FPS = fps;
        this.running = false;
<<<<<<< Updated upstream
=======
        xDim = 1500;
        yDim = 1500;
        xTop = -50;
        yTop = -50;
        xBottom = 50;
        yBottom = 50;
        unitSizePixelsX = (double)(xDim*scale)/(xBottom - xTop);
        unitSizePixelsY = (double)(yDim*scale)/(yBottom - yTop);
        terrainImage = new BufferedImage(xDim*scale, xDim*scale, BufferedImage.TYPE_4BYTE_ABGR);
        terrain = new Terrain("sin((x+y)/7)", new Vector2(xTop, yTop), new Vector2(xBottom, yBottom), 0.15, 0.07, xDim, yDim);
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
>>>>>>> Stashed changes
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
                // update();
                // render();
                //gui.renderBall();
                last += nanosPerFrame;
                numUpdates--;
                System.out.print(".");
            }
        }
    }

    /**
     * Updates the state of the gamea each step
     */
    public void update() {
        for (GameObject obj : gameObjects) {
            obj.update();
        }
    }

    /**
     * Renders the game
     */
    public void render() {

    }

    /**
     * Starts the game.
     * WARNING: Uses a sepparate thread
     */
    public void start() {
        running = true;
        Thread t = new Thread(this, "Game loop thread");
        t.start();
    }
}