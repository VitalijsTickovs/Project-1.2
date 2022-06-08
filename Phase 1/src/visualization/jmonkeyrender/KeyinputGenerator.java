package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class KeyinputGenerator {
    private Renderer renderer;

    public KeyinputGenerator(Renderer renderer) {
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
        renderer.getInputManager().addListener(actionListener, "PS Bot");
        renderer.getInputManager().addListener(actionListener, "HC Bot");
        renderer.getInputManager().addListener(actionListener, "GD Bot");
        renderer.getInputManager().addListener(actionListener, "Rule Bot");
        renderer.getInputManager().addListener(actionListener, "Manual Input");
        renderer.getInputManager().addListener(actionListener, "Reset");

    }

    private final ActionListener actionListener = new ActionListener() {
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
