import Data_storage.TerrainFunction1;
import Reader.Reader;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends Cam {

    float[] HeightMap;

    TerrainQuad terrain;
    TerrainFunction1 generator;

    public void initTerrain(){
        generator = new TerrainFunction1("sin((x+y)/7)");
        this.HeightMap = generator.getHeightMap(64, 64, 20);

        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        Texture grass = assetManager.loadTexture("Terrain/grass.jpeg");
        mat1.setTexture("ColorMap",grass);

        this.terrain = new TerrainQuad("Course", 65, 65, this.HeightMap);
        terrain.setMaterial(mat1);
        rootNode.attachChild(terrain);
    }


    protected Geometry player;
    float x, y, val;
    public void InitBall(){
        Sphere ball = new Sphere(120, 120, 1f);
        TangentBinormalGenerator.generate(ball);
        player = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture(
                "Ball/Golfball.jpeg");

        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        mat.setTexture("ColorMap", sphereTex);
        //mat.setColor("Color", ColorRGBA.Red);
        //mat.setBoolean("UseMaterialColors", true);
        //mat.setColor("Diffuse",ColorRGBA.White);
        //mat.setColor("Specular",ColorRGBA.White);
        //mat.setFloat("Shininess", 64f);
        player.setMaterial(mat);
        rootNode.attachChild(player);
    }

    public void moveBall(float x, float y){
        this.x = x;
        this.y = y;

        this.val = this.HeightMap[ ((Math.round(x) + Math.round(y)*10))];

        this.val = (float) generator.valueAt( this.x, this.y);

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
        val*=10;

        player.move(this.x, this.val+1, this.y);

    }

    public void InitSky(){
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Sky/Sky.jpg", SkyFactory.EnvMapType.SphereMap));
    }

    @Override
    public void simpleInitApp() {

        initTerrain();
        InitBall();
        ChaseCamera chaseCam = new ChaseCamera(cam, player, inputManager);
        InitCam(chaseCam);

        InitSky();

        Reader.main();
        float x = Float.parseFloat(String.valueOf(Reader.terrainX0));
        float y = Float.parseFloat(String.valueOf(Reader.terrainY0));
        //moveBall(x,y);

        moveBall(0, 1);
    }


    @Override
    public void simpleUpdate(float tpf) {
    }

    public static void main(String[] args){
        Main game = new Main();
        game.setShowSettings(false);

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