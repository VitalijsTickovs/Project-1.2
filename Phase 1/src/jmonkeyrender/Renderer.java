package jmonkeyrender;

import gameengine.Game;
import physics.*;
import physics.collisionsystems.StopCollisionSystem;
import physics.solvers.RungeKutta4Solver;
import physics.stoppingconditions.SmallVelocityStoppingCondition;
import reader.GameStateLoader;
import utility.math.Vector2;
import datastorage.*;
import gui.MenuGUI;

import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
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

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

    public class Renderer extends Cam {
        private TerrainQuad terrainQuad;
        private GameState gameState;
        private Geometry ballRender;

        private ArrayList<Vector2> points = new ArrayList<>();
        private Boolean inTarget = false;

        float ballRadius = 1f;
        float totalSize = 1024;

        float xoff = 0;
        float yoff = 0;

        Vector2 ballStartPos;
        double targetRadius;
        Vector2 targetPos;
        private PhysicsEngine engine;

        BitmapText text;
        private static final DecimalFormat df = new DecimalFormat("0.00");

        Node mainScene = new Node("Water");

        float x = 0;
        float y = 0;
        float val = 0;
        float normalFactor;
        float terScale = 10;
        private Terrain terrain;

        /**
         * Initializes area terrain based on the function given in input file
         */
        public void initTerrain(String texPath){

            //Calculating offset to spawn the terrain arround the ball
            this.xoff = (float) (this.ballStartPos.x - this.totalSize/2);
            this.yoff = (float) (this.ballStartPos.y - this.totalSize/2);

            //Creating heightmap representation of the terrain
            AlphaMapGenerator.generateAlphaMap(terrain);

            //Setting terrain using heightmap
            this.terrainQuad = new TerrainQuad("Course", 65, (int) (totalSize+1), this.terrain.heightmap);

            //Setting up the Texture of the ground
            Material matTerrain = new Material(assetManager,"Common/MatDefs/Terrain/Terrain.j3md");
            matTerrain.setTexture("Alpha", assetManager.loadTexture(
                    "Terrain/image.png"));
            Texture grass = assetManager.loadTexture(texPath);
            grass.setWrap(Texture.WrapMode.Repeat);
            matTerrain.setTexture("Tex1", grass);
            matTerrain.setFloat("Tex1Scale", 64f);

            Texture sand = assetManager.loadTexture(
                    "Terrain/sand.jpeg");
            sand.setWrap(Texture.WrapMode.Repeat);
            matTerrain.setTexture("Tex2", sand);
            matTerrain.setFloat("Tex2Scale", 32f);

            AmbientLight amb = new AmbientLight();
            amb.setColor(ColorRGBA.White.mult(5));
            rootNode.addLight(amb);

            terrainQuad.setMaterial(matTerrain);
            terrainQuad.scale(1,terScale,1);
            rootNode.attachChild(terrainQuad);
        }

        /**
         * Creates golf ball, with textures
         */
        public void InitBall(){
            //Creates Sphere object and adds to Geometry object
            Sphere ball = new Sphere(120, 120, ballRadius);
            TangentBinormalGenerator.generate(ball);
            this.ballRender = new Geometry("Ball", ball);

            //Adding textures to the ball
            Texture sphereTex = assetManager.loadTexture("Ball/Golfball.jpeg");
            Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
            mat.setTexture("ColorMap", sphereTex);
            this.ballRender.setMaterial(mat);

            //add the geometry object to the scene
            rootNode.attachChild(this.ballRender);
            //Moving the ball according to the start position
            moveBall(ballStartPos);
        }

        /**
         * Creates the target hole
         */
        public void InitTarget(){
            //Creating cylinder, which would represent target hole
            Cylinder tar = new Cylinder(120, 120, (float) targetRadius, 0.1f, true);
            Geometry target = new Geometry("Target", tar);

            //Rotating the cylinder
            target.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI , new Vector3f(0,0,1)));

            //Making the target hole white color
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.White);
            target.setMaterial(mat);

            //Finding the position for the target
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

            //Moving the cylinder to the calculatd position
            target.setLocalTranslation((float) this.targetPos.x, val*terScale, (float) this.targetPos.y);

            //Adding it to the scene
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

        /**
         * Moves the by finding the normal tangent by radius of the ball
         * so it would not seem that ball is in the terrain
         */
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
                val *= normalFactor * terScale;
                this.val = val;

                //Moving the ball object to specified position
                ballRender.setLocalTranslation((float) (this.x + terrain.xOff), (float) (this.val + terrain.xOff), (float) (this.y + terrain.xOff));
                //Adjusting the ball not to be in the ground
                findTangent();
                //Outputting the position of the ball
                text.setText("x: " + df.format(getBallX()) + "  y: " + df.format(getBallY()) + "  z: "+ df.format(getBallZ()/terScale));
            }
        }

        /**
         * Creates a sky background
         * @param path specification to load different background for different maps
         */
        public void InitSky(String path){
            mainScene.attachChild(SkyFactory.createSky(getAssetManager(), path, SkyFactory.EnvMapType.SphereMap));
            rootNode.attachChild(mainScene);
        }

        /**
         * Spawns water simulation around the terrain
         */
        public void InitWater(){
            //Creates new water object reflection
            SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
            waterProcessor.setLightPosition(new Vector3f(0.55f, -0.82f, 0.15f));
            waterProcessor.setReflectionScene(mainScene);

            //Setting the wave size
            Vector3f waterLocation=new Vector3f(0,0,0);
            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
            viewPort.addProcessor(waterProcessor);

            //Creating the box of water
            Quad waveSize = new Quad(this.totalSize + 200,this.totalSize + 200);
            Geometry water=new Geometry("water", waveSize);
            water.setShadowMode(RenderQueue.ShadowMode.Receive);
            water.setMaterial(waterProcessor.getMaterial());

            //Setting location to be around the terrain
            water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
            System.out.println(this.terrain.minScaledVal);
            water.setLocalTranslation(xoff-100, this.terrain.minScaledVal*terScale, -yoff+100);
            float heightLength = this.terrain.maxScaledVal-this.terrain.minScaledVal;

            if(heightLength>normalFactor/2) water.move(0,(heightLength-normalFactor/2)*terScale,0);

            //Attaching water object to the scene
            rootNode.attachChild(water);
        }

        /**
         * Initializes all the necessary files for calculating the ball movement
         */
        public void initPhysics(){
            //Attaches the input values to Terrain object
            this.gameState = GameStateLoader.readFile();
            this.terrain = gameState.getTerrain();
            this.normalFactor = (float) terrain.NORMAL_FACTOR;

            this.ballStartPos = this.gameState.getBall().state.position;
            this.targetRadius = GameStateLoader.getTargetRadius();
            this.targetPos = new Vector2(GameStateLoader.getTargetX(), GameStateLoader.getTargetY());

            //Initializing terrain
            initTerrain(MenuGUI.texPath);

            //setting the physics engine
            ball = new Ball(this.ballStartPos, new Vector2(3, -5));
            engine = new PhysicsEngine(new RungeKutta4Solver(0.01), new SmallVelocityStoppingCondition(), new StopCollisionSystem());
        }

        /**
         * Displays text with current ball position
         */
        public void InitText(){
            BitmapText hudText = new BitmapText(guiFont);
            hudText.setSize(guiFont.getCharSet().getRenderedSize());
            hudText.setColor(ColorRGBA.White);
            hudText.setText("");
            hudText.setLocalTranslation(0, 50, 0);
            this.text = hudText;
            guiNode.setQueueBucket(RenderQueue.Bucket.Gui);
            guiNode.attachChild(hudText);

        }



        Ball ball;
        Queue<Vector2> q;
        @Override
        public void simpleInitApp() {
            //Disabling unnecessary information
            setDisplayStatView(false);
            //Reading from the file to get move set
            q = VectorsReader.read("/Phase 1/src/Physics/Vectors.csv");

            initPhysics();
            //setting sky background to Sky.jpg
            InitSky("Sky/Skysphere.jpeg");
            InitWater();
            InitText();
            InitBall();
            InitTarget();

            //creating and attaching camera to ball
            //ChaseCamera chaseCam = new ChaseCamera(cam, ballRender, inputManager);
            //InitCam(chaseCam);
            flyCam.setMoveSpeed(100);
        }

        float ballx =.1f;
        @Override
        public void simpleUpdate(float tpf) {
            if (!inTarget) {
                //simulates from Vectors.csv file
                //moves the ball with calculated position
                //if (points.size() != 0) {
                //ball.state.position = points.get(0);
                moveBall(new Vector2(ballx,ballx));
                //points.remove(0);
                ballx++;
                //}
                //checks if the ball is in the hole
                isInTarget(ball);
            }else{
                //exit if ball reached the target
                System.exit(1);
            }
        }

        public void start3d(){
            this.setShowSettings(false);
            // Setting up renderer settings, so JME settings tab wouldn't pop out
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
