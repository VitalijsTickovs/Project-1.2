import Data_storage.Terrain;
import Data_storage.TerrainFunction1;
import Data_storage.Vector2;
import Reader.Reader;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.SimpleWaterProcessor;

public class Main extends Cam {
    TerrainQuad terrainQuad;
    Terrain terrain;
    final float unitPixelSize = 0.5f;

    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(){
        terrain = new Terrain("sin(x+y)",0.2, 0.1, new Vector2(0,0), new Vector2(1024,1024));
        terrain.calculateHeightMap(1024, 20);

        //Setting up the Texture of the ground
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture grass = assetManager.loadTexture("Terrain/grass.jpeg");

        mat1.setTexture("ColorMap",grass);

        this.terrainQuad = new TerrainQuad("Course", 65, 513, terrain.heightmap);
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

    public void newTerrain(){
        if(this.x > 50 || this.y > 50){
        }
    }


    protected Geometry ball;
    float x, y, val;

    /**
     * Creates golf ball, with textures
     */
    public void InitBall(){
        Sphere ball = new Sphere(120, 120, 1f);
        TangentBinormalGenerator.generate(ball);
        this.ball = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture(
                "Ball/Golfball.jpeg");

        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", sphereTex);
        //mat.setColor("Color", ColorRGBA.Red);
        //mat.setBoolean("UseMaterialColors", true);
        //mat.setColor("Diffuse",ColorRGBA.White);
        //mat.setColor("Specular",ColorRGBA.White);
        //mat.setFloat("Shininess", 64f);
        this.ball.setMaterial(mat);
        rootNode.attachChild(this.ball);
    }

    Geometry target;
    public void InitTarget(){
        Cylinder tar = new Cylinder(120, 120, 10, 2, true);
        this.target = new Geometry("Target", tar);
        this.target.rotate(48,0,0);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        this.target.setMaterial(mat);
        this.target.move(0, 20, 0);

        rootNode.attachChild(target);
    }

    /**
     * Moves ball according to x & y coordinates
     */
    TerrainFunction1 funct = new TerrainFunction1("sin(x+y)");
    public void moveBall(float x, float y){
        this.x = x;
        this.y = y;

        this.val = this.terrain.heightmap[ ((Math.round(x) + Math.round(y)*10))];

        this.val = (float) funct.valueAt( this.x, this.y);

        // Ensure min is 0
        val += Math.abs(-10);
        // Normalize values
        val /= 10-(-10);
        // Clamp out of range
        if (val > 1) {
            val = 1;
        }
        if (val < 0) {
            val = 0;
        }
        val*=20;

        ball.move(this.x, this.val+1, this.y);

    }

    public void InitSky(){
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Sky/Sky.jpg", SkyFactory.EnvMapType.SphereMap));
    }

    SimpleWaterProcessor waterProcessor;
    Spatial waterPlane;
    Node sceneNode;
    public void InitWater(){
        waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneNode);
        waterProcessor.setDebug(true);
        viewPort.addProcessor(waterProcessor);

        waterProcessor.setLightPosition(new Vector3f(0, 0, 0));

        //create water quad
        waterPlane = waterProcessor.createWaterGeometry(100, 100);
        waterPlane=  assetManager.loadModel("assets/WaterTest.mesh.xml");
        waterPlane.setMaterial(waterProcessor.getMaterial());
        waterPlane.setLocalScale(40);
        waterPlane.setLocalTranslation(-5, 0, 5);

        rootNode.attachChild(waterPlane);
    }

    Reader reader = new Reader();
    @Override
    public void simpleInitApp() {
        //builds terrain based on function given
        initTerrain();
        //generates a ball into the world
        InitBall();
        InitTarget();
        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ball, inputManager);
        InitCam(chaseCam);
        //flyCam.setMoveSpeed(100);
        //setting sky background to Sky.jpg
        InitSky();

        //reading from input file and assigning ball x and y positions
        float x = Float.parseFloat(String.valueOf(reader.getBallX()));
        float y = Float.parseFloat(String.valueOf(reader.getBallY()));
        //moving the ball according to input file
        //moveBall(x,y);
        moveBall(0,0);
    }


    @Override
    public void simpleUpdate(float tpf) {
    }

    public static void main(String[] args){
        Main game = new Main();
        game.setShowSettings(false);
        //Setting up renderer settings, so JME settings tab wouldnt pop out
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Golf Game");
        settings.put("VSync", true);
        //Anti-Aliasing
        settings.put("Samples", 4);
        game.setSettings(settings);

        game.start();
    }
}