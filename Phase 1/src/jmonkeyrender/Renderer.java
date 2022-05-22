package jmonkeyrender;

import bot.botimplementations.BotFactory;
import bot.botimplementations.IBot;
import com.jme3.input.ChaseCamera;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;

import gui.GameStateRenderer;
import gui.MenuGUI;

import physics.*;
import physics.collisionsystems.StopCollisionSystem;
import physics.solvers.RungeKutta4Solver;
import physics.stoppingconditions.SmallVelocityStoppingCondition;
import reader.GameStateLoader;
import utility.math.Vector2;
import datastorage.*;
import gameengine.Camera;

import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
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

    public class Renderer extends Cam {
        private TerrainQuad terrainQuad;
        private GameState gameState;
        private Geometry ballRender;

        private GameStateRenderer minimapGenerator;
        private BufferedImage minimapImg;

        private ArrayList<Vector2> points = new ArrayList<>();
        private Boolean inTarget = false;

        float ballRadius = 1f;
        float totalSize = 1024;

        Ball ball;
        double targetRadius;
        Vector2 targetPos;

        BitmapText text;
        private static final DecimalFormat df = new DecimalFormat("0.00");

        Node mainScene = new Node("Water");

        float normalFactor;
        float terScale = 5;
        float pixelScale = (totalSize+1)/100;
        private Terrain terrain;

        /**
         * Initializes area terrain based on the function given in input file
         */
        public void initTerrain(String texPath){
            //Creating heightmap representation of the terrain
            AlphaMapGenerator.generateAlphaMap(terrain);

            //Setting terrain using heightmap
            this.terrainQuad = new TerrainQuad("Course", 128, (int) (totalSize+1), this.terrain.heightmap);

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

            Texture targetRadius = assetManager.loadTexture(
                    "Terrain/dirt.jpeg");
            targetRadius.setWrap(Texture.WrapMode.Repeat);
            matTerrain.setTexture("Tex3", targetRadius);
            matTerrain.setFloat("Tex3Scale", 32f);

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
            moveBall(this.ball.state.position);
        }

        /**
         * Creates the target hole
         */
        public void InitTarget(){
            //Creating cylinder, which would represent target hole
            Cylinder tar = new Cylinder(120, 120, (float) targetRadius*pixelScale, 0.1f, true);
            Geometry target = new Geometry("Target", tar);

            //Rotating the cylinder
            target.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI , new Vector3f(0,0,1)));

            //Making the target hole white color
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.White);
            target.setMaterial(mat);

            //Finding the position for the target
            float val;
            val = (float) terrain.getTerrainFunction().valueAt(targetPos.x,targetPos.y);
            val += Math.abs(terrain.minVal);
            val /= terrain.maxVal - terrain.minVal;
            if (val < 0) {
                val = 0;
            }
            if (val > 1) {
                val = 1;
            }
            val *= normalFactor*terScale;

            //Moving the cylinder to the calculated position
            target.setLocalTranslation((float) (this.targetPos.x *totalSize/100), val, (float) (this.targetPos.y*totalSize/100));

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
        public void findTangent(Vector2 ballState){
            Vector3f terNormal = terrainQuad.getNormal(new Vector2f((float)ballState.x*pixelScale, (float)ballState.y*pixelScale));
            double scalar = ballRadius/terNormal.length();
            terNormal = terNormal.mult((float) scalar);
            //Just put 0.2 as a threshold, like if the difference is above that, is gonna be visible
            ballRender.move(terNormal.x, terNormal.y, terNormal.z);
        }

        /**
         * Moves ball according to x & y coordinates
         */
        AWTLoader loader = new AWTLoader();
        Picture pic = new Picture("HUD Picture");
        Image img;
        Texture2D texture2D = new Texture2D();
        float val;
        public void moveBall(Vector2 ballState){
            if(ballState.x*pixelScale<(this.totalSize)/2 && ballState.y*pixelScale < (this.totalSize)/2) {
                //Getting height value corresponding to x and y values
                val = (float) terrain.getTerrainFunction().valueAt( ballState.x, ballState.y);
                val += Math.abs(terrain.minVal);
                val /= terrain.maxVal - terrain.minVal;
                if (val < 0) {
                    val = 0;
                }
                if (val > 1) {
                    val = 1;
                }
                val *= normalFactor * terScale;

                //Moving the ball object to specified position
                ballRender.setLocalTranslation((float) (ballState.x*pixelScale), val, (float) (ballState.y*pixelScale));
                //Adjusting the ball not to be in the ground
                findTangent(ballState);
                //Outputting the position of the ball
                text.setText("x: " + df.format(ballState.x) + "  y: " + df.format(ballState.y) + "  z: "+ df.format(val/terScale));

                Camera camera = new Camera(25,25);
                camera.xPos = ball.state.position.x;
                camera.yPos = ball.state.position.y;

                minimapImg = minimapGenerator.getSubimage(camera, false, false);
                img = loader.load(minimapImg, false);
                minimapImg = minimapGenerator.getSubimage(camera, false, false);
                img = loader.load(minimapImg, true);
                texture2D.setImage(img);
                pic.setTexture(assetManager, texture2D, true);
                pic.setHeight(300);
                pic.setWidth(300);
                pic.setPosition(1280-300, 720-300);
                guiNode.attachChild(pic);
                img.dispose();
                minimapImg.flush();
            }
            
        }

        /**
         * Creates a sky background
         * @param path specification to load different background for different maps
         */
        public void InitSky(String path){
            mainScene.attachChild(SkyFactory.createSky(getAssetManager(), path, SkyFactory.EnvMapType.EquirectMap));
            rootNode.attachChild(mainScene);
        }

        /**
         * Spawns water simulation around the terrain
         */
        public void InitWater(){
            if(this.terrain.minScaledVal<normalFactor/2) {
                //Creates new water object reflection
                SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
                waterProcessor.setLightPosition(new Vector3f(0.55f, -0.82f, 0.15f));
                waterProcessor.setReflectionScene(mainScene);

                //Setting the wave size
                Vector3f waterLocation = new Vector3f(0, 0, 0);
                waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
                viewPort.addProcessor(waterProcessor);

                //Creating the box of water'
                Quad waveSize = new Quad(this.totalSize + 200, this.totalSize + 200);
                Geometry water = new Geometry("water", waveSize);
                water.setShadowMode(RenderQueue.ShadowMode.Receive);
                water.setMaterial(waterProcessor.getMaterial());

                //Setting location to be around the terrain
                water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
                water.setLocalTranslation(-totalSize/2-100, this.terrain.minScaledVal * terScale, totalSize/2+100);
                water.move(0, (normalFactor/2-this.terrain.minScaledVal) * terScale, 0);
                //Attaching water object to the scene
                rootNode.attachChild(water);
            }
        }

        /**
         * Initializes all the necessary files for calculating the ball movement
         */
        public void initPhysics(){
            //Attaches the input values to Terrain object
            this.gameState = GameStateLoader.readFile();
            this.terrain = gameState.getTerrain();
            this.normalFactor = (float) terrain.NORMAL_FACTOR;

            this.ball = this.gameState.getBall();
            this.targetRadius = terrain.target.radius;
            this.targetPos = terrain.target.position;

            minimapGenerator = new GameStateRenderer(this.gameState);

            //Initializing terrain
            initTerrain(MenuGUI.texPath);
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
        IBot bot;
        private void setupInitialBot(){
            bot = BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING);
            resetBotThread();
        }

        Thread botThread;
        private Vector2 shotForce;
        private void resetBotThread(){
            botThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Calculating shot...");
                    shotForce = bot.findBestShot(gameState);
                    System.out.println("Velocity: "+shotForce);
                }
            });
        }

        public void setShotForce(Vector2 newShotVector) {
            shotForce = newShotVector;
        }



        @Override
        public void simpleInitApp() {
            //Disabling unnecessary information
            setDisplayStatView(false);

            initPhysics();
            //setting sky background to Sky.jpg
            String path = "Sky/Skysphere.jpeg";
            if(GameStateLoader.OS.contains("Windows")) path = "Sky\\Skysphere.jpeg";
            InitSky(path);
            InitWater();
            InitText();
            InitBall();
            InitTarget();
            setupInitialBot();

            //creating and attaching camera to ball
            ChaseCamera chaseCam = new ChaseCamera(cam, ballRender, inputManager);
            InitCam(chaseCam);
            //flyCam.setMoveSpeed(100);
        }

        private void handleBallInWater() {
            if (isSimulationFinished()) {
                boolean isBallInWater = gameState.getTerrain().getTerrainFunction().valueAt(ball.state.position.x, ball.state.position.y) < 0;
                if (isBallInWater) {
                    resetGame();
                }
            }
        }
        private boolean isSimulationFinished() {
            boolean ballStopped = points.size() == 0;
            boolean noBot = (bot != null && !botThread.isAlive()) || (bot == null);
            boolean ballHasBeenPushed = shotForce == null;
            return ballHasBeenPushed && noBot && ballStopped;
        }
        private void resetGame() {
            gameState.getBall().state.position = gameState.getTerrain().ballStartingPosition;
            if (bot != null && botThread.isAlive()) {
                // End the bot thread if it is still running
                try {
                    botThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            shotForce = null;
            points = new ArrayList<>();
        }

        private void handleInput() {
            if (isSimulationFinished() && !isInTarget(ball)) {
                    resetBotThread();
                    botThread.start();
                }
        }


        @Override
        public void simpleUpdate(float tpf) {
            //simulates from Vectors.csv file
            //moves the ball with calculated position
            handleBallInWater();
            handleInput();
            if (points.size() == 0 && shotForce != null && !inTarget) {
                points = gameState.simulateShot(shotForce);
                //numShots++;
                shotForce = null;
            }
            if(points.size() != 0) {
                gameState.setBallPosition(points.get(0));
                moveBall(ball.state.position);
                points.remove(0);
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
