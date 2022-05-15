package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import Data_storage.GameState;
import Data_storage.Vector2;
import GUI.InterfaceFactory;
import GUI.ShotInputWindow;
import Reader.*;
import bot.Bot;

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
    private Bot bot;
    private Thread botThread;

    // region Startup
    /**
     * @param fps The target FPS (frames per second) of the game
     */
    public Game(int fps) {
        FPS = fps;
        createGameState();
        startBotThread();
        resetStartingVariables();
        createInputWindow();
        //createTerrain();
        createCamera();
        createRenderer();
        createTerrainImage();
        createFrame();
    }
    
    private void startBotThread(){
        bot = null;//new HillClimbingBot(new FinalEuclidianDistanceHeuristic(), 0.01, 8);
        botThread = new Thread(new Runnable() {
            @Override
            public void run() {
                shotVector = bot.findBestShot(gameState);
            }
        });
    }

    private void createGameState() {
        gameState = Reader.readFile();
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
        cam.width = 25;
        cam.height = 25;
        cam.x = gameState.getBall().state.position.x;
        cam.y = gameState.getBall().state.position.y;
    }

    private void createRenderer() {
        renderer = new Renderer();
        renderer.heightRange = 20;
        renderer.terrain = gameState.getTerrain();
        renderer.cam = cam;
        renderer.ball = gameState.getBall();
        renderer.unitSizePixels = 20;
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
            if (bot == null) {
                shotInputWindow.openWindow();
            } else {
                botThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        shotVector = bot.findBestShot(gameState);
                    }
                });
                botThread.start();
            }
        }
    }

    private boolean hasReachedTarget() {
        double distance = gameState.getBall().state.position.copy().translate(gameState.getTerrain().target.position.copy().scale(-1)).length();
        return distance <= gameState.getTerrain().target.radius;
    }

    /**
     * @return true, if the ball has stopped and the input window should open
     */
    private boolean isSimulationFinished() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean inputWindowClosed = (bot != null && !botThread.isAlive()) || (bot == null && !shotInputWindow.isOpen);
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
        return ballStopped && ballHasNotBeenPushed && !hasReachedTarget();
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