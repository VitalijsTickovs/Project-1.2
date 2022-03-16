import Data_storage.TerrainFunction1;
import Data_storage.Terrain;
import Data_storage.Vector2;
import Reader.Reader;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends Cam {

    TerrainQuad terrainQuad;
    Terrain terrain;
    final float unitPixelSize = 0.5f;

    /**
     * Initializes area terrain based on the function given in input file
     */
    public void initTerrain(){
        terrain = new Terrain("sin(x+y)", 0.1, 0.1, new Vector2(-10, -10), new Vector2(10, 10));
        terrain.calculateHeightMap(512, 20.0);
        //this.HeightMap = generator.getHeightMap(128, 128, 50);

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

        /*for(int i=0;i<terrainQuad.getChildren().size();i++) {
            for(int j=0;j<((TerrainQuad)terrainQuad.getChild(i)).getChildren().size();j++) {
                for(int k=0;k<((TerrainQuad)((TerrainQuad)terrainQuad.getChild(i)).getChild(j)).getChildren().size();k++) {
                    ((TerrainPatch)((TerrainQuad)((TerrainQuad)terrainQuad.getChild(i)).getChild(j)).getChild(k))
                            .getMesh().scaleTextureCoordinates(new Vector2f(xDim,yDim));
                }
            }
        }*/
        
        rootNode.attachChild(terrainQuad);
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

    /**
     * Moves ball according to x & y coordinates
     */
    public void moveBall(float x, float y){
        this.x = x;
        this.y = y;

        this.val = terrain.heightmap[ ((Math.round(x) + Math.round(y)*10))];

        this.val = (float) terrain.terrainFunction.valueAt( this.x, this.y);

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
        val*=50;

        ball.move(this.x, this.val+1, this.y);

    }

    public void InitSky(){
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Sky/Sky.jpg", SkyFactory.EnvMapType.SphereMap));
    }

    //Reader reader = new Reader();
    @Override
    public void simpleInitApp() {
        //builds terrain based on function given
        initTerrain();
        //generates a ball into the world
        InitBall();
        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ball, inputManager);
        InitCam(chaseCam);
        //setting sky background to Sky.jpg
        InitSky();

        //reading from input file and assigning ball x and y positions
        //Reader.main();
        float x = 0;//Float.parseFloat(String.valueOf(reader.getBallX()));
        float y = 0;//Float.parseFloat(String.valueOf(reader.getBallY()));
        //moving the ball according to input file
        moveBall(x,y);
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