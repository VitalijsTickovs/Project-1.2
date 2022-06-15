package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Ray;
import gui.shotinput.MouseInputReader;
import gui.shotinput.ShotInputWindow;
import utility.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InputsGenerator {
    private final Renderer renderer;
    private boolean isMouseInput= true;
    private boolean isTerrainEditor = false;
    private String obstacleType = "Box";
    private TerrainEditor terrainEditor;
    private ArrayList<CollisionResults> collisions = new ArrayList<>();

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
        renderer.getInputManager().addMapping("Box", new KeyTrigger(KeyInput.KEY_1));
        renderer.getInputManager().addMapping("Tree", new KeyTrigger(KeyInput.KEY_2));

        //Setting listeners for inputs
        renderer.getInputManager().addListener(keyListener, "PS Bot","HC Bot","GD Bot","Rule Bot","Manual Input","Reset", "Change Input Type", "Terrain Editor", "Box", "Tree");
    }
    private final ActionListener keyListener = new ActionListener() {
        public void resetGame(){
            renderer.getUpdateLoop().resetGame();
            renderer.moveBall(renderer.ball.state.position);
        }

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("HC Bot") && !keyPressed && !isTerrainEditor) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("PS Bot") && !keyPressed && !isTerrainEditor) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.PARTICLE_SWARM));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("GD Bot") && !keyPressed && !isTerrainEditor) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.GRADIENT_DESCENT));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("Rule Bot") && !keyPressed && !isTerrainEditor) {
                renderer.getUpdateLoop().setBot(BotFactory.getBot(BotFactory.BotImplementations.RULE));
                renderer.getUpdateLoop().setManualInputType(null);
            }
            if (name.equals("Manual Input") && !keyPressed && !isTerrainEditor) {
                renderer.getUpdateLoop().setBot(null);
                renderer.getUpdateLoop().setManualInputType(new MouseInputReader(renderer));
                resetGame();
                System.out.println("Manual Input");
            }
            if(name.equals("Change Input Type") && !keyPressed && !isTerrainEditor){
                if(isMouseInput){
                    renderer.getUpdateLoop().setManualInputType(new ShotInputWindow(renderer));
                }else{
                    renderer.getUpdateLoop().getBallVelocityInput().hideInputWindow();
                    renderer.getUpdateLoop().setManualInputType(new MouseInputReader(renderer));
                }
                isMouseInput = !isMouseInput;
                System.out.println("Changed Input");
            }
            if (name.equals("Reset") && !keyPressed && !isTerrainEditor) {
                resetGame();
                System.out.println("Game Reset");
            }
            if(name.equals("Terrain Editor")&& !keyPressed){
                if(!isTerrainEditor){
                    isMouseInput = false;
                    System.out.println("Terrain editor mode on");

                    terrainEditor = new TerrainEditor(renderer);

                }else{
                    System.out.println("Terrain editor mode off");
                    isMouseInput = true;

                    terrainEditor.switchCamera();
                }
                isTerrainEditor = !isTerrainEditor;
            }
            if(name.equals("Box") && !keyPressed){
                obstacleType = name;
            }
            if(name.equals("Tree") && !keyPressed){
                obstacleType = name;
            }
        }
    };

    protected void mouseInput(){
        renderer.getInputManager().addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        renderer.getInputManager().addMapping("Remove Object" , new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        renderer.getInputManager().addListener(mouseListener, "Left Click", "Remove Object");

        renderer.getUpdateLoop().setManualInputType(new MouseInputReader(renderer));
    }

    private final ActionListener mouseListener = new ActionListener() {
        private CollisionResults getCollisions(String collision){
            CollisionResults collisionResults = new CollisionResults();
            Ray ray = new Ray(renderer.getCamera().getLocation(), renderer.getCamera().getDirection());
            renderer.getRootNode().getChild(collision).collideWith(ray,collisionResults);
            return collisionResults;
        }

        @Override
        public void onAction(String name, boolean clicked, float v) {
            if(name.equals("Left Click") && !clicked && isMouseInput){
                renderer.getUpdateLoop().setShotForce(new Vector2(-renderer.getShotInput().getX()/15*5,renderer.getShotInput().getY()/15*5));
            }
            if(name.equals("Left Click") && !clicked && isTerrainEditor){
                collisions.add(getCollisions("Course"));
                renderer.drawPoint(collisions.get(collisions.size() - 1).getCollision(0).getContactPoint());

                //The box would be made if the size is bigger than 2
                if (collisions.size() == 2) {
                    collisions = new ArrayList<>();
                    renderer.drawObstacle(obstacleType);
                    renderer.clearPoint();
                }
            }
            if(name.equals("Remove Object") && !clicked && isTerrainEditor){
                CollisionResults collisionResults = getCollisions("Obstacles");
                if(collisionResults.size()>0) renderer.removeObject(collisionResults.getCollision(0).getGeometry());
            }
        }
    };
}
