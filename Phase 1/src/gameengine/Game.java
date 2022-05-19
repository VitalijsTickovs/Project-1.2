package gameengine;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

import datastorage.Ball;
import datastorage.GameState;
import utility.math.Vector2;
import gui.GameStateRenderer;
import gui.InterfaceFactory;
import gui.ShotInputWindow;
import bot.botimplementations.IBot;
import bot.botimplementations.BotFactory;
import bot.botimplementations.GradientDescentBot;
import bot.botimplementations.HillClimbingBot;
import bot.botimplementations.ParticleSwarmBot;
import bot.heuristics.FinalEuclidianDistanceHeuristic;
import gui.BallVelocityInput;

public class Game extends JPanel implements Runnable, GameObject {
    public JFrame frame;
    public GameState gameState;
    public int numShots;

    private final int FPS;
    private boolean running;
    private Thread thread;
    private Vector2 shotForce;
    private Camera camera;
    private BallVelocityInput ballVelocityInput;
    private IBot bot = null;
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

        setupInitialBot();
        FPS = fps;
        createGameState();
        resetStartingVariables();
        setManualInputType();
        // createTerrain();
        createCamera();
        createRenderer();
        createFrame();
        createInput();
    }

    private void setupInitialBot() {
        setBot(BotFactory.getBot(BotFactory.BotImplementations.PARTICLE_SWARM));
        resetBotThread();
    }

    private void createInput() {
        input = new Input();
        setFocusable(true);
        requestFocus();
        addKeyListener(input);
    }

    private void resetBotThread() {
        botThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Calculating shot...");
                shotForce = bot.findBestShot(gameState);
                System.out.println("Velocity: " + shotForce);
            }
        });
    }

    private void createGameState() {
        gameState = reader.GameStateLoader.readFile();
        BotFactory.setTerrain(gameState.getTerrain());
    }

    private void resetStartingVariables() {
        numShots = 0;
        running = false;
        shotForce = null;
    }

    private void setManualInputType() {
        ballVelocityInput = new ShotInputWindow(this);
    }

    private void createCamera() {
        camera = new Camera(15, 15);
        camera.xPos = gameState.getBall().state.position.x;
        camera.yPos = gameState.getBall().state.position.y;
    }

    private void createRenderer() {
        gameStateRenderer = new GameStateRenderer(gameState);
    }

    private void createFrame() {
        Vector2 frameSize = new Vector2((int) (camera.WIDTH * gameStateRenderer.PIXELS_PER_GAME_UNIT),
                (int) (camera.HEIGHT * gameStateRenderer.PIXELS_PER_GAME_UNIT));
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

    public void setBot(IBot bot) {
        this.bot = bot;
    }

    /**
     * Checks if a key was pressed.
     * 
     * @param key The key to check. see {@code Input}
     * @return {@code true} if it was pressed and {@code false} otherwise
     */
    public boolean checkKeyPressed(int key) {
        return input.isPressed(key);
    }

    /**
     * Checks if a key is being held down.
     * 
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
        handleInput();
        simulateShot();
        moveBall();
        moveCamera();
        handleKeyInputs();
    }

    private void handleBallInWater() {
        if (isSimulationFinished()) {
            boolean isBallInWater = gameState.getTerrain().terrainFunction.valueAt(gameState.getBall().state.position.x,
                    gameState.getBall().state.position.y) < 0;
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
        shotForce = null;
        ballPositions = new ArrayList<Vector2>();
    }

    private void handleInput() {
        if (isSimulationFinished() && !hasReachedTarget()) {
            if (bot == null) {
                ballVelocityInput.readyForNextInput();
            } else {
                resetBotThread();
                botThread.start();
            }
        }
    }

    private boolean hasReachedTarget() {
        double distance = gameState.getBall().state.position.copy()
                .translate(gameState.getTerrain().target.position.copy().scale(-1)).length();
        return distance <= gameState.getTerrain().target.radius;
    }

    /**
     * @return true, if the ball has stopped and the input window should open
     */
    private boolean isSimulationFinished() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean notWaitingForBot = (bot != null && !botThread.isAlive()) || (bot == null);
        boolean ballHasBeenPushed = shotForce == null;
        return ballHasBeenPushed && notWaitingForBot && ballStopped;
    }

    private void simulateShot() {
        if (shouldPushBall()) {
            ballPositions = gameState.simulateShot(shotForce);
            numShots++;
            shotForce = null;
        }
    }

    private boolean shouldPushBall() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean ballHasNotBeenPushed = shotForce != null;
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
        Ball ball = gameState.getBall();
        camera.xPos += (ball.state.position.x - camera.xPos) / 10;
        double zOffset = ball.getZCoordinate(gameState.getTerrain());
        camera.yPos += (ball.state.position.y - zOffset - camera.yPos) / 10;
    }

    // region keyInputs
    private void handleKeyInputs() {
        checkResetGame();
        changeBotImplementation();
        input.removeFromPressed();
    }

    private void checkResetGame() {
        if (checkKeyPressed(Input.R)) {
            resetGame();
        }
    }

    private void changeBotImplementation() {
        if (checkKeyPressed(Input.H)) {
            setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
        }
        if (checkKeyPressed(Input.P)) {
            setBot(BotFactory.getBot(BotFactory.BotImplementations.PARTICLE_SWARM));
        }
        if (checkKeyPressed(Input.G)) {
            setBot(BotFactory.getBot(BotFactory.BotImplementations.GRADIENT_DESCENT));
        }
    }
    // endregion
    // endregion

    // region Render
    private GameStateRenderer gameStateRenderer;

    /**
     * Renders the game
     */
    public void render() {
        BufferedImage gameStateImage = gameStateRenderer.getSubimage(camera);
        drawImage(gameStateImage);
        gameStateImage.flush();
    }

    private void drawImage(BufferedImage gameStateImage) {
        Graphics2D gameg2 = (Graphics2D) getGraphics();
        gameg2.drawImage(gameStateImage, null, 0, 0);
        gameg2.dispose();
    }
    // endregion

    public void setShotForce(Vector2 newShotVector) {
        shotForce = newShotVector;
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
        Game g = new Game(256);
        g.start();
    }
}