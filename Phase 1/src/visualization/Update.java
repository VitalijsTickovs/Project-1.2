package visualization;

import bot.botimplementations.IBot;
import datastorage.GameState;
import gui.shotinput.BallVelocityInput;
import gui.shotinput.MouseInputReader;
import utility.math.Vector2;
import visualization.gameengine.Game;

import java.util.ArrayList;

public class Update {
    private final GameState gameState;

    private ArrayList<Vector2> ballPositions = new ArrayList<Vector2>();
    private BallVelocityInput ballVelocityInput;

    private int numShots;
    private Vector2 shotForce;

    private IBot bot = null;
    private Thread botThread;

    public boolean drawArrow = false;

    public Update(GameState gameState) {
        this.gameState = gameState;

    }

    public ArrayList<Vector2> getBallPositions() {
        return ballPositions;
    }

    public void setBallVelocityInput(BallVelocityInput ballVelocityInput) {
        this.ballVelocityInput = ballVelocityInput;
    }

    public void setBot(IBot bot) {
        this.bot = bot;
    }

    public void setShotForce(Vector2 shotForce) {
        this.shotForce = shotForce;
    }

    public IBot getBot() {
        return bot;
    }

    public void setBotThread(Thread botThread) {
        this.botThread = botThread;
    }

    public int getNumShots() {
        return numShots;
    }

    public Vector2 getShotForce() {
        return shotForce;
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

    private void resetStartingVariables() {
        numShots = 0;
        shotForce = null;
        ballPositions = new ArrayList<Vector2>();
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

    private void setManualInputType(Game game) {
        ballVelocityInput = new MouseInputReader(game);
    }


    private boolean hasReachedTarget() {
        double distance = gameState.getBall().state.position.copy()
                .translate(gameState.getTerrain().target.position.copy().scale(-1)).length();
        return distance <= gameState.getTerrain().target.radius;
    }

    private void resetBotThread() {
        botThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Calculating shot...");
                shotForce = bot.findBestShot(gameState);
                System.out.println("Velocity: " + shotForce);
                System.out.println("Number of simulations: "+bot.getNumSimulations());
                System.out.println("Number of iterations: "+bot.getNumIterations());
            }
        });
    }

    private void handleInput() {
        if (isSimulationFinished() && !hasReachedTarget()) {
                resetBotThread();
                botThread.start();
        }
    }

    private boolean shouldPushBall() {
        boolean ballStopped = ballPositions.size() == 0;
        boolean ballHasNotBeenPushed = shotForce != null;
        return ballStopped && ballHasNotBeenPushed && !hasReachedTarget();
    }

    private void simulateShot() {

        if (shouldPushBall()) {
            ballPositions = gameState.simulateShot(shotForce);
            numShots++;
            shotForce = null;
            drawArrow = false;
        }
    }

    public void updateLoop(){
        handleBallInWater();
        handleInput();
        simulateShot();
    }
}
