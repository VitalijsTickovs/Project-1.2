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
import gui.shotinput.BallVelocityInput;
import gui.shotinput.IClickListener;
import gui.shotinput.MouseInputReader;
import bot.botimplementations.IBot;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import bot.botimplementations.BotFactory;

public class Game extends JPanel implements Runnable, GameObject, MouseListener {
    public int numShots;
    public boolean drawArrow = false;

    public JFrame frame;
    public GameState gameState;
    public Camera camera;
    public static Game game;

    private final int FPS;
    private boolean running;
    private Thread gameThread;
    private Vector2 shotForce;
    private IBot bot = null;
    private Thread botThread;
    public Input input;
    private BallVelocityInput ballVelocityInput;
    private GameStateRenderer gameStateRenderer;
    private ArrayList<Vector2> ballPositions = new ArrayList<Vector2>();

    public static void main(String[] args) {
        Game g = new Game(256);
        g.start();
    }

    // region Startup
    /**
     * @param fps The target FPS (frames per second) of the game
     */
    public Game(int fps) {
        game = this;
        running = false;

        // setupInitialBot();
        FPS = fps;
        createGameState();
        setManualInputType();
        createCamera();
        createRenderer();
        createFrame();
        createInput();
    }

    private void setupInitialBot() {
        setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
        resetBotThread();
    }

    private void createInput() {
        input = new Input();
        setFocusable(true);
        requestFocus();
        addKeyListener(input);
        addMouseListener(this);
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

    private void setManualInputType() {
        // ballVelocityInput = new ShotInputWindow(this);
        ballVelocityInput = new MouseInputReader(this);
    }

    private void resetStartingVariables() {
        numShots = 0;
        shotForce = null;
        ballPositions = new ArrayList<Vector2>();
    }

    private void createCamera() {
        camera = new Camera(25, 20);
        camera.xPos = gameState.getBall().state.position.x;
        camera.yPos = gameState.getBall().state.position.y;
    }

    private void createRenderer() {
        gameStateRenderer = new GameStateRenderer(gameState);
    }

    private void createFrame() {
        Vector2 frameSize = new Vector2((int) (camera.WIDTH * GameStateRenderer.PIXELS_PER_GAME_UNIT),
                (int) (camera.HEIGHT * GameStateRenderer.PIXELS_PER_GAME_UNIT));
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
            boolean isBallInWater = gameState.getTerrain().getTerrainFunction().valueAt(
                    gameState.getBall().state.position.x,
                    gameState.getBall().state.position.y) < 0;
            if (isBallInWater) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        gameState.getBall().state.position = gameState.getTerrain().ballStartingPosition;
        if (bot != null && botThread.isAlive()) {
            // End the bot thread if it is still running
            try {
                botThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        resetStartingVariables();
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
        boolean notWaitingForBot = (bot == null || botThread == null) || (bot != null && !botThread.isAlive());
        boolean ballHasBeenPushed = shotForce == null;
        return ballHasBeenPushed && notWaitingForBot && ballStopped;
    }

    private void simulateShot() {
        if (shouldPushBall()) {
            ballPositions = gameState.simulateShot(shotForce);
            numShots++;
            shotForce = null;
            drawArrow = false;
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
        double zOffset = ball.state.getZCoordinate(gameState.getTerrain());
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
    public void render() {
        // BufferedImage gameStateImage = gameStateRenderer.getSubimage(camera, drawArrow, true);
        BufferedImage gameStateImage = gameStateRenderer.getMinimap(camera);
        drawImage(gameStateImage);
        gameStateImage.flush();
    }

    private void drawImage(BufferedImage gameStateImage) {
        Graphics2D gameg2 = (Graphics2D) getGraphics();
        gameg2.drawImage(gameStateImage, null, 0, 0);
        gameg2.dispose();
    }
    // endregion

    public synchronized void setShotForce(Vector2 newShotVector) {
        shotForce = newShotVector;
    }

    /**
     * Starts the game.
     * WARNING: Uses a separate thread
     */
    public void start() {
        running = true;
        gameThread = new Thread(this, "Game loop thread");
        gameThread.start();
    }

    public void setBot(IBot bot) {
        this.bot = bot;
    }

    // region Static accessor methods
    /**
     * @return mouse position in the window as a vector in pixels where point(0,0)
     *         is in the top left corner of the screen
     */
    public static Vector2 getMousePositionInPixels() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        Point windowPosition = game.getLocationOnScreen();
        Vector2 mousePositionInWindow = new Vector2(mousePoint.getX() - windowPosition.getX(),
                mousePoint.getY() - windowPosition.getY());
        return mousePositionInWindow;
    }

    /**
     * @return the position of the mouse in the window in game units where
     *         point(0,0) is in the middle of the screen
     */
    public static Vector2 getMiddleMousePosition() {
        // Here: mPos = mousePosition
        Vector2 mPosInWindow = Game.getMousePositionInPixels(); // In pixels
        Vector2 mPosInGameUnits = mPosInWindow.scale(1d / GameStateRenderer.PIXELS_PER_GAME_UNIT); // In game units from
                                                                                                   // now on

        double halfCameraWidth = Game.game.camera.WIDTH / 2;
        double halfCameraHeight = Game.game.camera.HEIGHT / 2;
        Vector2 mPosFromCenterOfScreen = mPosInGameUnits
                .translate(new Vector2(halfCameraWidth, halfCameraHeight).reversed());

        double ballZOffset = game.gameState.getBall().state.getZCoordinate(game.gameState.getTerrain());
        Vector2 deltaPosFromBall = mPosFromCenterOfScreen.translated(Game.getMiddleOfWindowPosition()).translate(0,
                ballZOffset);
        Vector2 ballPosition = game.gameState.getBall().state.position;
        Vector2 deltaPosition = ballPosition.deltaPositionTo(deltaPosFromBall);

        return deltaPosition;
    }

    /**
     * @return the position of the middle of the window as if it was placed on the
     *         map in game units.
     */
    public static Vector2 getMiddleOfWindowPosition() {
        Camera cam = game.camera;
        Vector2 middlePosition = new Vector2(cam.xPos, cam.yPos);
        return middlePosition;
    }
    // endregion

    // region Mouse listening
    public ArrayList<IClickListener> clickListeners = new ArrayList<>();

    @Override
    public void mouseClicked(MouseEvent e) {
        int key = e.getButton();
        if (key == MouseEvent.BUTTON1) {
            for (IClickListener listener : clickListeners) {
                listener.mouseWasClicked();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    // endregion
}