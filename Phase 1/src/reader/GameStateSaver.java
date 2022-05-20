package reader;

import java.io.*;

import datastorage.*;
import datastorage.obstacles.IObstacle;
import datastorage.obstacles.ObstacleBox;
import datastorage.obstacles.ObstacleTree;
import physics.*;

public class GameStateSaver {

   private static final String delimiter = ";";
   private static PrintWriter printWriter;

   /**
    * @param gameState
    * @param fileName  the name of the file, where the data will be saved.
    *                  If the file with that name does not exist, a new file will
    *                  be created
    * @return true, if the save has been committed successfully or false, if there
    *         was a problem
    */
   public static boolean saveGameState(GameState gameState, String fileName) {
      setupFileWriter(fileName);
      if (printWriter == null) {
         // If the file was not found, there was a problem that was already printed in
         // the console.
         return false;
      }
      saveDataIntoFile(gameState);
      printWriter.close();
      // Save was successful - return true
      return true;
   }

   public static void main(String[] args) {
      GameState state = GameStateLoader.readFile();
      saveGameState(state, "newFile");
   }

   private static void setupFileWriter(String fileName) {
      try {
         File file = new File(fileName + ".csv");
         FileWriter fileWriter = new FileWriter(file);
         printWriter = new PrintWriter(fileWriter);
      } catch (IOException e) {
         System.out.println("This is the name of the directory, choose a different name");
         e.printStackTrace();
      }
   }

   private static void saveDataIntoFile(GameState gameState) {
      savePhysicsEngine(gameState.getPhysicsEngine());
      saveBall(gameState.getBall());
      saveTerrain(gameState.getTerrain());
   }

   private static void savePhysicsEngine(PhysicsEngine physicsEngine) {
      printWriter.println("solverStep = " + physicsEngine.odeSolver.getStepSize() + delimiter);
      printWriter.println("solver = " + physicsEngine.odeSolver.getSolverName() + delimiter);
      printWriter.println("stoppingCondition = " + physicsEngine.stoppingCondition.getConditionName() + delimiter);
      printWriter.println("collisionSystem = " + physicsEngine.collisionSystem.getCollisionSystemName() + delimiter);
      printWriter.println("");
   }

   private static void saveBall(Ball ball) {
      printWriter.println("ballRadius = " + ball.radius + delimiter);
      printWriter.println("ballStartPointX = " + ball.state.position.x + delimiter);
      printWriter.println("ballStartPointY = " + ball.state.position.y + delimiter);

      printWriter.println("");
   }

   private static void saveTerrain(Terrain terrain) {

      printWriter.println("terrainX0 = " + terrain.topLeftCorner.x + delimiter);
      printWriter.println("terrainY0 = " + terrain.topLeftCorner.y + delimiter);
      printWriter.println("terrainX1 = " + terrain.bottomRightCorner.x + delimiter);
      printWriter.println("terrainY1 = " + terrain.bottomRightCorner.y + delimiter);
      printWriter.println("");
      
      printWriter.println("greenStaticFriction = " + terrain.staticFriction + delimiter);
      printWriter.println("greenKineticFriction = " + terrain.kineticFriction + delimiter);
      printWriter.println("terrainFunction = " + terrain.getTerrainFunction().f.getString() + delimiter);
      printWriter.println("");

      printWriter.println("targetRadius = " + terrain.target.radius + delimiter);
      printWriter.println("targetX = " + terrain.target.position.x + delimiter);
      printWriter.println("targetY = " + terrain.target.position.y + delimiter);
      printWriter.println("");

      addSandZones(terrain);
      addTrees(terrain);
      addBoxes(terrain);
   }
   private static void addSandZones(Terrain terrain){
      for (Zone zone : terrain.zones) {
         printWriter.println("sandZoneX = " + zone.bottomLeftCorner.x + "<" + zone.topRightCorner.x + delimiter);
         printWriter.println("sandZoneY = " + zone.bottomLeftCorner.y + "<" + zone.topRightCorner.y + delimiter);
         printWriter.println("sandKineticFriction = " + zone.kineticFriction + delimiter);
         printWriter.println("sandStaticFriction = " + zone.staticFriction + delimiter);
   
         printWriter.println("");
         
      }
      
   }
   private static void addTrees(Terrain terrain){
      for (IObstacle obstacle : terrain.obstacles) {
         if (obstacle instanceof ObstacleTree) {
            ObstacleTree tree = (ObstacleTree) obstacle;
            printWriter.println("treeX = " + tree.originPosition.x + delimiter);
            printWriter.println("treeY = " + tree.originPosition.x + delimiter);
            printWriter.println("treeRadius = " + tree.radius + delimiter);
            printWriter.println("treeBounciness = " + tree.bounciness + delimiter);
            printWriter.println("");

         }
      }
   }
   private static void addBoxes(Terrain terrain){
      for (IObstacle obstacle : terrain.obstacles) {
         if (obstacle instanceof ObstacleBox) {
            ObstacleBox box = (ObstacleBox) obstacle;
            printWriter.println("boxX = " + box.bottomLeftCorner.x + "<" + box.topRightCorner.x + delimiter);
            printWriter.println("boxY = " + box.bottomLeftCorner.y + "<" + box.topRightCorner.y + delimiter);
            printWriter.println("boxBounciness = " + box.bounciness + delimiter);
            printWriter.println("");

         }
      }
   }
}