package reader;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import datastorage.*;
import datastorage.obstacles.IObstacle;
import datastorage.obstacles.ObstacleBox;
import datastorage.obstacles.ObstacleTree;
import physics.collisionsystems.*;
import physics.*;
import physics.solvers.*;
import physics.stoppingconditions.*;
import utility.math.Vector2;

public class GameStateLoader {

   private static final String delimiter = ";";
   private static Scanner scanner;

   // region private Variables
   // Singular values
   private static double solverStep;

   private static IODESolver ODEsolver;
   private static IStoppingCondition stoppingCondition;
   private static ICollisionSystem collisionSystem;

   private static double terrainX0;
   private static double terrainY0;
   private static double terrainX1;
   private static double terrainY1;
   private static double greenKineticFriction;
   private static double greenStaticFriction;
   private static String terrainFunction;

   private static double ballStartPointX;
   private static double ballStartPointY;
   private static double ballRadius;

   private static double targetRadius;
   private static double targetX;
   private static double targetY;

   // ArrayList values
   private static ArrayList<Double> sandZoneX0 = new ArrayList<Double>();
   private static ArrayList<Double> sandZoneY0 = new ArrayList<Double>();
   private static ArrayList<Double> sandZoneX1 = new ArrayList<Double>();
   private static ArrayList<Double> sandZoneY1 = new ArrayList<Double>();
   private static ArrayList<Double> sandKineticFriction = new ArrayList<Double>();
   private static ArrayList<Double> sandStaticFriction = new ArrayList<Double>();

   private static ArrayList<Double> treeX = new ArrayList<Double>();
   private static ArrayList<Double> treeY = new ArrayList<Double>();
   private static ArrayList<Double> treeRadius = new ArrayList<Double>();
   private static ArrayList<Double> treeBounciness = new ArrayList<Double>();

   private static ArrayList<Double> boxX0 = new ArrayList<Double>();
   private static ArrayList<Double> boxY0 = new ArrayList<Double>();
   private static ArrayList<Double> boxX1 = new ArrayList<Double>();
   private static ArrayList<Double> boxY1 = new ArrayList<Double>();
   private static ArrayList<Double> boxBounciness = new ArrayList<Double>();

   private static Terrain terrain; // The generated terrain that will be returned
   // endregion

   // region default variable values
   private final static double defsolverStep = 0.01;

   private final static IODESolver defODEsolver = new RungeKutta4Solver(defsolverStep);
   private final static IStoppingCondition defstoppingCondition = new SmallVelocityStoppingCondition();
   private final static ICollisionSystem defcollisionSystem = new BounceCollisionSystem();

   private final static double defx0 = 0;
   private final static double defy0 = 0;
   private final static double defxt = 5;
   private final static double defyt = 5;
   private final static double defgreenKineticFriction = 0.05;
   private final static double defgreenStaticFriction = 0.1;
   private final static String defterrainFunction = "sin(x+y)"; // ask Niko for his implementation and leave it as a
                                                                // String for now

   private final static double defballStartPointX = 1;
   private final static double defballStartPointY = 1;

   private final static double deftargetRadius = 0.1;
   private final static double deftargetX = 4;
   private final static double deftargetY = 4;

   // ArrayList values
   private final static double defsandZoneX0 = 0;
   private final static double defsandZoneY0 = 0;
   private final static double defsandZoneX1 = 100;
   private final static double defsandZoneY1 = 100;
   private final static double defsandKineticFriction = 0.25;
   private final static double defsandStaticFriction = 0.4;

   private final static double deftreeX = 0.5;
   private final static double deftreeY = 0.5;
   private final static double deftreeRadius = 0.5;
   private final static double deftreeBounciness = 1;

   private final static double defBoxX0 = 0;
   private final static double defBoxY0 = 0;
   private final static double defBoxX1 = 1;
   private final static double defBoxY1 = 1;
   private final static double defBoxBounciness = 1;

   public static double[] getSandX() {
      return new double[] { defsandZoneX0, defsandZoneX1 };
   }

   public static double[] getSandY() {
      return new double[] { defsandZoneY0, defsandZoneY1 };
   }

   public static GameState readFile() {
      createScanner();

      resetVariables();
      String[] allLinesSplit = splitLines();
      readVariables(allLinesSplit);

      return createGameStateUsingGeneratedData();
   }

   private static void resetVariables() {
      solverStep = 0;
      ODEsolver = null;
      stoppingCondition = null;
      collisionSystem = null;

      terrainX0 = 0;
      terrainY0 = 0;
      terrainX1 = 0;
      terrainY1 = 0;

      greenKineticFriction = 0;
      greenStaticFriction = 0;
      terrainFunction = null;

      ballStartPointX = 0;
      ballStartPointY = 0;

      targetRadius = 0;
      targetX = 0;
      targetY = 0;

      sandZoneX0 = new ArrayList<>();
      sandZoneY0 = new ArrayList<>();
      sandZoneX1 = new ArrayList<>();
      sandZoneY1 = new ArrayList<>();
      sandKineticFriction = new ArrayList<>();
      sandStaticFriction = new ArrayList<>();

      treeX = new ArrayList<>();
      treeY = new ArrayList<>();
      treeBounciness = new ArrayList<>();
      treeRadius = new ArrayList<>();

      boxX0 = new ArrayList<>();
      boxY0 = new ArrayList<>();
      boxX1 = new ArrayList<>();
      boxY1 = new ArrayList<>();
      boxBounciness = new ArrayList<>();
   }

   private static GameState createGameStateUsingGeneratedData() {
      Terrain generatedTerrain = createTerrain();
      Ball startingBall = createBall();
      PhysicsEngine engine = createEngine();

      GameState gameState = new GameState(generatedTerrain, startingBall, engine);
      return gameState;
   }

   /**
    * Tries to create a buffered reader
    * 
    * @return true, if the reader has been successfully created
    */
   private static void createScanner() {
      try {
         // scanner = new Scanner(new FileReader(System.getProperty("user.dir") + "/Phase 1/src/Reader/UserInput.csv"));
         // The top line does not work on my computer, so I put the one at the bottom -
         // comment it out and switch.
         // I was not able to come up with a line of code that would work on everyone's
         // computer
         scanner = new Scanner(
         new FileReader(System.getProperty("user.dir") +"/Phase 1/src/reader/UserInput.csv"));

      } catch (FileNotFoundException e) {
         throw new NullPointerException("File not found - the path to the save file is wrong, see comment above");
      } catch (NullPointerException e) {
         throw new NullPointerException("The path to the save file itself was null");
      }
   }

   private static String[] splitLines() {
      String wholeLine = "";
      while (scanner.hasNextLine()) {
         wholeLine += scanner.nextLine();
      }
      scanner.close();
      return wholeLine.split(delimiter);
   }

   private static void readVariables(String[] allLinesSplit) {
      for (String line : allLinesSplit) {
         checkLineForVariables(line);
      }
   }

   private static void checkLineForVariables(String line) {
      // Physics engine
      if (lineContainsKeywordAndEqualSign(line, "solverStep")) {
         solverStep = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "ODEsolver")) {
         ODEsolver = readSolver(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "stoppingCondition")) {
         stoppingCondition = readStoppingCondition(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "collisionSystem")) {
         collisionSystem = readCollisionSystem(line);
      }
      // Green
      if (lineContainsKeywordAndEqualSign(line, "terrainX0")) {
         terrainX0 = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "terrainY0")) {
         terrainY0 = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "terrainX1")) {
         terrainX1 = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "terrainY1")) {
         terrainY1 = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "greenKineticFriction")) {
         greenKineticFriction = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "greenStaticFriction")) {
         greenStaticFriction = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "terrainFunction")) {
         terrainFunction = readString(line);
      }
      // Ball
      if (lineContainsKeywordAndEqualSign(line, "ballStartPointX")) {
         ballStartPointX = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "ballStartPointY")) {
         ballStartPointY = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "ballRadius")) {
         ballRadius = readDouble(line);
      }
      // Target
      if (lineContainsKeywordAndEqualSign(line, "targetRadius")) {
         targetRadius = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "targetX")) {
         targetX = readDouble(line);
      }
      if (lineContainsKeywordAndEqualSign(line, "targetY")) {
         targetY = readDouble(line);
      }
      // Sand zone
      if (lineContainsKeywordAndEqualSign(line, "sandZoneX")) {
         double[] range = readRange(line);
         sandZoneX0.add(range[0]);
         sandZoneX1.add(range[1]);
      }
      if (lineContainsKeywordAndEqualSign(line, "sandZoneY")) {
         double[] range = readRange(line);
         sandZoneY0.add(range[0]);
         sandZoneY1.add(range[1]);
      }
      if (lineContainsKeywordAndEqualSign(line, "sandKineticFriction")) {
         sandKineticFriction.add(readDouble(line));
      }
      if (lineContainsKeywordAndEqualSign(line, "sandStaticFriction")) {
         sandStaticFriction.add(readDouble(line));
      }
      // Tree
      if (lineContainsKeywordAndEqualSign(line, "treeX")) {
         treeX.add(readDouble(line));
      }
      if (lineContainsKeywordAndEqualSign(line, "treeY")) {
         treeY.add(readDouble(line));
      }
      if (lineContainsKeywordAndEqualSign(line, "treeRadius")) {
         treeRadius.add(readDouble(line));
      }
      if (lineContainsKeywordAndEqualSign(line, "treeBounciness")) {
         treeBounciness.add(readDouble(line));
      }
      // Box
      if (lineContainsKeywordAndEqualSign(line, "boxX")) {
         double[] range = readRange(line);
         boxX0.add(range[0]);
         boxX1.add(range[1]);
      }
      if (lineContainsKeywordAndEqualSign(line, "boxY")) {
         double[] range = readRange(line);
         boxY0.add(range[0]);
         boxY1.add(range[1]);
      }
      if (lineContainsKeywordAndEqualSign(line, "boxBounciness")) {
         boxBounciness.add(readDouble(line));
      }
   }

   private static boolean lineContainsKeywordAndEqualSign(String line, String keyword) {
      return line.contains(keyword) && line.contains("=");
   }

   // region Read Objects
   private static IODESolver readSolver(String line) {
      try {
         String name = line.substring(line.lastIndexOf("=") + 1);
         return getSolverFromName(name);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return defODEsolver;
      }
   }

   private static IODESolver getSolverFromName(String name) {
      if (solverStep == 0) {
         solverStep = defsolverStep;
      }

      if (name.contains("Euler")) {
         return new EulerSolver(solverStep);
      }
      if (name.contains("RK2")) {
         return new RungeKutta2Solver(solverStep);
      }
      if (name.contains("RK4")) {
         return new RungeKutta4Solver(solverStep);
      } else {
         return defODEsolver;
      }
   }

   private static IStoppingCondition readStoppingCondition(String line) {
      try {
         String name = line.substring(line.lastIndexOf("=") + 1);
         return getStoppingConditionFromName(name);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return defstoppingCondition;
      }
   }

   private static IStoppingCondition getStoppingConditionFromName(String name) {
      if (name.contains("smallV")) {
         return new SmallVelocityStoppingCondition();
      } else {
         return defstoppingCondition;
      }
   }

   private static ICollisionSystem readCollisionSystem(String line) {
      try {
         String name = line.substring(line.lastIndexOf("=") + 1);
         return getCollisionSystemFromName(name);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return defcollisionSystem;
      }
   }

   private static ICollisionSystem getCollisionSystemFromName(String name) {
      if (name.contains("bounce")) {
         return new BounceCollisionSystem();
      }
      if (name.contains("stop")) {
         return new StopCollisionSystem();
      } else {
         return defcollisionSystem;
      }
   }
   // endregion

   // region Read Values
   private static double readDouble(String line) {
      try {
         if (line.contains("=")) {
            String temp = (line.substring(line.lastIndexOf("=") + 1));
            return Double.parseDouble(temp);
         } else {
            return Double.parseDouble(line);
         }
      } catch (NullPointerException e) {
         System.out.println("String after = was null");
         return 0;
      } catch (NumberFormatException e) {
         System.out.println("String after = was not parsable into a double");
         return 0;
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return 0;
      }
   }

   private static String readString(String line) {
      try {
         return (line.substring(line.lastIndexOf("=") + 1));
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return null;
      }
   }

   private static double[] readRange(String line) {
      try {
         String temp = line.substring(line.lastIndexOf("=") + 2);
         String[] split = temp.split("<");
         double[] range = new double[2];
         range[0] = readDouble(split[0]);
         range[1] = readDouble(split[1]);
         return range;

      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         return null;
      } catch (Exception e) {
         System.out.println("Pattern syntax was invalid");
         return null;
      }
   }
   // endregion

   // region Create Terrain
   private static Terrain createTerrain() {
      terrain = new Terrain();
      // Singular values
      defineGreen();
      defineTarget();
      defineStartingPoint();

      // Multiple values
      defineObstacles();
      defineZones();

      return terrain;
   }

   private static void defineGreen() {
      if (terrainX0 == 0 && terrainY0 == 0) {
         terrain.topLeftCorner = new Vector2(defx0, defy0);
      } else {
         terrain.topLeftCorner = new Vector2(terrainX0, terrainY0);
      }
      if (terrainX1 == 0 && terrainY1 == 0) {
         terrain.bottomRightCorner = new Vector2(defxt, defyt);
      } else {
         terrain.bottomRightCorner = new Vector2(terrainX1, terrainY1);
      }

      if (greenKineticFriction == 0) {
         terrain.kineticFriction = defgreenKineticFriction;
      } else {
         terrain.kineticFriction = greenKineticFriction;
      }
      if (greenStaticFriction == 0) {
         terrain.staticFriction = defgreenStaticFriction;
      } else {
         terrain.staticFriction = greenStaticFriction;
      }
      TerrainFunction1 decodedFunction;
      if (terrainFunction == null) {
         decodedFunction = new TerrainFunction1(defterrainFunction);
      } else {
         decodedFunction = new TerrainFunction1(terrainFunction);
      }
      terrain.setTerrainFunction(decodedFunction);

   }

   private static void defineTarget() {
      Target target = new Target();
      if (targetRadius == 0) {
         target.radius = deftargetRadius;
      } else {
         target.radius = targetRadius;
      }
      if (targetX == 0 && targetY == 0) {
         target.position = new Vector2(deftargetX, deftargetY);
      } else {
         target.position = new Vector2(targetX, targetY);
      }
      terrain.target = target;
   }

   private static void defineStartingPoint() {
      if (ballStartPointX == 0 && ballStartPointY == 0) {
         terrain.ballStartingPosition = new Vector2(defballStartPointX, defballStartPointY);
      } else {
         terrain.ballStartingPosition = new Vector2(ballStartPointX, ballStartPointY);
      }
   }

   // region Obstacles
   private static void defineObstacles() {
      ArrayList<IObstacle> obstacles = new ArrayList<>();

      obstacles.addAll(createTrees());
      obstacles.addAll(createBoxes());

      terrain.obstacles = obstacles.toArray(new IObstacle[0]);
   }

   private static ArrayList<IObstacle> createTrees() {
      ArrayList<IObstacle> trees = new ArrayList<>();
      while (hasTree()) {
         trees.add(createTree());
      }
      return trees;
   }

   private static boolean hasTree() {
      return treeX.size() > 0 || treeY.size() > 0 || treeRadius.size() > 0 || treeBounciness.size() > 0;
   }

   private static IObstacle createTree() {
      ObstacleTree tree = new ObstacleTree();
      Vector2 position = new Vector2();
      if (treeX.size() > 0) {
         position.x = treeX.get(0);
         treeX.remove(0);
      } else {
         position.x = deftreeX;
      }
      if (treeY.size() > 0) {
         position.y = treeY.get(0);
         treeY.remove(0);
      } else {
         position.y = deftreeY;
      }
      if (treeRadius.size() > 0) {
         tree.radius = treeRadius.get(0);
         treeRadius.remove(0);
      } else {
         tree.radius = deftreeRadius;
      }
      if (treeBounciness.size() > 0) {
         tree.bounciness = treeBounciness.get(0);
         treeBounciness.remove(0);
      } else {
         tree.bounciness = deftreeBounciness;
      }
      tree.originPosition = position;
      return tree;
   }

   private static ArrayList<IObstacle> createBoxes() {
      ArrayList<IObstacle> boxes = new ArrayList<>();
      while (hasBox()) {
         boxes.add(createBox());
      }
      return boxes;
   }

   private static boolean hasBox() {
      return boxX0.size() > 0 || boxX1.size() > 0 || boxY0.size() > 0 || boxY1.size() > 0 || boxBounciness.size() > 0;
   }

   private static IObstacle createBox() {
      // Position
      Vector2 position0 = new Vector2();
      Vector2 position1 = new Vector2();
      if (boxX0.size() > 0) {
         position0.x = boxX0.get(0);
         boxX0.remove(0);
      } else {
         position0.x = defBoxX0;
      }
      if (boxY0.size() > 0) {
         position0.y = boxY0.get(0);
         boxY0.remove(0);
      } else {
         position0.y = defBoxY0;
      }
      if (boxX1.size() > 0) {
         position1.x = boxX1.get(0);
         boxX1.remove(0);
      } else {
         position1.x = defBoxX1;
      }
      if (boxY1.size() > 0) {
         position1.y = boxY1.get(0);
         boxY1.remove(0);
      } else {
         position1.y = defBoxY1;
      }
      ObstacleBox box = new ObstacleBox(position0, position1);
      // Bounciness
      if (boxBounciness.size() > 0) {
         box.bounciness = boxBounciness.get(0);
         boxBounciness.remove(0);
      } else {
         box.bounciness = defBoxBounciness;
      }

      return box;
   }
   // endregion

   // region Zones
   private static void defineZones() {
      ArrayList<Zone> zones = new ArrayList<>();

      zones.addAll(createZones());

      terrain.zones = zones.toArray(new Zone[0]);
   }

   private static ArrayList<Zone> createZones() {
      ArrayList<Zone> sandZones = new ArrayList<>();
      while (hasSandZone()) {
         sandZones.add(createSandZone());
      }
      return sandZones;
   }

   private static boolean hasSandZone() {
      return sandKineticFriction.size() > 0 || sandStaticFriction.size() > 0 || sandZoneX0.size() > 0
            || sandZoneX1.size() > 0 || sandZoneY0.size() > 0 || sandZoneY1.size() > 0;
   }

   private static Zone createSandZone() {
      Zone zone = new Zone();
      // Position
      Vector2 position0 = new Vector2();
      Vector2 position1 = new Vector2();
      if (sandZoneX0.size() > 0) {
         position0.x = sandZoneX0.get(0);
         sandZoneX0.remove(0);
      } else {
         position0.x = defsandZoneX0;
      }
      if (sandZoneY0.size() > 0) {
         position0.y = sandZoneY0.get(0);
         sandZoneY0.remove(0);
      } else {
         position0.y = defsandZoneY0;
      }
      if (sandZoneX1.size() > 0) {
         position1.x = sandZoneX1.get(0);
         sandZoneX1.remove(0);
      } else {
         position1.x = defsandZoneX1;
      }
      if (sandZoneY1.size() > 0) {
         position1.y = sandZoneY1.get(0);
         sandZoneY1.remove(0);
      } else {
         position1.y = defsandZoneY1;
      }
      // Friction
      if (sandKineticFriction.size() > 0) {
         zone.kineticFriction = sandKineticFriction.get(0);
         sandKineticFriction.remove(0);
      } else {
         zone.kineticFriction = defsandKineticFriction;
      }
      if (sandStaticFriction.size() > 0) {
         zone.staticFriction = sandStaticFriction.get(0);
         sandStaticFriction.remove(0);
      } else {
         zone.staticFriction = defsandStaticFriction;
      }
      zone.bottomLeftCorner = position0;
      zone.topRightCorner = position1;
      return zone;
   }
   // endregion
   // endregion

   // region Create Engine
   private static PhysicsEngine createEngine() {
      IODESolver savedSolver;
      IStoppingCondition savedCondition;
      ICollisionSystem savedCollisionSystem;

      if (ODEsolver == null) {
         savedSolver = defODEsolver;
      } else {
         savedSolver = ODEsolver;
      }
      if (stoppingCondition == null) {
         savedCondition = defstoppingCondition;
      } else {
         savedCondition = stoppingCondition;
      }
      if (collisionSystem == null) {
         savedCollisionSystem = defcollisionSystem;
      } else {
         savedCollisionSystem = collisionSystem;
      }

      return new PhysicsEngine(savedSolver, savedCondition, savedCollisionSystem);
   }

   private static Ball createBall(){
      Ball newBall = new Ball(terrain.ballStartingPosition, Vector2.zeroVector());
      newBall.radius = ballRadius;
      return newBall;
   }
   // endregion
}