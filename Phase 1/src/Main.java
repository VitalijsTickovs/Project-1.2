import Data_storage.Terrain;
import Data_storage.TerrainFunction1;
import Data_storage.Vector2;
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

public class Main extends Cam {

    private boolean inTarget;
    TerrainQuad terrainQuad;
    Terrain terrain;
    final float unitPixelSize = 0.5f;
    String function = "sin(x+y)";
    float totalSize = 1024;
    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(){
        terrain = new Terrain(function,0.2, 0.1, new Vector2(-10,-10), new Vector2(totalSize-10,totalSize-10));
        terrain.calculateHeightMap((int) totalSize, 20);

        //Setting up the Texture of the ground
        Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        Texture grass = assetManager.loadTexture("Terrain/grass.jpeg");

        //Adding grass texture to terrain
        mat1.setTexture("ColorMap",grass);

        //Setting terrain using heightmap
        this.terrainQuad = new TerrainQuad("Course", 65, (int) (totalSize+1), terrain.heightmap);
        float minDim = (float) Math.min(terrain.limitingCorner.x-terrain.startingCorner.x, terrain.limitingCorner.y-terrain.startingCorner.y);
        float xDim = (float) ((terrain.limitingCorner.x-terrain.startingCorner.x)/minDim);
        float yDim = (float) ((terrain.limitingCorner.y-terrain.startingCorner.y)/minDim);

        float xScale = (float) (xDim*(terrain.limitingCorner.x-terrain.startingCorner.x)/512/(terrainQuad.getTerrainSize()/512.0));
        float yScale = (float) (yDim*(terrain.limitingCorner.y-terrain.startingCorner.y)/512/(terrainQuad.getTerrainSize()/512.0));

        terrainQuad.setLocalScale(new Vector3f(xDim, 1.0f, yDim));
        terrainQuad.getTerrainSize();
        terrainQuad.setMaterial(mat1);
        
        rootNode.attachChild(terrainQuad);
    }

    protected Geometry ball;
    float ballRadius = 1f;
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
    }

    Geometry target;
    public void InitTarget(){
        Cylinder tar = new Cylinder(120, 120, 10, 0.1f, true);
        this.target = new Geometry("Target", tar);
        //this.target.rotate(new Quaternion(0.26345053f, 0.6972198f, -0.5893796f, 0.31165418f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        this.target.setMaterial(mat);
        this.target.move(0, 0, 0);

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

        Vector3f normal = this.terrainQuad.getNormal(new Vector2f(x,y));

        float XDifference=BallatX-(float)normal.x;
        float YDifference=BallatY-(float)normal.z;
        float ZDifference=BallatZ-(float)normal.y;

        //Just put 0.2 as a threshold, like if the difference is above that, is gonna be visible
        if(XDifference<this.ballRadius) {
            ball.move(this.ballRadius - XDifference + this.ballRadius ,0,0);
        }

        if(YDifference<this.ballRadius){
            ball.move(0,0,this.ballRadius - YDifference + this.ballRadius);
        }
        if(ZDifference <this.ballRadius){
            ball.move(0, this.ballRadius - ZDifference + this.ballRadius, 0);
        }
    }

    /**
     * Moves ball according to x & y coordinates
     */
    float x=512;
    float y=512;
    float val = 0;

    public float getBallX(){
        return this.x;
    }

    public float getBallY(){
        return this.y;
    }

    public float getBallZ(){
        return this.val;
    }
    TerrainFunction1 funct = new TerrainFunction1(function);
    public void moveBall(float x, float y){
        this.x = x;
        this.y = y;
        //Getting height value corresponding to x and y values
        float val;
        float minVal = -10;
        float maxVal = 10;
        val = (float) funct.valueAt(x, y);
        if (val > maxVal) {
            maxVal = val;
        }
        if (val < minVal) {
            minVal = val;
        }

        val += Math.abs(minVal);
        val /= maxVal - minVal;
        if (val < 0) {
            val = 0;
        }
        if (val > 1) {
            val = 1;
        }
        val *= 20;
        this.val = val;
        this.val = terrain.heightmap[Math.round(x+y)];
        //Moving the ball object to specified position
        ball.setLocalTranslation(y - this.totalSize/2,val,x - this.totalSize/2);
        findTangent();
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
        Quad waveSize = new Quad(1024 +200,1024 +200);
        waveSize.scaleTextureCoordinates(new Vector2f(6f,6f));

        Geometry water=new Geometry("water", waveSize);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-200, -1, 250);
        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }

    Reader reader = new Reader();

    @Override
    public void simpleInitApp() {
        // builds terrain based on function given
        initTerrain();
        // generates a ball into the world
        InitBall();
        //InitTarget();
        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ball, inputManager);
        InitCam(chaseCam);
        //flyCam.setMoveSpeed(100);

        //setting sky background to Sky.jpg
        InitSky("Sky/Skysphere.jpeg");
        //InitWater();

        // reading from input file and assigning ball x and y positions
        float x = Float.parseFloat(String.valueOf(reader.getBallX()));
        float y = Float.parseFloat(String.valueOf(reader.getBallY()));
        //moving the ball according to input file
    }


    float movex = 0;
    @Override
    public void simpleUpdate(float tpf) {
        moveBall(movex,movex);
        movex+=0.1f;
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
