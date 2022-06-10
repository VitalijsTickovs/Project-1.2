package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import gui.shotinput.MouseInputReader;
import gui.shotinput.ShotInputWindow;
import utility.math.Vector2;

public class InputsGenerator {
    private final Renderer renderer;
    private boolean isMouseInput= true;
    private boolean isTerrainEditor = false;
    private TerrainEditor terrainEditor;

    public InputsGenerator(Renderer renderer) {
        this.renderer = renderer;
    }

    public boolean isMouseInput() {
        return isMouseInput;
    }

    public boolean isTerrainEditor() {
        return isTerrainEditor;
    }

    protected void initKeys() {
        //Mapping each bot implementation to keys
        renderer.getInputManager().addMapping("HC Bot",  new KeyTrigger(KeyInput.KEY_H));
        renderer.getInputManager().addMapping("PS Bot",   new KeyTrigger(KeyInput.KEY_P));
        renderer.getInputManager().addMapping("GD Bot",  new KeyTrigger(KeyInput.KEY_G));
        renderer.getInputManager().addMapping("Rule Bot", new KeyTrigger(KeyInput.KEY_B));
        renderer.getInputManager().addMapping("Manual Input", new KeyTrigger(KeyInput.KEY_L));
        renderer.getInputManager().addMapping("Change Input Type", new KeyTrigger(KeyInput.KEY_K));
        renderer.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        renderer.getInputManager().addMapping("Terrain Editor", new KeyTrigger(KeyInput.KEY_Z));
        //Setting listeners for inputs
        renderer.getInputManager().addListener(keyListener, "PS Bot","HC Bot","GD Bot","Rule Bot","Manual Input","Reset", "Change Input Type", "Terrain Editor");
    }
    private final ActionListener keyListener = new ActionListener() {
        public void resetGame(){
            renderer.getUpdateLoop().resetGame();
            renderer.moveBall(renderer.ball.state.position);
        }

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("HC Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("PS Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.PARTICLE_SWARM));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("GD Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.GRADIENT_DESCENT));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("Rule Bot") && !keyPressed) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.RULE));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("Manual Input") && !keyPressed) {
                renderer.getUpdateLoop().setBot(null);
                resetGame();
                System.out.println("Manual Input");
            }
            if(name.equals("Change Input Type") && !keyPressed){
                if(isMouseInput){
                    renderer.getUpdateLoop().setManualInputType(new ShotInputWindow(renderer));
                }else{
                    renderer.getUpdateLoop().getBallVelocityInput().hideInputWindow();
                    renderer.getUpdateLoop().setManualInputType(new MouseInputReader(renderer));
                }
                isMouseInput = !isMouseInput;
                System.out.println("Changed Input");
            }
            if (name.equals("Reset") && !keyPressed) {
                resetGame();
                System.out.println("Game Reset");
            }
            if(name.equals("Terrain Editor")&& !keyPressed){
                if(!isTerrainEditor){
                    isMouseInput = false;
                    System.out.println("Terrain editor mode on");

                    renderer.getInputManager().removeListener(mouseListener);
                    renderer.getInputManager().deleteMapping("Left Click");
                    terrainEditor = new TerrainEditor(renderer);
                }else{
                    System.out.println("Terrain editor mode off");
                    isMouseInput = true;

                    renderer.getInputManager().addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
                    renderer.getInputManager().addListener(mouseListener, "Left Click");
                    terrainEditor.switchCamera();
                }
                isTerrainEditor = !isTerrainEditor;
            }
        }
    };

    protected void initMouse(){
        renderer.getInputManager().addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        renderer.getInputManager().addListener(mouseListener, "Left Click");

        renderer.getUpdateLoop().setManualInputType(new MouseInputReader(renderer));
    }

    private final ActionListener mouseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean clicked, float v) {

            if(name.equals("Left Click") && !clicked){
                renderer.getUpdateLoop().setShotForce(new Vector2(-renderer.getShotInput().getX()/15*5,renderer.getShotInput().getY()/15*5));
            }
        }
    };
}
