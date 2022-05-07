package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.LinkedList;

import Data_storage.Ball;
import Data_storage.GameState;
import Data_storage.Terrain;
import Data_storage.Vector2;
import GUI.InterfaceFactory;
import GUI.ShotInputWindow;
import Physics.PhysicsEngine;
import Physics.RungeKutta;
import Reader.*;

public class Game extends Canvas implements Runnable, GameObject {
    public JFrame frame;
    public Vector2 shotVector;
    public int numShots;

    private final int FPS;
    private boolean running;
    private Thread thread;
    private BufferedImage terrainImage;
    private Renderer renderer;
    private Camera cam;
    private ShotInputWindow shotInputWindow;
    private GameState gameState;

    // region Startup
    /**
     * @param fps The target FPS (frames per second) of the game
     */
    public Game(int fps) {
        FPS = fps;
        resetStartingVariables();
        createInputWindow();
        createGameState();
        //createTerrain();
        createCamera();
        createRenderer();
        createTerrainImage();
        createFrame();
    }

    private void createGameState() {
        gameState = Reader.readFile();
        gameState.getTerrain().calculateHeightMap(1024, 1.0);
    }

    private void resetStartingVariables() {
        numShots = 0;
        running = false;
        shotVector = null;
    }

    private void createInputWindow() {
        shotInputWindow = new ShotInputWindow(this);
    }

    private void createCamera() {
        cam = new Camera();
        cam.width = 75;
        cam.height = 75;
        cam.x = gameState.getBall().state.position.x;
        cam.y = gameState.getBall().state.position.y;
    }

    private void createRenderer() {
        renderer = new Renderer();
        renderer.heightRange = 20;
        renderer.terrain = gameState.getTerrain();
        renderer.cam = cam;
        renderer.ball = gameState.getBall();
        renderer.unitSizePixels = 10;
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
    // endregion

    // region Update
    private ArrayList<Vector2> ballPositions = new ArrayList<Vector2>();

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
            boolean isBallInWater = gameState.getTerrain().terrainFunction.valueAt(gameState.getBall().state.position.x, gameState.getBall().state.position.y) <= 0;
            if (isBallInWater) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        gameState.getBall().state.position = gameState.getTerrain().ballStartingPosition;
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
            ballPositions = gameState.simulateShot(shotVector);
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
            gameState.setBallPosition(ballPositions.get(0));
            ballPositions.remove(0);
        }
    }

    private void moveCamera() {
        cam.x += (gameState.getBall().state.position.x - cam.x) / 10;
        cam.y += (gameState.getBall().state.position.y - cam.y) / 10;
    }
    // endregion

    // region Render
    private BufferStrategy bufferStrategy;

    /**
     * Renders the game
     */
    public void render() {
        if (isBufferStrategyNull()) {
            return;
        }
        updateGraphics();
    }

    private boolean isBufferStrategyNull() {
        bufferStrategy = getBufferStrategy();
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
        Game g = new Game(256);
        g.start();
    }
}