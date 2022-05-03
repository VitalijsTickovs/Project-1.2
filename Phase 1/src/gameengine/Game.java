package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.LinkedList;

import Data_storage.Ball;
import Data_storage.Terrain;
import Data_storage.Vector2;
import GUI.InterfaceFactory;
import GUI.ShotInputWindow;
import Physics.PhysicsEngine;
import Reader.*;

public class Game extends Canvas implements Runnable, GameObject {
    public JFrame frame;
    public Vector2 shotVector;
    public int numShots;

    private final int FPS;
    private boolean running;
    private Thread thread;
    private BufferedImage terrainImage;
    private Terrain terrain;
    private Ball ball;
    private PhysicsEngine engine;
    private Renderer renderer;
    private Camera cam;
    private ShotInputWindow shotInputWindow;

    // region Startup
    /**
     * @param fps The target FPS (frames per second) of the game
     */
    public Game(int fps) {
        FPS = fps;
        resetStartingVariables();
        createInputWindow();
        createTerrain();
        createBall();
        createCamera();
        createRenderer();
        createTerrainImage();
        createFrame();
    }

    private void resetStartingVariables() {
        numShots = 0;
        running = false;
        shotVector = null;
    }

    private void createInputWindow() {
        shotInputWindow = new ShotInputWindow(this);
    }

    private void createTerrain() {
        terrain = TerrainLoader.readFile();
        terrain.calculateHeightMap(1024, 1.0);
    }

    private void createBall() {
        ball = new Ball(terrain.ballStartingPosition, Vector2.zeroVector());
        engine = new PhysicsEngine(terrain);
        engine.addBall(ball);
    }

    private void createCamera() {
        cam = new Camera();
        cam.width = 75;
        cam.height = 75;
        cam.xPosition = ball.state.position.x;
        cam.yPosition = ball.state.position.y;
    }

    private void createRenderer() {
        renderer = new Renderer();
        renderer.heightRange = 20;
        renderer.terrain = terrain;
        renderer.cam = cam;
        renderer.ball = ball;
        renderer.unitSizePixels = 10;
        renderer.game = this;
        renderer.createTerrainImage();
    }

    private void createTerrainImage() {
        terrainImage = new BufferedImage((int) (cam.width * renderer.unitSizePixels),
                (int) (cam.height * renderer.unitSizePixels), BufferedImage.TYPE_4BYTE_ABGR);
    }

    private void createFrame() {
        Vector2 frameSize = new Vector2((int) (cam.width * renderer.unitSizePixels),
                (int) (cam.height * renderer.unitSizePixels));
        frame = InterfaceFactory.createFrame("Crazy Putting", frameSize, false, null, null);
        frame.add(this);
        frame.setVisible(true);
    }
    // endregion

    // region Game loop
    /**
     * Runs the game loop
     */
    public void run() {
        final double nanosPerFrame = 1000000000.0 / FPS; // How many nanoseconds should pass between frames
        long last = System.nanoTime();

        int fps = 0;
        double numUpdates = 0;
        double timer = 0;

        while (running) {
            long now = System.nanoTime();
            numUpdates += (now - last) / nanosPerFrame;

            timer += now - last;

            if (numUpdates >= 1) {
                printFPS(fps); // For debugging

                update();
                render();
                fps++;
                numUpdates--;
            }

            last = now;

            boolean isTimerOutOfBounds = timer > 1000000000.0;
            if (isTimerOutOfBounds) {
                timer = 0;
                fps = 0;
            }
        }
    }

    private void printFPS(int fps){
        System.out.print("*");
        if (fps % 60 == 0) {
            System.out.println();
        }
    }
    // endregion

    // region Update
    private LinkedList<Vector2> ballPositions = new LinkedList<Vector2>();

    /**
     * Updates the state of the game each step
     */
    public void update() {
        handleBallInWater();
        handleOpenWindow();
        // Find the positions after a shot
        simulateShot();
        // Update the ball position if it has been calculated
        moveBall();
        moveCamera();
    }

    private void handleBallInWater() {
        if (isSimulationFinished()) {
            boolean isBallInWater = terrain.terrainFunction.valueAt(ball.state.position.x, ball.state.position.y) <= 0;
            if (isBallInWater) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        ball.state.position = terrain.ballStartingPosition;
        numShots = 0;
    }

    private void handleOpenWindow() {
        if (isSimulationFinished()) {
            shotInputWindow.openWindow();
        }
    }

    /**
     * @return true, if the ball has stopped and the input window should open
     */
    private boolean isSimulationFinished() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean inputWindowClosed = !shotInputWindow.isOpen;
        boolean ballHasBeenPushed = shotVector == null;
        return ballHasBeenPushed && inputWindowClosed && ballStopped;
    }

    private void simulateShot() {
        if (shouldPushBall()) {
            ballPositions = engine.simulateShot(shotVector, ball);
            numShots++;
            shotVector = null;
        }
    }

    private boolean shouldPushBall() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean ballHasNotBeenPushed = shotVector != null;
        return ballStopped && ballHasNotBeenPushed;
    }

    private void moveBall() {
        boolean ballMoving = ballPositions.size() != 0;
        if (ballMoving) {
            ball.state.position = ballPositions.get(0);
            ballPositions.remove(0);
        }
    }

    private void moveCamera() {
        final int MOVEMENT_SMOOTHNESS = 10;
        cam.xPosition += (ball.state.position.x - cam.xPosition) / MOVEMENT_SMOOTHNESS;
        cam.yPosition += (ball.state.position.y - cam.yPosition) / MOVEMENT_SMOOTHNESS;
    }
    // endregion

    // region Render
    private BufferStrategy bufferStrategy;

    /**
     * Renders the game
     */
    public void render() {
        bufferStrategy = getBufferStrategy();
        if (isBufferStrategyNull()) {
            return;
        }
        updateGraphics();
    }

    private boolean isBufferStrategyNull() {
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            return true;
        }
        return false;
    }

    private void updateGraphics() {
        Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
        g2.drawImage(terrainImage, 0, 0, terrainImage.getWidth(), terrainImage.getHeight(), null);

        renderer.render(g2);

        g2.dispose();
        bufferStrategy.show();
    }
    // endregion

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