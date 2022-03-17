package JMonkeyRender;

import Data_storage.*;
import Reader.Reader;
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

public class Renderer extends Cam {
    private boolean inTarget;
    TerrainQuad terrainQuad;
    Terrain terrain;
    Geometry target;
    Geometry ball;
    float ballRadius = 1f;
    final float unitPixelSize = 0.5f;
    float totalSize = 512;
    float xoff = 0;
    float yoff = 0;
    double ballStartx;
    double ballStarty;
    double targetRadius;
    Vector2 targetPos;

    float x=totalSize/2;
    float y=totalSize/2;
    float val = 0;
    int normalFactor = 25;
    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(){
        this.xoff = (float) (this.ballStartx - this.totalSize/2);
        this.yoff = (float) (this.ballStarty - this.totalSize/2);
        terrain.calculateHeightMap((int) totalSize+1, normalFactor);

        //Setting up the Texture of the ground
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Texture grass = assetManager.loadTexture("Terrain/grass.jpeg");

        //Adding grass texture to terrain
        mat1.setTexture("ColorMap",grass);

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
        this.ball = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture("Ball/Golfball.jpeg");
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", sphereTex);
        this.ball.setMaterial(mat);
        rootNode.attachChild(this.ball);
        moveBall((float) this.ballStartx, (float) this.ballStarty);
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
    public boolean isInTarget(Ball ball, Target t){

        if (ball.state.position.x >= t.position.x - t.radius && ball.state.position.x <= t.position.x + t.radius && ball.state.position.y >= t.position.y - t.radius && ball.state.position.y <= t.position.y + t.radius){
            inTarget = true;
        }
        else inTarget = false;

        return inTarget;

    }

    public void findTangent(){
        double NormaltoX;
        //XPosition=terrain.terrainFunction.xDerivativeAt(x, y);

        NormaltoX=-1/terrain.terrainFunction.xDerivativeAt(x, y);
        double normaltoy= -1/terrain.terrainFunction.yDerivativeAt(x,y);
        Vector3f terNormal = terrainQuad.getNormal(new Vector2f(getBallX(),getBallY()));
        double scalar = ballRadius/terNormal.length();
        terNormal = terNormal.mult((float) scalar);
        //Just put 0.2 as a threshold, like if the difference is above that, is gonna be visible
        ball.move(terNormal.x, terNormal.y, terNormal.z);
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
    public void moveBall(float x, float y){
        if(x<(this.totalSize)/2 && y < (this.totalSize)/2) {
            this.x = x;
            this.y = y;
            //Getting height value corresponding to x and y values
            float val;
            val = (float) terrain.terrainFunction.valueAt( x* terrain.xOff, y * terrain.yOff);
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
            ball.setLocalTranslation((float) (x + terrain.xOff), (float) (val + terrain.xOff), (float) (y + terrain.xOff));
            findTangent();
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
        water.move(xoff - 100, normalFactor/2, -xoff + 100);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    @Override
    public void simpleInitApp() {
        this.terrain = Reader.readFile();
        this.ballStartx = this.terrain.ballStartingPosition.x;
        this.ballStarty = this.terrain.ballStartingPosition.y;
        this.targetRadius = this.terrain.target.radius;
        this.targetPos = this.terrain.target.position;
        // builds terrain based on function given
        initTerrain();
        //System.out.println(terrain.heightmap);
        // generates a ball into the world
        InitBall();
        InitTarget();
        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ball, inputManager);
        InitCam(chaseCam);
        //flyCam.setMoveSpeed(100);

        //setting sky background to Sky.jpg
        InitSky("Sky/Skysphere.jpeg");
        InitWater();

    }

    float f;
    @Override
    public void simpleUpdate(float tpf) {
//        moveBall(f,f);
//        f+=0.01f;
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
