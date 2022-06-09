package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.scene.debug.Arrow;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import gui.MenuGUI;

import gui.shotinput.IClickListener;
import reader.GameStateLoader;
import utility.math.Vector2;
import datastorage.*;

import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import visualization.Update;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Renderer extends SimpleApplication {
    protected int WIDTH = 1280;
    protected int HEIGHT = 720;

    private MapGeneration mapGeneration;
    private ObjectGeneration objectGeneration;
    private UIGeneration uiGeneration;
    private InputsGenerator inputsGenerator;
    private final Cam camInit = new Cam();

    private Update updateLoop;

    private TerrainQuad terrainQuad;
    private GameState gameState;
    private Geometry ballRender;

    private final float ballRadius = 1f;

    private Terrain terrain;

    protected Ball ball;

    private BitmapText text;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private float normalFactor;
    private final float terScale = 6;
    private float pixelScale;

    public ArrayList<IClickListener> clickListeners = new ArrayList<>();
    public boolean drawArrow = false;

    public Update getUpdateLoop() {
        return updateLoop;
    }

    public GameState getGameState() {
        return gameState;
    }

    public float getTerScale() {
        return terScale;
    }

    public float getNormalFactor() {
        return normalFactor;
    }

    public float getPixelScale(){
        return pixelScale;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public void setTerrainQuad(TerrainQuad terrainQuad) {
        this.terrainQuad = terrainQuad;
    }

    public void setText(BitmapText text) {
        this.text = text;
    }

    public void setBallRender(Geometry ballRender) {
        this.ballRender = ballRender;
    }

    /**
     * Moves the by finding the normal tangent by radius of the ball
     * so it would not be that ball is in the terrain
     */
    public void findTangent(Vector2 ballState){
        Vector3f terNormal = terrainQuad.getNormal(new Vector2f((float)ballState.x*pixelScale, (float)ballState.y*pixelScale));
        double scalar = ballRadius/terNormal.length();
        terNormal = terNormal.mult((float) scalar);
        ballRender.move(terNormal.x, terNormal.y, terNormal.z);
    }

    /**
     * Moves ball according to x & y coordinates
     */
    public void moveBall(Vector2 ballState){
        if(ballState.x*pixelScale<(float)(terrain.getVERTECES_PER_SIDE()-1)/2 && ballState.y*pixelScale < (float)(terrain.getVERTECES_PER_SIDE())/2) {
            //Getting height value corresponding to x and y values
            float val = terrain.HeightMapValueAt(ballState)*terScale;

            //Moving the ball object to specified position
            ballRender.setLocalTranslation((float) (ballState.x)*pixelScale, val, (float) (ballState.y*pixelScale));

            //Adjusting the ball not to be in the ground
            findTangent(ballState);

            //Outputting the position of the ball
            text.setText("x: " + df.format(ballState.x) + "  y: " + df.format(ballState.y) + "  z: "+ df.format(val/terScale));

            //Displaying minimap based on the ball position
            uiGeneration.generateMinimap(ballState);
        }
    }

    public void drawArrow(Vector2f cursorPos) {
        cursorPos = cursorPos.add(new Vector2f((float)ball.state.position.x,(float)ball.state.position.y));
        Arrow arrow = new Arrow(new Vector3f(1,0,0));

        Geometry arrowRender = new Geometry("Arrow", arrow);
        Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setColor("Color", ColorRGBA.Red);
        arrowRender.setMaterial(redMat);

        arrowRender.setLocalTranslation((float)ball.state.position.x,terrain.HeightMapValueAt(ball.state.position)+2*terScale,(float)ball.state.position.y);
        arrowRender.setLocalScale(100,1,1);
        getRootNode().attachChild(arrowRender);
    }

    public void initPointers(){
        mapGeneration = new MapGeneration(this);
        objectGeneration = new ObjectGeneration(this);
        uiGeneration = new UIGeneration(this);
        inputsGenerator = new InputsGenerator(this);
        updateLoop = new Update(gameState);
    }
    /**
     * Initializes physics for calculating the ball movement
     */
    public void initPhysics(){
        //Attaches the input values to Terrain object
        this.gameState = GameStateLoader.readFile();
        this.terrain = gameState.getTerrain();
        this.normalFactor = (float) terrain.NORMAL_FACTOR;
        this.pixelScale = (float) terrain.getVERTECES_PER_SIDE()/100;

        this.ball = this.gameState.getBall();
    }

    @Override
    public void simpleInitApp() {
        //Disabling unnecessary information
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_MEMORY );
        setDisplayStatView(false);

        initPhysics();
        initPointers();

        mapGeneration.initMap(MenuGUI.texPath);
        objectGeneration.initTarBall();
        uiGeneration.initText(guiFont);
        moveBall(this.ball.state.position);

        updateLoop.setManualInputType3d(this);

        inputsGenerator.initKeys();
        inputsGenerator.initMouse();

        //creating and attaching camera to ball
        ChaseCamera chaseCam = new ChaseCamera(cam, ballRender, inputManager);
        camInit.InitCam(chaseCam,this);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //simulates from Vectors.csv file
        //moves the ball with calculated position
        updateLoop.updateLoop();
        if(updateLoop.getBallPositions().size() != 0) {
            gameState.setBallPosition(updateLoop.getBallPositions().get(0));
            moveBall(ball.state.position);
            updateLoop.getBallPositions().remove(0);
        }
        Vector2f cursorPos = getInputManager().getCursorPosition();
        cursorPos.x -= (float)WIDTH/2; cursorPos.y -= (float) HEIGHT/2;
        cursorPos = cursorPos.mult(0.3f);
        drawArrow(cursorPos);
    }

    public void start3d(){
        this.setShowSettings(false);
        // Setting up renderer settings, so JME settings tab wouldn't pop out
        AppSettings settings = new AppSettings(true);
        settings.put("Width", WIDTH);
        settings.put("Height", HEIGHT);
        settings.put("Title", "Golf Game");
        settings.put("VSync", true);
        settings.put("Samples", 4);
        this.setSettings(settings);


        this.start();
    }

    public Vector2 getMousePosition() {
        Vector3f location = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
        System.out.println(location);
        return new Vector2(location.x,location.z);
    }


}