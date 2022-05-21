package gui.shotinput;

import datastorage.Ball;
import gameengine.Game;
import gui.GameStateRenderer;
import utility.UtilityClass;
import utility.math.Vector2;

public class MouseInputReader extends BallVelocityInput implements IClickListener {

    public MouseInputReader(Game game) {
        super(game);
        game.clickListeners.add(this);
    }

    private boolean isListening = false;

    @Override
    public void readyForNextInput() {
        if (isListening) {
            return;
        }
        isListening = true;
        Game.game.drawArrow = true;
    }

    public void mouseWasClicked(){
        if (!isListening) {
            return;
        }
        Game.game.setShotForce(getForce(Game.getMiddleMousePosition()));
        isListening = false;
    }

    private synchronized Vector2 getForce(Vector2 deltaPosition) {
        double maxVelocity = Ball.maxSpeed;
        Vector2 clampedMousePosition = UtilityClass.clamp(deltaPosition, 0, GameStateRenderer.MAX_ARROW_LENGTH);

        double forceValue = (clampedMousePosition.length() / GameStateRenderer.MAX_ARROW_LENGTH) * maxVelocity;
        return clampedMousePosition.normalized().scale(forceValue);
    }
}
