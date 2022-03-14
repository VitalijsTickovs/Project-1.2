import Data_storage.TerrainFunction1;
import Reader.Reader;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends SimpleApplication {

    float[] HeightMap;

    public void initTerrain(){
        TerrainFunction1 generator = new TerrainFunction1("sin(x + y)");
        this.HeightMap = generator.getHeightMap(128, 128, 50);

        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat1.setColor("Color", ColorRGBA.Blue);

        TerrainQuad terrain = new TerrainQuad("Course", 65, 129, this.HeightMap);
        terrain.setMaterial(mat1);
        terrain.move(0,-10,0);
        rootNode.attachChild(terrain);
    }


    protected Geometry player;
    float x, y, z;
    public void InitBall(){
        Sphere ball = new Sphere(200, 120, 0.1f);
        TangentBinormalGenerator.generate(ball);
        player = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture(
                "Ball/Golfball.jpeg");

        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");

        mat.setTexture("NormalMap", sphereTex);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse",ColorRGBA.White);
        mat.setColor("Specular",ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        player.setMaterial(mat);
        rootNode.attachChild(player);
    }

    public void moveBall(Double x, Double y){
        this.x = Float.parseFloat(String.valueOf(x));
        this.z = Float.parseFloat(String.valueOf(y));

        this.y = this.HeightMap[(int) ((Math.round(z)*10) + Math.round(x))] + 1f ;

        player.move( this.x, this.y, this.z);
    }


    Reader reader = new Reader();
    @Override
    public void simpleInitApp() {
        initTerrain();
        InitBall();

        reader.main();
        moveBall(reader.x0,reader.y0);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    public static void main(String[] args){
        Main game = new Main();
        game.start();
    }
}