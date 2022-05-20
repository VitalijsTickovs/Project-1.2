package bot;

import bot.botimplementations.Bot;
import bot.botimplementations.HillClimbingBot;
import bot.heuristics.FinalEuclidianDistanceHeuristic;
import datastorage.GameState;
import datastorage.Target;
import datastorage.Terrain;
import physics.PhysicsEngine;
import physics.collisionsystems.StopCollisionSystem;
import physics.solvers.RungeKutta4Solver;
import physics.stoppingconditions.SmallVelocityStoppingCondition;
import utility.math.Vector2;
import datastorage.Ball;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class BotTester {

    public String testBot(Bot bot, GameState gameState, int numShots) {
        String data = "";
        int numHolesInOne = 0;
        // Test a bot multiplte times with the same shot
        for (int i=0; i<numShots; i++) {
            System.out.println("Iteration "+(i+1)+"...");
            Vector2 velocity = bot.findBestShot(gameState);
            ArrayList<Vector2> positions = gameState.simulateShot(velocity);
            Vector2 finalPosition = positions.get(positions.size()-1);
            double distance = finalPosition.distanceTo(gameState.getTerrain().target.position);
            boolean holeInOne = distance <= gameState.getTerrain().target.radius;
            if (holeInOne) {
                numHolesInOne++;
            }
            data += distance+"\n";
            System.out.println("Done!");
        }
        data += numHolesInOne/(double) numShots;
        return data;
    }

    public void storeData(String data, String fileName) {
        try {
            File f = new File(System.getProperty("user.dir")+"/Phase 1/src/bot/results/"+fileName+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            fw.append(data);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Terrain terrain = new Terrain(
                "0.4*(0.9-e**(-(x*x+y*y)/8))",
                0.2,
                0.08,
                new Vector2(-50, -50),
                new Vector2(50, 50)
        );
        terrain.target = new Target();
        terrain.target.position = new Vector2(4, 1);
        terrain.target.radius = 0.15;
        GameState gameState = new GameState(
                terrain,
                new Ball(new Vector2(-3, 0), Vector2.zeroVector()),
                new PhysicsEngine(
                        new RungeKutta4Solver(0.01),
                        new SmallVelocityStoppingCondition(),
                        new StopCollisionSystem()
                )
        );
        Bot bot = new HillClimbingBot(
                new FinalEuclidianDistanceHeuristic(),
                0.01,
                8,
                null
        );
        BotTester bt = new BotTester();
        bt.storeData(bt.testBot(bot, gameState, 100), "SimpleHillClimbing");
    }

}
