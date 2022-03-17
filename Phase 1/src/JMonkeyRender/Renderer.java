package JMonkeyRender;

import Data_storage.*;
import GUI.MenuGUI;
import Physics.PhysicsEngine;
import Reader.Reader;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.*;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.SimpleWaterProcessor;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Renderer extends Cam {
    TerrainQuad terrainQuad;
    Terrain terrain;
    Geometry target;
    Geometry ballRender;
    float ballRadius = 1f;
    float totalSize = 1024;
    float xoff = 0;
    float yoff = 0;
    Vector2 ballStartPos;
    double targetRadius;
    Vector2 targetPos;
    private PhysicsEngine engine;

    float x=totalSize/2;
    float y=totalSize/2;
    float val = 0;
    int normalFactor = 100;
    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(String texPath){
        this.xoff = (float) (this.ballStartPos.x - this.totalSize/2);
        this.yoff = (float) (this.ballStartPos.y - this.totalSize/2);
        terrain.calculateHeightMap((int) totalSize+1, normalFactor);

        //Setting up the Texture of the ground
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(texPath);

        //Adding grass texture to terrain
        mat1.setTexture("ColorMap",tex);

        //Setting terrain using heightmap
        this.terrainQuad = new TerrainQuad("Course", 65, (int) (totalSize+1), terrain.heightmap);

        terrainQuad.setMaterial(mat1);
        
        rootNode.attachChild(terrainQuad);
    }

    /**
     * Creates golf ball, with textures
     */
    public void InitBall(){
        Sphere ball = new Sphere(120, 120, ballRadius);
        TangentBinormalGenerator.generate(ball);
        this.ballRender = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture("Ball/Golfball.jpeg");
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", sphereTex);
        this.ballRender.setMaterial(mat);
        rootNode.attachChild(this.ballRender);
        moveBall(ballStartPos);
    }

    public void InitTarget(){
        Cylinder tar = new Cylinder(120, 120, (float) targetRadius, 0.1f, true);
        this.target = new Geometry("Target", tar);
        this.target.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI , new Vector3f(0,0,1)));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        this.target.setMaterial(mat);
        float val;
        val = (float) terrain.terrainFunction.valueAt(targetPos.x * terrain.xOff,targetPos.y * terrain.yOff);
        val += Math.abs(terrain.minVal);
        val /= terrain.maxVal - terrain.minVal;
        if (val < 0) {
            val = 0;
        }
        if (val > 1) {
            val = 1;
        }
        val *= normalFactor;
        this.val = val;

        this.target.setLocalTranslation((float) this.targetPos.x, val, (float) this.targetPos.y);

        rootNode.attachChild(target);
    }

    /**
     *checks if final ball position is within the target radius
    */
    public boolean isInTarget(Ball ball){
        if (ball.state.position.x >= targetPos.x - targetRadius && ball.state.position.x <= targetPos.x + targetRadius &&
                ball.state.position.y >= targetPos.y - targetRadius && ball.state.position.y <= targetPos.y + targetRadius){
            inTarget = true;
        }
        else inTarget = false;

        return inTarget;

    }

    public void findTangent(){
        Vector3f terNormal = terrainQuad.getNormal(new Vector2f(getBallX(),getBallY()));
        double scalar = ballRadius/terNormal.length();
        terNormal = terNormal.mult((float) scalar);
        //Just put 0.2 as a threshold, like if the difference is above that, is gonna be visible
        ballRender.move(terNormal.x, terNormal.y, terNormal.z);
    }


    public float getBallX(){
        return this.x;
    }

    public float getBallY(){
        return this.y;
    }

    public float getBallZ(){
        return this.val;
    }

    /**
     * Moves ball according to x & y coordinates
     */
    public void moveBall(Vector2 balPos){
        if(balPos.x<(this.totalSize)/2 && balPos.y < (this.totalSize)/2) {
            this.x = (float) balPos.x;
            this.y = (float) balPos.y;
            //Getting height value corresponding to x and y values
            float val;
            val = (float) terrain.terrainFunction.valueAt( this.x* terrain.xOff, this.y * terrain.yOff);
            val += Math.abs(terrain.minVal);
            val /= terrain.maxVal - terrain.minVal;
            if (val < 0) {
                val = 0;
            }
            if (val > 1) {
                val = 1;
            }
            val *= normalFactor;
            this.val = val;
            //Moving the ball object to specified position
            ballRender.setLocalTranslation((float) (this.x + terrain.xOff), (float) (val + terrain.xOff), (float) (this.y + terrain.xOff));
            findTangent();
            text.setText("x: " + df.format(getBallX()) + "  y: " + df.format(getBallY()) + "  z: "+ df.format(getBallZ()));
        }
    }

    public void InitSky(String path){
        mainScene.attachChild(SkyFactory.createSky(getAssetManager(), path, SkyFactory.EnvMapType.SphereMap));
        rootNode.attachChild(mainScene);
    }

    Node mainScene = new Node("Test");
    public void InitWater(){
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setLightPosition(new Vector3f(0.55f, -0.82f, 0.15f));
        waterProcessor.setReflectionScene(mainScene);

        Vector3f waterLocation=new Vector3f(0,0,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));

        viewPort.addProcessor(waterProcessor);
        Quad waveSize = new Quad(this.totalSize + 200,this.totalSize + 200);

        Geometry water=new Geometry("water", waveSize);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.move(xoff - 100, totalSize/2, -xoff + 100);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    public void initPhysics(){
        this.terrain = Reader.readFile();
        this.ballStartPos = this.terrain.ballStartingPosition;
        this.targetRadius = this.terrain.target.radius;
        this.targetPos = this.terrain.target.position;
        initTerrain(MenuGUI.texPath);

        ball = new Ball(this.ballStartPos, new Vector2(3, -5));
        engine = new PhysicsEngine();
        engine.terrain = this.terrain;
        engine.addBall(ball);
    }

    BitmapText text;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public void InitText(){
        BitmapText hudText = new BitmapText(guiFont);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText("");
        hudText.setLocalTranslation(0, 50, 0); // position
        this.text = hudText;
        guiNode.setQueueBucket(RenderQueue.Bucket.Gui);
        guiNode.attachChild(hudText);

    }

    Ball ball;
    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        initPhysics();
        InitText();

        // builds terrain based on function given
        initTerrain(MenuGUI.getTexPath());
        InitBall();

        InitTarget();
        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ballRender, inputManager);
        InitCam(chaseCam);

        //setting sky background to Sky.jpg
        InitSky("Sky/Skysphere.jpeg");
        InitWater();

    }


    private ArrayList<Vector2> points = new ArrayList<>();
    Boolean inTarget = false;
    @Override
    public void simpleUpdate(float tpf) {
            if (!inTarget) {
                if (points.size() == 0) {
                    points = engine.simulateShot(new Vector2(Math.random() * 10 - 5, Math.random() * 10 - 5), ball);

                }
                if (points.size() != 0) {
                    ball.state.position = points.get(0);
                    moveBall(ball.state.position);
                    points.remove(0);
                }
                isInTarget(ball);
            }else{
                System.exit(1);
            }
    }

    public void start3d(){
        this.setShowSettings(false);
        // Setting up renderer settings, so JME settings tab wouldnt pop out
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Golf Game");
        settings.put("VSync", true);
        settings.put("Samples", 4);
        this.setSettings(settings);

        this.start();
    }
}
