package physics;

import function.Function;
import physics.collisionsystems.StopCollisionSystem;
import physics.solvers.EulerSolver;
import physics.solvers.RungeKutta4Solver;
import physics.stoppingconditions.SmallVelocityStoppingCondition;
import utility.math.Vector2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import datastorage.Ball;
import datastorage.Terrain;

public class EngineTester {
    public void testStepSizeAccuracy(PhysicsEngine engine, double minH, double maxH, int numStepSizes, double stopT, Vector2 v0, Vector2 p0, Terrain terrain) {
        try {
            File f = new File(System.getProperty("user.dir")+"/Phase 1/src/physics/results/"+engine.odeSolver.getSolverName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);

            Ball ball = new Ball(p0, Vector2.zeroVector());
            // Approximate the actual value using a solution with a very low step size
            engine.odeSolver.setStepSize(minH / 10);
            ArrayList<Vector2> actualPositions = engine.simulateShot(v0, ball, terrain);

            // Test all the other engines
            for (double h=minH; h<maxH; h+=(maxH-minH)/numStepSizes) {
                engine.odeSolver.setStepSize(h);
                ArrayList<Vector2> shotPositions = engine.simulateShot(v0, ball, terrain);
                double t = 0;
                Vector2 position = null;
                Vector2 actualPosition = null;
                // Find the simulated value
                for (int i=0; i<shotPositions.size(); i++) {
                    t = i*h;
                    if (t >= stopT) {
                        position = shotPositions.get(i);
                        break;
                    }
                }
                // Find the actual value
                double tActual = 0;
                for (int i=0; i<actualPositions.size(); i++) {
                    tActual = i*minH/10;
                    if (tActual >= t) {
                        actualPosition = actualPositions.get(i);
                        break;
                    }
                }
                // Calculate the error
                double error = actualPosition.distanceTo(position);
                // Store the error
                fw.append(h+", "+Math.log10(h)+", "+error+", "+Math.log10(error)+"\n");
            }

            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        PhysicsEngine engine = new PhysicsEngine(new RungeKutta4Solver(0.01), new SmallVelocityStoppingCondition(), new StopCollisionSystem());
        EngineTester et = new EngineTester();
        et.testStepSizeAccuracy(
                engine,
                0.0001,
                0.01,
                1000,
                0.2,
                new Vector2(3, 0),
                new Vector2(-1, -0.5),
                new Terrain(
                        "e**(-(x*x + y*y)/40)",
                        0.2,
                        0.1,
                        new Vector2(-50, -50),
                        new Vector2(50, 50)
                )
        );
    }
}
