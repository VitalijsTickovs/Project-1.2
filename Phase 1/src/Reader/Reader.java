package Reader;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import Data_storage.IObstacle;
import Data_storage.ObstacleTree;
import Data_storage.SandZone;
import Data_storage.Target;
import Data_storage.Terrain;
import Data_storage.TerrainFunction1;
import Data_storage.Vector2;
import function.Function;

public class Reader {

   private static final String delimiter = ";";
   private static Scanner scanner;

   // region private Variables
   // Singular values
   private static double terrainX0;
   private static double terrainY0;
   private static double terrainX1;
   private static double terrainY1;
   private static double greenKineticFriction;
   private static double greenStaticFriction;
   private static String terrainFunction; // ask Niko for his implementation and leave it as a String for now

   private static double ballStartPointX;
   private static double ballStartPointY;

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

   private static Terrain terrain; // The generated terrain that will be returned
   // endregion

   // region default variable values
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
   private final static double defsandPitX0 = 0;
   private final static double defsandPitY0 = 0;
   private final static double defsandPitX1 = 1;
   private final static double defsandPitY1 = 1;
   private final static double defsandKineticFriction = 0.25;
   private final static double defsandStaticFriction = 0.4;

   private final static double deftreeX = 0.5;
   private final static double deftreeY = 0.5;
   private final static double deftreeRadius = 0.5;
   private final static double deftreeBounciness = 1;

   // endregion

   public static void main(String[] args) {
      String csvFile = "C:/Users/leahi/Documents/AAA-Maastricht class/Period 1.3/Project/UserInput.csv";
      Reader.readFile(csvFile);
   }

   public static Terrain readFile(String csvFile) {

      if (!createBufferedReader(csvFile)) {
         return null;
      }

      String[] allLinesSplit = splitLines(csvFile);
      readVariables(allLinesSplit);

      return saveDataIntoObject();
   }

   /**
    * Tries to create a buffered reader
    * 
    * @param csvFile
    * @return true, if the reader has been successfully created
    */
   private static boolean createBufferedReader(String csvFile) {
      try {
         File file = new File(csvFile);
         FileReader fr = new FileReader(file);
         scanner = new Scanner(fr);
         return true;

      } catch (FileNotFoundException e) {
         System.out.println("File not found");
         e.printStackTrace();
         return false;

      } catch (NullPointerException e) {
         System.out.println("File was null");
         e.printStackTrace();
         return false;
      }
   }

   private static String[] splitLines(String csvFile) {
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
      // Green
      if (line.contains("terrainX0")) {
         terrainX0 = readDouble(line);
      }
      if (line.contains("terrainY0")) {
         terrainY0 = readDouble(line);
      }
      if (line.contains("terrainX1")) {
         terrainX1 = readDouble(line);
      }
      if (line.contains("terrainY1")) {
         terrainY1 = readDouble(line);
      }
      if (line.contains("greenKineticFriction")) {
         terrainY1 = readDouble(line);
      }
      if (line.contains("greenStaticFriction")) {
         terrainY1 = readDouble(line);
      }
      if (line.contains("terrainFunction")) {
         terrainFunction = readString(line);
      }
      // Ball start point
      if (line.contains("ballStartPointX")) {
         ballStartPointX = readDouble(line);
      }
      if (line.contains("ballStartPointY")) {
         ballStartPointY = readDouble(line);
      }
      // Target
      if (line.contains("targetRadius")) {
         targetRadius = readDouble(line);
      }
      if (line.contains("targetX")) {
         targetX = readDouble(line);
      }
      if (line.contains("targetY")) {
         targetY = readDouble(line);
      }
      // Sand zone
      if (line.contains("sandZoneX")) {
         double[] range = readRange(line);
         sandZoneX0.add(range[0]);
         sandZoneX1.add(range[1]);
      }
      if (line.contains("sandZoneY")) {
         double[] range = readRange(line);
         sandZoneY0.add(range[0]);
         sandZoneY1.add(range[1]);
      }
      if (line.contains("sandKineticFriction")) {
         sandKineticFriction.add(readDouble(line));
      }
      if (line.contains("sandKineticFriction")) {
         sandStaticFriction.add(readDouble(line));
      }
      // Tree
      if (line.contains("treeX")) {
         treeX.add(readDouble(line));
      }
      if (line.contains("treeY")) {
         treeY.add(readDouble(line));
      }
      if (line.contains("treeRadius")) {
         treeRadius.add(readDouble(line));
      }
      if (line.contains("treeBounciness")) {
         treeBounciness.add(readDouble(line));
      }
   }

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
         e.printStackTrace();
         return 0;
      } catch (NumberFormatException e) {
         System.out.println("String after = was not parsable into a double");
         e.printStackTrace();
         return 0;
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         e.printStackTrace();
         return 0;
      }
   }

   private static String readString(String line) {
      try {
         return (line.substring(line.lastIndexOf("=") + 1));
      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         e.printStackTrace();
         return null;
      }
   }

   private static double[] readRange(String line) {
      try {
         String temp = line.substring(line.lastIndexOf("=") + 2);
         String[] split = temp.split("<");
         double[] range = new double[2];
         range[0] = readDouble(split[0]);
         range[1] = readDouble(split[2]);
         return range;

      } catch (IndexOutOfBoundsException e) {
         System.out.println("There was nothing after the = sign");
         e.printStackTrace();
         return null;
      } catch (Exception e) {
         System.out.println("Pattern syntax was invalid");
         e.printStackTrace();
         return null;
      }
   }

   // region Create Terrain
   private static Terrain saveDataIntoObject() {
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
         terrain.startingCorner = new Vector2(defx0, defy0);
      } else {
         terrain.startingCorner = new Vector2(terrainX0, terrainY0);
      }
      if (terrainX1 == 0 && terrainY1 == 0) {
         terrain.limitingCorner = new Vector2(defxt, defyt);
      } else {
         terrain.limitingCorner = new Vector2(terrainX1, terrainY1);
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
      if (terrainFunction == null) {
         terrain.terrainFunction = new TerrainFunction1(defterrainFunction);
      } else {
         terrain.terrainFunction = new TerrainFunction1(terrainFunction);
      }
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
         terrain.ballStartingPoisition = new Vector2(defballStartPointX, defballStartPointY);
      } else {
         terrain.ballStartingPoisition = new Vector2(ballStartPointX, ballStartPointY);
      }
   }

   // region Obstacles
   private static void defineObstacles() {
      ArrayList<IObstacle> obstacles = new ArrayList<>();

      obstacles.addAll(createTrees());

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
   // endregion

   // region Zones
   private static void defineZones() {
      ArrayList<SandZone> sandZones = new ArrayList<>();

      sandZones.addAll(createSandZones());

      terrain.zones = sandZones.toArray(new SandZone[0]);
   }

   private static ArrayList<SandZone> createSandZones() {
      ArrayList<SandZone> sandZones = new ArrayList<>();
      while (hasSandZone()) {
         sandZones.add(createSandZone());
      }
      return sandZones;
   }

   private static boolean hasSandZone() {
      return sandKineticFriction.size() > 0 || sandStaticFriction.size() > 0 || sandZoneX0.size() > 0
            || sandZoneX1.size() > 0 || sandZoneY0.size() > 0 || sandZoneY1.size() > 0;
   }

   private static SandZone createSandZone() {
      SandZone sandZone = new SandZone();
      // Position
      Vector2 position0 = new Vector2();
      Vector2 position1 = new Vector2();
      if (sandZoneX0.size() > 0) {
         position0.x = sandZoneX0.get(0);
         sandZoneX0.remove(0);
      } else {
         position0.x = defsandPitX0;
      }
      if (sandZoneY0.size() > 0) {
         position0.y = sandZoneY0.get(0);
         sandZoneY0.remove(0);
      } else {
         position0.y = defsandPitY0;
      }
      if (sandZoneX1.size() > 0) {
         position1.x = sandZoneX1.get(0);
         sandZoneX1.remove(0);
      } else {
         position1.x = defsandPitX1;
      }
      if (sandZoneY1.size() > 0) {
         position1.y = sandZoneY1.get(0);
         sandZoneY1.remove(0);
      } else {
         position1.y = defsandPitY1;
      }
      // Friction
      if (sandKineticFriction.size() > 0) {
         sandZone.kineticFriction = sandKineticFriction.get(0);
         sandKineticFriction.remove(0);
      } else {
         sandZone.kineticFriction = defsandKineticFriction;
      }
      if (sandStaticFriction.size() > 0) {
         sandZone.staticFriction = sandStaticFriction.get(0);
         sandStaticFriction.remove(0);
      } else {
         sandZone.staticFriction = defsandStaticFriction;
      }
      sandZone.downLeftCorner = position0;
      sandZone.topRightCorner = position1;
      return sandZone;
   }
   // endregion
   // endregion
}