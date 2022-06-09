package gui.shotinput;

import datastorage.Ball;
import gui.GameStateRenderer;
import utility.UtilityClass;
import utility.math.Vector2;
import visualization.jmonkeyrender.Renderer;

public class MouseInputReader3d extends BallVelocityInput implements IClickListener {

        public MouseInputReader3d(Renderer renderer) {
            super(renderer);
            renderer.clickListeners.add(this);
        }

        private boolean isListening = false;

        public void readyForNextInput() {
            if (isListening) {
                return;
            }
            isListening = true;
            renderer.drawArrow = true;
        }

        public void mouseWasClicked(){
            if (!isListening) {
                return;
            }
            renderer.getUpdateLoop().setShotForce(getForce(renderer.getMousePosition()));
            //renderer.getUpdateLoop().setShotForce(getForce(renderer.getMiddleMousePosition()));
            isListening = false;
        }

        private synchronized Vector2 getForce(Vector2 deltaPosition) {
            double maxVelocity = Ball.maxSpeed;
            Vector2 clampedMousePosition = UtilityClass.clamp(deltaPosition, 0, GameStateRenderer.MAX_ARROW_LENGTH);

            double forceValue = (clampedMousePosition.length() / GameStateRenderer.MAX_ARROW_LENGTH) * maxVelocity;
            return clampedMousePosition.normalized().scale(forceValue);
        }
}
