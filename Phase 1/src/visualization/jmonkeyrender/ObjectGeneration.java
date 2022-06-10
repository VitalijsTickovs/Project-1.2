package visualization.jmonkeyrender;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import datastorage.Ball;
import datastorage.BallState;
import datastorage.Terrain;
import utility.math.Vector2;

public class ObjectGeneration {
    private final Renderer renderer;
    private final Terrain terrain;
    private final AssetManager assetManager;
    private final float terScale;
    private final float pixelScale;

    public ObjectGeneration(Renderer renderer) {
        this.renderer = renderer;
        this.terrain = renderer.getGameState().getTerrain();
        this.pixelScale = renderer.getPixelScale();
        this.terScale = renderer.getTerScale();

        this.assetManager = renderer.getAssetManager();
    }

    /**
     * Creates a white target object
     */
    private void initTarget(){
        //Creating cylinder, which would represent target hole
        Cylinder tar = new Cylinder(120, 120, (float) terrain.target.radius*pixelScale, 0.1f, true);
        Geometry target = new Geometry("Target", tar);

        //Rotating the cylinder
        target.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI , new Vector3f(0,0,1)));

        //Making the target hole white color
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        target.setMaterial(mat);

        //Finding the position for the target
        float val = terrain.HeightMapValueAt(terrain.target.position)*terScale;

        //Moving the cylinder to the calculated position
        target.setLocalTranslation((float) (terrain.target.position.x *pixelScale), val, (float) (terrain.target.position.y*pixelScale));

        //Adding it to the scene
        renderer.getRootNode().attachChild(target);
    }

    /**
     * Creates golf ball, with textures
     */
    private void initBall(){
        //Creates Sphere object and adds to Geometry object
        Sphere ball = new Sphere(120, 120, renderer.getBallRadius());
        TangentBinormalGenerator.generate(ball);
        Geometry ballRender = new Geometry("Ball", ball);

        //Adding textures to the ball
        Texture sphereTex = assetManager.loadTexture("Ball/Golfball.jpeg");
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", sphereTex);
        ballRender.setMaterial(mat);

        //add the geometry object to the scene
        renderer.getRootNode().attachChild(ballRender);
        renderer.setBallRender(ballRender);
    }

    /**
     * Generates an arrow Geometry object
     * @param pos - vector, which distance is from ballPos to the mouse coordinates
     * @return Geometry object of an arrow
     */
    public Geometry initArrow(Vector2f pos){
        Arrow arrow = new Arrow(new Vector3f(-pos.x,0,pos.y));
        Geometry arrowRender = new Geometry("Arrow", arrow);

        Vector2 ballPos = renderer.getGameState().getBall().state.position;
        arrowRender.setLocalTranslation((float)ballPos.x*pixelScale,
                terrain.HeightMapValueAt(ballPos)*terScale+1,(float)ballPos.y*pixelScale);

        //Textures for the arrow
        Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setColor("Color", ColorRGBA.Red);
        arrowRender.setMaterial(redMat);

        return arrowRender;
    }

    public void initTarBall(){
        initTarget();
        initBall();
    }
}
