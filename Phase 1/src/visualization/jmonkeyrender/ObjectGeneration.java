package visualization.jmonkeyrender;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import datastorage.GameState;
import datastorage.Terrain;
import datastorage.obstacles.IObstacle;
import datastorage.obstacles.ObstacleBox;
import datastorage.obstacles.ObstacleTree;
import utility.math.Vector2;

import java.util.ArrayList;

public class ObjectGeneration {
    private final Renderer renderer;
    private final GameState gameState;
    private final Terrain terrain;
    private final AssetManager assetManager;
    private final float terScale;
    private final float pixelScale;

    public ObjectGeneration(Renderer renderer) {
        this.renderer = renderer;
        this.gameState = renderer.getGameState();
        this.terrain = gameState.getTerrain();
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

        renderer.getRootNode().attachChild(target);
    }



    /**
     * Creates golf ball, with textures
     */
    private void initBall(){
        //Creates Sphere object and adds to Geometry object
        Sphere ball = new Sphere(120, 120, Renderer.ballRadius);
        TangentBinormalGenerator.generate(ball);
        Geometry ballRender = new Geometry("Ball", ball);

        //Adding textures to the ball
        Texture sphereTex = assetManager.loadTexture("Ball/Golfball.jpeg");
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", sphereTex);
        ballRender.setMaterial(mat);

        //add the geometry object to the scene
        renderer.setBallRender(ballRender);
        renderer.getRootNode().attachChild(ballRender);
    }

    /**
     * Generates an arrow Geometry object
     * @param pos - vector, which distance is from ballPos to the mouse coordinates
     * @return Geometry object of an arrow
     */
    public Geometry initArrow(Vector2f pos){
        Arrow arrow = new Arrow(new Vector3f(-pos.x,0,pos.y));
        Geometry arrowRender = new Geometry("Arrow", arrow);

        Vector2 ballPos = gameState.getBall().state.position;
        arrowRender.setLocalTranslation((float)ballPos.x*pixelScale,
                terrain.HeightMapValueAt(ballPos)*terScale+2.56f,(float)ballPos.y*pixelScale);

        //Textures for the arrow
        Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setColor("Color", ColorRGBA.Red);
        arrowRender.setMaterial(redMat);

        return arrowRender;
    }

    private void initObstacles(){
        ArrayList<IObstacle> obstacles = terrain.obstacles;
        for(IObstacle obstacle: obstacles){
            if(obstacle instanceof ObstacleBox){
                float startY = terrain.HeightMapValueAt(((ObstacleBox) obstacle).bottomLeftCorner)*terScale;

                Vector3f start = new Vector3f((float)((ObstacleBox) obstacle).bottomLeftCorner.x*renderer.getPixelScale(), startY,
                        (float)((ObstacleBox) obstacle).bottomLeftCorner.y*renderer.getPixelScale());


                float endY = terrain.HeightMapValueAt(((ObstacleBox) obstacle).topRightCorner)*terScale;

                Vector3f end = new Vector3f((float)(((ObstacleBox) obstacle).topRightCorner.x)*renderer.getPixelScale(),endY,
                        (float)(((ObstacleBox) obstacle).topRightCorner.y)* renderer.getPixelScale());
                renderer.obstacles.attachChild(drawObstacle("Box", start,end));
            } else if(obstacle instanceof ObstacleTree){
                float startY = terrain.HeightMapValueAt(((ObstacleTree) obstacle).originPosition);
                Vector3f start = new Vector3f((float)((ObstacleTree) obstacle).originPosition.x* renderer.getPixelScale(), startY,
                        (float)((ObstacleTree) obstacle).originPosition.y* renderer.getPixelScale());
                drawObstacle("Tree", start,null);
            }
        }
    }

    /**
     * Generates ball and target geometries for the terrain
     */
    public void initObjects(){
        initBall();
        initTarget();
        initObstacles();
    }

    public Spatial drawObstacle(String obstacleType, Vector3f start, Vector3f end) {
        start.y+=0.5f;
        Spatial obstacle = new Geometry();
        if(obstacleType.equals("Box")){
            end.y+=5;
            Box box = new Box(start, end);
            obstacle = new Geometry("Obstacle", box);

            Texture crateTex = assetManager.loadTexture("ObjectTexture/Crate.png");
            Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            redMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);

            redMat.setTexture("ColorMap", crateTex);
            obstacle.setMaterial(redMat);
            //Adding the obstacle to the physics, if the its added through terrain editor
            if(Inputs.isTerrainEditor) {
                Vector2 vector1 = new Vector2(start.x, start.z);
                Vector2 vector2 = new Vector2(end.x, end.z);
                vector2.scale(1/renderer.getPixelScale());
                vector1.scale(1/renderer.getPixelScale());

                terrain.addObstacle(new ObstacleBox(vector1, vector2));
            }
        }
        if(obstacleType.equals("Tree")){
            obstacle = assetManager.loadModel("ObjectTexture/Tree.j3o");
            Texture crateTex = assetManager.loadTexture("ObjectTexture/Leaves.png");
            Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            //redMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            redMat.setTexture("ColorMap", crateTex);

            obstacle.setMaterial(redMat);
            obstacle.scale(10);
            obstacle.setLocalTranslation(start);
            if(Inputs.isTerrainEditor) {
                double scale = 8.7;
                Vector2 vector1 = new Vector2(start.x / scale,
                        start.z / scale);
                ObstacleTree tree = new ObstacleTree();
                tree.originPosition = vector1;
                tree.bounciness = 0.75;
                tree.radius = 1;
                terrain.addObstacle(tree);
            }
        }
        return obstacle;
    }
}
