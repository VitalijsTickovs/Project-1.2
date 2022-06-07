package visualization.jmonkeyrender;

import bot.botimplementations.BotFactory;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import gui.MenuGUI;

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

public class Renderer extends SimpleApplication {
    private MapGeneration mapGeneration;
    private ObjectGeneration objectGeneration;
    private UIGeneration uiGeneration;
    private final Cam camInit = new Cam();

    Update updateLoop;

    private TerrainQuad terrainQuad;
    private GameState gameState;
    private Geometry ballRender;

    private final float ballRadius = 1f;

    private Terrain terrain;

    private Ball ball;

    private BitmapText text;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private float normalFactor;
    private final float terScale = 6;
    private float pixelScale;


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

    /**
     * Initializes physics for calculating the ball movement
     */
    public void InitPhysics(){
        //Attaches the input values to Terrain object
        this.gameState = GameStateLoader.readFile();
        this.terrain = gameState.getTerrain();
        this.normalFactor = (float) terrain.NORMAL_FACTOR;
        this.pixelScale = (float) terrain.getVERTECES_PER_SIDE()/100;

        this.ball = this.gameState.getBall();

        mapGeneration = new MapGeneration(this);
        objectGeneration = new ObjectGeneration(this);
        uiGeneration = new UIGeneration(this);
        updateLoop = new Update(gameState);
    }

    private void setupInitialBot(){
        updateLoop.setBot(BotFactory.getBot(BotFactory.BotImplementations.HILL_CLIMBING));
    }

    @Override
    public void simpleInitApp() {
        //Disabling unnecessary information
        setDisplayStatView(false);

        InitPhysics();

        mapGeneration.InitMap(MenuGUI.texPath);
        objectGeneration.InitTarBall();
        uiGeneration.InitText(guiFont);
        moveBall(this.ball.state.position);
        setupInitialBot();

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