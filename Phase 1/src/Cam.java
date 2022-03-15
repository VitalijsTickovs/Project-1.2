import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Cam extends SimpleApplication{

    //private ChaseCamera chaseCam;
    //private Spatial player;
    //private Node rootNode;
    /*private final InputManager inputManager;
    private final AssetManager assetManager;
    private final Camera cam;*/
    private Vector3f flyDirection = Vector3f.ZERO;

    /*public Cam (SimpleApplication app){
        // player = Main.player;
        rootNode = app.getRootNode();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        cam = app.getCamera();
    }*/

    public void InitCam(ChaseCamera chaseCam) {
        flyCam.setEnabled(false);               // disable the default first-person camera
        chaseCam.setSmoothMotion(true);
        chaseCam.setDefaultDistance(40f);       // default distance to the target (ball)
        chaseCam.setChasingSensitivity(5f);     // the lower the sensitivity the slower the camera will follow the target when it moves
        chaseCam.setRotationSensitivity(5f);
        chaseCam.setMaxDistance(50f);
        chaseCam.setMinDistance(30f);
        //chaseCam.setDefaultVerticalRotation(-FastMath.PI/2);
        //chaseCam.setDefaultHorizontalRotation(-FastMath.PI/2);

    }

    @Override
    public void simpleInitApp() {

    }
}
