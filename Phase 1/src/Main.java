import Data_storage.TerrainFunction1;
import Reader.Reader;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
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
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

import javax.imageio.ImageIO;
<<<<<<< HEAD
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main extends SimpleApplication {
=======
import java.io.IOException;
import java.net.URL;

public class Main extends Cam {
>>>>>>> 6e98647c9e12f4c134c39034f43017ff06f001fc

    float[] HeightMap;

    public void initTerrain(){
<<<<<<< HEAD
        TerrainFunction1 generator = new TerrainFunction1("sin((x - y)/7) + 0.5");
        this.HeightMap = generator.getHeightMap(128, 128, 5.0);
=======
        TerrainFunction1 generator = new TerrainFunction1("sin(x+y)");
        this.HeightMap = generator.getHeightMap(128, 128, 50);
>>>>>>> 6e98647c9e12f4c134c39034f43017ff06f001fc

        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");  // create a simple material
        //mat1.setColor("Color", ColorRGBA.Green);

        TerrainQuad terrain = new TerrainQuad("Course", 65, 129, this.HeightMap);
        terrain.setMaterial(mat1);
        terrain.move(0,-5,0);

        DirectionalLight light = new DirectionalLight();
        light.setColor(ColorRGBA.White);
        light.setDirection(new Vector3f(-10.0f, -1.0f, -1.0f).normalize());

        rootNode.attachChild(terrain);
        rootNode.addLight(light);

    }


    protected Geometry player;
    float x, y, z;
    public void InitBall(){
        Sphere ball = new Sphere(200, 120, 1f);
        TangentBinormalGenerator.generate(ball);
        player = new Geometry("Ball", ball);

        Texture sphereTex = assetManager.loadTexture(
                "Ball/Golfball.jpeg");

        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        //mat.setTexture("NormalMap", sphereTex);
        mat.setColor("Color", ColorRGBA.Red);
        //mat.setBoolean("UseMaterialColors", true);
        //mat.setColor("Diffuse",ColorRGBA.White);
        //mat.setColor("Specular",ColorRGBA.White);
        //mat.setFloat("Shininess", 64f);
        player.setMaterial(mat);
        rootNode.attachChild(player);
    }

    public void moveBall(Double x, Double y){
        this.x = Float.parseFloat(String.valueOf(x));
        this.y = Float.parseFloat(String.valueOf(y));

        this.z = this.HeightMap[(int) ((Math.round(x)*10) + Math.round(y))];


        player.move( this.x, this.y, this.z);
    }

    public void InitSky(){
        //Texture sky = assetManager.loadTexture("Sky/Sky2.jpg");
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Sky/Sky.jpg", SkyFactory.EnvMapType.SphereMap));
    }


    Reader reader = new Reader();
    @Override
    public void simpleInitApp() {
        initTerrain();
        InitBall();
        ChaseCamera chaseCam = new ChaseCamera(cam, player, inputManager);
        InitCam(chaseCam);

        InitSky();

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