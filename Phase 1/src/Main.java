import Data_storage.Terrain;
import Data_storage.Vector2;
import Reader.Reader;
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

public class Main extends Cam {

    TerrainQuad terrainQuad;
    Terrain terrain;
    Geometry target;
    Geometry ball;
    float ballRadius = 1f;

    final float unitPixelSize = 0.5f;
    String function = "sin(x) + sin(y)";
    float totalSize = 1024;
    float xoff = 0;
    float yoff = 0;
    double ballStartx;
    double ballStarty;
    double targetRadius;
    Vector2 targetPos;

    float x=totalSize/2;
    float y=totalSize/2;
    float val = 0;
    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(){

        this.xoff = (float) (this.ballStartx - this.totalSize/2);
        this.yoff = (float) (this.ballStarty - this.totalSize/2);
        terrain = new Terrain(function,0.2, 0.1, new Vector2(xoff,yoff), new Vector2(-xoff,-yoff));
        terrain.calculateHeightMap((int) totalSize+1, 20);

        //Setting up the Texture of the ground
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Texture grass = assetManager.loadTexture("Terrain/grass.jpeg");

        //Adding grass texture to terrain
        mat1.setTexture("ColorMap",grass);

        //Setting terrain using heightmap
        this.terrainQuad = new TerrainQuad("Course", 65, (int) (totalSize+1), terrain.heightmap);
        this.terrainQuad.setMaterial(mat1);

        rootNode.attachChild(this.terrainQuad);
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
        Cylinder tar = new Cylinder(120, 120, (float) this.targetRadius, 0.1f, true);
        this.target = new Geometry("Target", tar);

        Quaternion roll180 = new Quaternion();
        roll180.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
        target.setLocalRotation(roll180);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        this.target.setMaterial(mat);

        float val;
        val = (float) terrain.terrainFunction.valueAt(this.xoff + this.targetPos.x, this.yoff + this.targetPos.y);
        if(val > terrain.maxVal) val = terrain.maxVal;
        else if(val < terrain.minVal) val = terrain.minVal;
        this.val = val;

        this.target.setLocalTranslation((float) this.targetPos.x, 0, (float) this.targetPos.y);

        rootNode.attachChild(target);
    }

    public void findTangent(){
        double XPosition;
        double YPosition;
        double ZPosition;
        float BallatX;
        float BallatY;
        float BallatZ;

        XPosition=terrain.terrainFunction.xDerivativeAt(x, y);
        YPosition=terrain.terrainFunction.yDerivativeAt(x, y);
        ZPosition=terrain.terrainFunction.valueAt(x,y); //height
        BallatX=getBallX();
        BallatY=getBallY();
        BallatZ=getBallZ();

        Vector3f normal = this.terrainQuad.getNormal(new Vector2f(BallatX,BallatY));

        float XDifference=BallatX-normal.x;
        float YDifference=BallatY-normal.z;
        float ZDifference=BallatZ-normal.y;

        System.out.println(XDifference);
        //Just put 0.2 as a threshold, like if the difference is above that, is gonna be visible
        if(XDifference<this.ballRadius) {
            ball.move(this.ballRadius - XDifference ,0,0);
        }else if(XDifference>this.ballRadius){
            ball.move(new Vector3f(XDifference + ballRadius, 0,0));
        }

        if(YDifference<this.ballRadius){
            ball.move(0,0,this.ballRadius - YDifference );
        }else if(XDifference>this.ballRadius){
            ball.move(new Vector3f(0, 0,YDifference + ballRadius));
        }
        if(ZDifference <this.ballRadius){
            ball.move(0, this.ballRadius - ZDifference, 0);
        }else if(XDifference>this.ballRadius){
            ball.move(new Vector3f(0, YDifference + ballRadius,0));
        }
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
        if(x< totalSize || y < totalSize) {

            this.x = x;
            this.y = y;
            //Getting height value corresponding to x and y values
            float val;
            val = (float) terrain.terrainFunction.valueAt(this.xoff + x, this.yoff + y);
            if(val > terrain.maxVal) val = terrain.maxVal;
            else if(val < terrain.minVal) val = terrain.minVal;
            this.val = val;
            //Moving the ball object to specified position
            ball.setLocalTranslation(x, val+ballRadius, y);
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
//        waveSize.scaleTextureCoordinates(new Vector2f(6f,6f));

        Geometry water=new Geometry("water", waveSize);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.move(xoff - 100, -1, -xoff + 100);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    @Override
    public void simpleInitApp() {
        this.terrain = Reader.readFile("userInput.csv");
        this.ballStartx = this.terrain.ballStartingPosition.x;
        this.ballStarty = this.terrain.ballStartingPosition.y;
        this.targetRadius = this.terrain.target.radius;
        this.targetPos = this.terrain.target.position;
        // builds terrain based on function given
        initTerrain();
        // generates a ball into the world
        InitBall();
        InitTarget();
        //creating and attaching camera to ball
        //ChaseCamera chaseCam = new ChaseCamera(cam, ball, inputManager);
        //InitCam(chaseCam);
        flyCam.setMoveSpeed(100);

        //setting sky background to Sky.jpg
        InitSky("Sky/Skysphere.jpeg");
        InitWater();

    }


    @Override
    public void simpleUpdate(float tpf) {
    }

    public static void main(String[] args) {
        Main game = new Main();
        game.setShowSettings(false);
        // Setting up renderer settings, so JME settings tab wouldnt pop out
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Golf Game");
        settings.put("VSync", true);
        settings.put("Samples", 4);
        game.setSettings(settings);

        game.start();
    }
}