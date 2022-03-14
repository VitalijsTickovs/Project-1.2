import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends SimpleApplication {

    protected Geometry player;
    @Override
    public void simpleInitApp() {
        Sphere ball = new Sphere(32, 32, 5f);
        TangentBinormalGenerator.generate(ball);
        player = new Geometry("Ball", ball);

        //Texture sphereTex = assetManager.loadTexture(
        //        "Ball/Golfball.jpeg");

        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        //mat.setTexture("BallTexture", sphereTex);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse",ColorRGBA.White);
        mat.setColor("Specular",ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        player.setMaterial(mat);
        rootNode.attachChild(player);


        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }



    public static void main(String[] args){
        Main game = new Main();
        game.start();
    }
}