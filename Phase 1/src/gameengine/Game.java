package gameengine;

import java.util.ArrayList;

public class Game implements Runnable, GameObject {
    private int FPS;
    private boolean running;
    private ArrayList<GameObject> gameObjects;

    /**
     * Constructor.
     * 
     * @param fps The wanted FPS (frames per second) of the game
     */
    public Game(int fps) {
        this.FPS = fps;
        this.running = false;
    }

    /**
     * Main method to run the game loop
     */
    public void run() {
        long last = System.nanoTime();
        final double ns = 1000000000.0 / FPS; // How many nanoseconds should pass between game steps
        long now = System.nanoTime();

        double numUpdates = 0;

        while (running) {
            now = System.nanoTime();

            numUpdates += (now - last) / ns;

            while (numUpdates >= 1) {
                update();
                render();
                numUpdates--;
            }

            last = now;
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