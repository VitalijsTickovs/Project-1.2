package physics;

import javax.swing.event.MouseInputListener;

import java.awt.Point;
import java.awt.event.MouseEvent;
import gameengine.Game;
import gui.BallVelocityInput;
import utility.math.Vector2;

public class MouseInputReader extends BallVelocityInput implements MouseInputListener {

    public MouseInputReader(Game game) {
        super(game);
        isListening = false;
    }

    private boolean isListening;

    @Override
    public void readyForNextInput() {
        isListening = true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        boolean clickedLeftMouseButton = e.getButton() == 1;
        if (clickedLeftMouseButton) {
            Vector2 mousePosition = getMousePosition(e);
            game.setShotVector(calculateShotForce(mousePosition));
        }
        isListening = false;
    }

    private Vector2 calculateShotForce(Vector2 mousePosition) {
        Vector2 ballPosition = game.gameState.getBall().state.position;
        Vector2 deltaPosition = ballPosition.deltaPositionTo(mousePosition);

        clampVector(deltaPosition);

        
    }

    private void clampVector(Vector2 deltaPosition) {
        double maxVectorLength = 5;
        double vectorLength = deltaPosition.length();
        if (vectorLength <= maxVectorLength) {
            return;
        }
        double maxVelocity = game.gameState.getBall().maxSpeed;
        deltaPosition.normalize().scale(vectorLength / maxVelocity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Vector2 mousePosition = getMousePosition(e);
    }

    private Vector2 getMousePosition(MouseEvent e) {
        Point point = game.frame.getMousePosition();
        return new Vector2(point.getX(), point.getY());
    }

    // region Unused
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

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

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    // endregion
}
