package Physics;

import Data_storage.Ball;
import Data_storage.Terrain;
import Data_storage.Vector2;
import function.Function;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class EngineTester {
    public void testStepSizeAccuracy(PhysicsEngine engine, Vector2 stepSizeRange, Vector2 p0,
                                     Vector2 v0, String xActual[], String yActual[], double[] tSplit, Vector2 a,
                                     Terrain terrain, double stopT) {
        try {
            File f = new File(System.getProperty("user.dir") + "/Phase 1/src/Physics/Results/"+engine.odeSolver.getClass().getName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            for (double h=stepSizeRange.x; h<=stepSizeRange.y; h+=stepSizeRange.x) {
                engine.odeSolver.setStepSize(h);
                Ball ball = new Ball(p0.copy(), Vector2.zeroVector());
                Function[] yAs = new Function[yActual.length];
                Function[] xAs = new Function[xActual.length];
                for (int i=0; i<yActual.length; i++) {
                    xAs[i] = new Function(xActual[i]);
                    yAs[i] = new Function(yActual[i]);
                }
                ArrayList<Vector2> positions = engine.simulateShot(v0.copy(), ball, terrain);
                for (int i=0; i<positions.size(); i++) {
                    double t = h * i;
                    if (t >= stopT) {
                        Vector2 position = positions.get(i);
                        double x = position.x;
                        double y = position.y;
                        // Find where the t belongs in tSplit
                        int pos = 0;
                        for (int j = 1; j <= tSplit.length; j++) {
                            if (tSplit[pos] > t) {
                                break;
                            }
                            pos = j;
                        }
                        Function yA = yAs[pos];
                        Function xA = xAs[pos];

                        double xValActual = xA.evaluate(new String[]{"t", "vx0", "ax", "x0"}, new double[]{t, v0.x, a.x, p0.x});
                        double yValActual = yA.evaluate(new String[]{"t", "vy0", "ay", "y0"}, new double[]{t, v0.y, a.y, p0.y});

                        double xError = xValActual - x;
                        double yError = yValActual - y;

                        double error = Math.sqrt(xError*xError + yError*yError);

                        fw.append(h + ", " + Math.log10(h) + "," + xValActual + ", " + yValActual + ", " + x + ", " + y + ", " + error + "," +Math.log10(error) +"\n");

                        break;
                    }
                }
            }
                /*Vector2 result = positions.get(positions.size()-1);
                double error = expectedResult.copy().translate(result.copy().scale(-1)).length();
                fw.append(h+", "+error+", "+Math.log10(h)+", "+Math.log10(error)+"\n");
                ball.state.position = ballStart.copy();
                System.out.println(h+", x="+result.x+", y="+result.y);*/
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PhysicsEngine engine = new PhysicsEngine(9.81, new RungeKutta4Solver(0.01), new SmallVelocityStoppingCondition(), new StopCollisionSystem());
        Terrain terrain = new Terrain("0", 0.2, 0.1, new Vector2(-50, -50), new Vector2(50, 50));
        EngineTester et = new EngineTester();
        et.testStepSizeAccuracy(
                engine,
                new Vector2(0.0001, 0.01),
                new Vector2(0, 0),
                new Vector2(5, 0),
                new String[] {"x0 + t*(2*vx0 + ax*t)/2"},
                new String[] {"0"},
                new double[] {},
                new Vector2(-0.1*9.81, 0),
                terrain,
                1
        );
    }
}
