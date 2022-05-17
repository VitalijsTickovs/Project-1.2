package gameengine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import Data_storage.GameState;
import Data_storage.Vector2;
import GUI.GameStateRenderer;
import GUI.InterfaceFactory;
import GUI.ShotInputWindow;
import Reader.*;
import bot.*;

public class Game extends JPanel implements Runnable, GameObject {
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
    private Input input;
    private static Game game;

    public static Game getInstance() {
        if (game == null) {
            game = new Game(60);
        }
        return game;
    }

    // region Startup
    /**
     * @param fps The target FPS (frames per second) of the game
     */
    public Game(int fps) {
        game = this;
        //bot = new HillClimbingBot(new FinalEuclidianDistanceHeuristic(), 0.01, 16, null);//new RandomBot(new FinalEuclidianDistanceHeuristic(), 100));
        //bot = new ParticleSwarmBot(new FinalEuclidianDistanceHeuristic(), 0.5, 0.5, 0.5, 100, 10);
        //bot = new HillClimbingBot(new FinalEuclidianDistanceHeuristic(), 0.01, 16, null);
        setBot(new HillClimbingBot(
                new FinalEuclidianDistanceHeuristic(),
                0.01,
                16,
                new ParticleSwarmBot(new FinalEuclidianDistanceHeuristic(), 0.5, 0.5, 0.5, 100, 10)
        ));
        botThread = new Thread(new Runnable() {
            @Override
            public void run() {
                shotVector = bot.findBestShot(gameState);
            }
        });
        FPS = fps;
        createGameState();
        resetBotThread();
        resetStartingVariables();
        createInputWindow();
        //createTerrain();
        createCamera();
        createRenderer();
        createTerrainImage();
        createFrame();
        createInput();
    }

    private void createInput() {
        input = new Input();
        setFocusable(true);
        requestFocus();
        addKeyListener(input);
    }
    
    private void resetBotThread(){
        botThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Calculating shot...");
                shotVector = bot.findBestShot(gameState);
                System.out.println("Velocity: "+shotVector);
            }
        });
    }

    private void createGameState() {
        gameState = GameStateLoader.readFile();
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
        cam.width = 15;
        cam.height = 15;
        cam.x = gameState.getBall().state.position.x;
        cam.y = gameState.getBall().state.position.y;
    }

    private void createRenderer() {
        renderer = new Renderer();
        renderer.heightRange = 20;
        renderer.terrain = gameState.getTerrain();
        renderer.cam = cam;
        renderer.ball = gameState.getBall();
        renderer.unitSizePixels = 40;
        renderer.createTerrainImage();
        gameStateRenderer = new GameStateRenderer(gameState, 40);
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

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    /**
     * Checks if a key was pressed.
     * @param key The key to check. see {@code Input}
     * @return {@code true} if it was pressed and {@code false} otherwise
     */
    public boolean checkKeyPressed(int key) {
        return input.isPressed(key);
    }

    /**
     * Checks if a key is being held down.
     * @param key The key to check. see {@code Input}
     * @return {@code true} if it is being helf down and {@code false} otherwise
     */
    public boolean checkKeyDown(int key) {
        return input.isDown(key);
    }

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
        // Reset the game
        if (checkKeyPressed(Input.R)) {
            resetGame();
        }
        if (checkKeyPressed(Input.H)) {
            setBot(new HillClimbingBot(
                    new FinalEuclidianDistanceHeuristic(),
                    0.01,
                    12,
                    new ParticleSwarmBot(new FinalEuclidianDistanceHeuristic(), 0.5, 0.5, 0.5, 100, 10)
            ));
        }
        if (checkKeyPressed(Input.P)) {
            setBot(new ParticleSwarmBot(
                    new FinalEuclidianDistanceHeuristic(),
                    0.5,
                    0.5,
                    0.5,
                    100,
                    10)
            );
        }
        if (checkKeyPressed(Input.G)) {
            setBot(new GradientDescentBot(
                    new FinalEuclidianDistanceHeuristic(),
                    0.01,
                    new ParticleSwarmBot(new FinalEuclidianDistanceHeuristic(), 0.5, 0.5, 0.5, 100, 10)
            ));
        }
        input.removeFromPressed();
    }

    private void handleBallInWater() {
        if (isSimulationFinished()) {
            boolean isBallInWater = gameState.getTerrain().terrainFunction.valueAt(gameState.getBall().state.position.x, gameState.getBall().state.position.y) < 0;
            if (isBallInWater) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        gameState.getBall().state.position = gameState.getTerrain().ballStartingPosition;
        numShots = 0;
        if (bot != null && botThread.isAlive()) {
            // End the bot thread if it is still running
            try {
                botThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        shotVector = null;
        ballPositions = new ArrayList<Vector2>();
    }

    private void handleOpenWindow() {
        if (isSimulationFinished() && !hasReachedTarget()) {
            if (bot == null) {
                shotInputWindow.openWindow();
            } else {
                resetBotThread();
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
    private GameStateRenderer gameStateRenderer;

    /**
     * Renders the game
     */
    public void render() {
        Graphics2D g2 = (Graphics2D) terrainImage.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0 ,0, terrainImage.getWidth(), terrainImage.getHeight());
        //renderer.render(g2);
        gameStateRenderer.render(g2, cam.x, cam.y, cam.width, cam.height, 0, 0);
        g2.dispose();

        g2 = (Graphics2D) getGraphics();
        g2.drawImage(terrainImage, null, 0, 0);
        g2.dispose();
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