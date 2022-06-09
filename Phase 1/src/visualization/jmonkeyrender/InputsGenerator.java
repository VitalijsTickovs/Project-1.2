package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Vector2f;
import com.jme3.scene.debug.Arrow;

public class InputsGenerator {
    private Renderer renderer;

    public InputsGenerator(Renderer renderer) {
        this.renderer = renderer;
    }

    protected void initKeys() {
        // You can map one or several inputs to one named action
        renderer.getInputManager().addMapping("HC Bot",  new KeyTrigger(KeyInput.KEY_H));
        renderer.getInputManager().addMapping("PS Bot",   new KeyTrigger(KeyInput.KEY_P));
        renderer.getInputManager().addMapping("GD Bot",  new KeyTrigger(KeyInput.KEY_G));
        renderer.getInputManager().addMapping("Rule Bot", new KeyTrigger(KeyInput.KEY_B));
        renderer.getInputManager().addMapping("Manual Input", new KeyTrigger(KeyInput.KEY_L));
        renderer.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        // Add the names to the action listener.
        renderer.getInputManager().addListener(keyListener, "PS Bot","HC Bot","GD Bot","Rule Bot","Manual Input","Reset");
    }

    protected void initMouse(){
        renderer.getInputManager().addMapping("MiddleButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        renderer.getInputManager().addListener(mouseListener, "MiddleButton");
    }

    private final ActionListener mouseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean clicked, float v) {

            if(name.equals("MiddleButton") && !clicked){

            }
        }
    };

    private final ActionListener keyListener = new ActionListener() {
        public void resetGame(){
            renderer.getUpdateLoop().resetGame();
            renderer.moveBall(renderer.ball.state.position);
        }

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("HC Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
            }
            if (name.equals("PS Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.PARTICLE_SWARM));
            }
            if (name.equals("GD Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.GRADIENT_DESCENT));
            }
            if (name.equals("Rule Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.RULE));
            }
            if (name.equals("Manual Input") && !keyPressed) {
                renderer.getUpdateLoop().setBot(null);
                resetGame();
                System.out.println("Manual Input");
            }
            if (name.equals("Reset") && !keyPressed) {
                resetGame();
                System.out.println("Game Reset");
            }
        }
    };

}
