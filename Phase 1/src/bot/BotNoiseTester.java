package bot;

import bot.botimplementations.AdaptiveHillClimbingBot;
import bot.botimplementations.IBot;
import bot.botimplementations.RuleBasedBot;
import bot.heuristics.FinalClosestEuclidianDistanceHeuristic;
import datastorage.Ball;
import datastorage.GameState;
import datastorage.Target;
import datastorage.Terrain;
import physics.PhysicsEngine;
import physics.collisionsystems.BounceCollisionSystem;
import physics.solvers.RungeKutta4Solver;
import physics.stoppingconditions.SmallVelocityStoppingCondition;
import utility.math.Vector2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class BotNoiseTester {
    public void saveData(String data, String fileName) {
        try {
            File f = new File("Phase 1/src/bot/results/"+fileName+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            fw.append(data);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String testBotForNoise(IBot bot, GameState gameState, double minNoise, double maxNoise, int numTests) {
        String data = "";
        for (int i=0; i<numTests; i++) {
            double noise = minNoise + (maxNoise-minNoise)/numTests*i;
            Vector2 dataVect = testBotOneNoise(bot, gameState, numTests, noise);
            double averageHolesInOne = dataVect.x;
            double averageDistance = dataVect.y;
            data += noise+", "+averageHolesInOne+", "+averageDistance+"\n";
        }
        return data;
    }

    public Vector2 testBotOneNoise(IBot bot, GameState gameState, int numShots, double maxNoise) {
        ShotNoiseGenerator noiseGenerator = new ShotNoiseGenerator();
        double holesInOne = 0;
        double totalDistance = 0;
        for (int i=0; i<numShots; i++) {
            ArrayList<Vector2> positions = gameState.simulateShot(
                    noiseGenerator.addNoiseToShot(bot.findBestShot(gameState), maxNoise)
            );
            double distance = gameState.getTerrain().target.position.distanceTo(positions.get(positions.size()-1));
            boolean holeInOne = distance <= gameState.getTerrain().target.radius;

            if (holeInOne) {
                holesInOne++;
            }

            totalDistance += distance;
        }

        System.out.println("Holes in 1: "+(holesInOne/numShots)*100+"%");
        System.out.println("Average distance: "+(totalDistance/numShots));

        return new Vector2(holesInOne/numShots, totalDistance/numShots);
    }

    public static void main(String[] args) {
        IBot bot = new AdaptiveHillClimbingBot(new FinalClosestEuclidianDistanceHeuristic(), new RuleBasedBot());
        Terrain terrain = new Terrain(
                "0",
                0.2,
                0.1,
                new Vector2(-50, -50),
                new Vector2(50, 50)
        );
        terrain.target = new Target();
        terrain.target.setPosition(new Vector2(4, 1));
        terrain.target.setRadius(0.15);

        Ball ball = new Ball(new Vector2(-3, 0), Vector2.zeroVector());

        PhysicsEngine engine = new PhysicsEngine(
                new RungeKutta4Solver(0.01),
                new SmallVelocityStoppingCondition(),
                new BounceCollisionSystem()
        );

        GameState gameState = new GameState(
                terrain,
                ball,
                engine
        );

        BotNoiseTester bnt = new BotNoiseTester();

        bnt.saveData(bnt.testBotForNoise(bot, gameState, 0.001, 1, 100), "AdaptiveHC");
    }
}
