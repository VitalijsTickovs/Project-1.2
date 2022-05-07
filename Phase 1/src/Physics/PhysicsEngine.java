package Physics;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import Data_storage.*;
import function.Function;

public abstract class PhysicsEngine {

    public boolean testing = false;

    public double h = 0.01; // The step of the Euler's method
    public final double G = 9.81;

    /**
     * Simulates a shot and stores the positions until the ball stops
     * @param initialSpeed The inital speed of the ball
     * @param ball The ball to shoot
     * @param terrain The terrain to shoot the ball on
     * @return ArrayList containing ball positions throughout the shot
     */
    public ArrayList<Vector2> simulateShot(Vector2 initialSpeed, Ball ball, Terrain terrain) {
        Ball tempBall = ball.copy();
        ArrayList<Vector2> coordinates = new ArrayList<Vector2>();
        tempBall.state.velocity = initialSpeed.copy();
        // Add the initial position
        coordinates.add(tempBall.state.position.copy());
        do {
            tempBall.state = countNewBallState(tempBall, terrain);
            coordinates.add(tempBall.state.position.copy());
            //System.out.println("p="+ball.state.position+" v="+ball.state.velocity);
            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        } while (
                tempBall.state.velocity.length() != 0 /*||
            new Vector2(
                getXSlopeAt(ball.state.position.x, ball.state.position.y),
                getYSlopeAt(ball.state.position.y, ball.state.position.y)
            ).length() > getStaticFrictionAtPosition(ball.state.position)*/
        );
        return coordinates;
    }

    /**
     * Creates the new state of the ball after an iteration step
     * @param ball The ball to calculate the new state for
     * @return The new state of the ball
     */
    protected abstract BallState countNewBallState(Ball ball, Terrain terrain);

    /**
     * Checks if a position is colliding with an obstacle
     * @param position The position to check for
     * @return {@code true} if colliding and {@code false} otherwise
     */
    protected boolean isTouchingAnObstacle(Vector2 position, Terrain terrain){
        for (IObstacle obstacle : terrain.obstacles) {
            if (obstacle.isPositionColliding(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the x derivative at a given position
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The derivative value
     */
    double getXSlopeAt(double x, double y, Terrain terrain) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.xDerivativeAt(x, y);
        }
    }

    /**
     * Gets the y derivative at a given position
     * @param x The x coordinate
     * @param y THe y coordinate
     * @return The derivative value
     */
    protected double getYSlopeAt(double x, double y, Terrain terrain) {
        double functionValue = terrain.terrainFunction.valueAt(x, y);
        if (functionValue > 10 || functionValue < -10) {
            return 0;
        } else {
            return terrain.terrainFunction.yDerivativeAt(x, y);
        }
    }

    /**
     * Gets the kinetic friction at a given position
     * @param position The position to check
     * @return The kinetic friction value
     */
    protected double getKineticFrictionAtPosition(Vector2 position, Terrain terrain) {
        double friction = terrain.kineticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.kineticFriction;
            }
        }
        return friction;
    }

    /**
     * Gets the static friction at a given position
     * @param position The position to check
     * @return The static friction value
     */
    protected double getStaticFrictionAtPosition(Vector2 position, Terrain terrain) {
        double friction = terrain.staticFriction;
        for (Zone zone : terrain.zones) {
            if (zone.isPositionInside(position)) {
                friction = zone.staticFriction;
            }
        }
        return friction;
    }

    /**
     * Gets the x-acceleration
     * @param slope The terrain derivative vector
     * @param speed The current velocity vector
     * @param friction The friction to use
     * @return The x-acceleration value
     */
    protected double xAcceleration(Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction * speed.x / speed.length();
        return (downHillForce - frictionForce);
    }

    /**
     * Gets the y-acceleration
     * @param slope The terrain derivative vector
     * @param speed The current velocity vector
     * @param friction The friction to use
     * @return The y-acceleration value
     */
    protected double yAcceleration(Vector2 slope, Vector2 speed, double friction) {
        double downHillForce = -G * slope.y;
        double frictionForce = G * friction * speed.y / speed.length();
        return (downHillForce - frictionForce);
    }

    protected BallState handleCollisions(BallState oldState, BallState newState, Terrain terrain) {
        BallState state = newState.copy();
        // Check for collision
        if (isTouchingAnObstacle(state.position, terrain)) {
            state.position = oldState.position.copy();
            //System.out.println("1");
            state.velocity = Vector2.zeroVector.copy();
        }
        // Check for out of bounds
        // On x-axis
        boolean reverseX = false;
        if (state.position.x > terrain.limitingCorner.x || state.position.x < terrain.startingCorner.x) {
            reverseX = true;
            state.position = oldState.position.copy();
            //state.velocity = Vector2.zeroVector.copy();
            //System.out.println("2");
        }
        // On y-axis
        boolean reverseY = false;
        if (state.position.y > terrain.limitingCorner.y || state.position.y < terrain.startingCorner.y) {
            reverseY = true;
            state.position = oldState.position.copy();
            //state.velocity = Vector2.zeroVector.copy();
            //System.out.println("3");
        }
        // Reverse the velocity if needed
        if (reverseX) {
            state.velocity.x = -state.velocity.x;
            state.velocity.scale(0.5);
        }
        if (reverseY) {
            state.velocity.y = -state.velocity.y;
            state.velocity.scale(0.5);
        }
        return state;
    }

    /**
     * Tests a physics engine
     * Creates a .csv file storing lines in the form:
     *     - h, error, log10(h), log10(error)
     * @param engine The engine to test
     * @param minH The starting h (step size)
     * @param maxH The final h (step size)
     * @param numTests The number of tests to do between minH and maxH
     * @param expectedResult The expected final position of the shot
     * @param ballStart The starting position of the ball
     * @param initialSpeed The initial velocity of the ball
     */
    public static void testEngine(PhysicsEngine engine, double minH, double maxH, int numTests, Vector2 expectedResult, Vector2 ballStart, Vector2 initialSpeed, Terrain terrain) {
        try {
            File f = new File("src/physics/results/results-"+engine.getClass().getName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            Ball ball = new Ball(ballStart.copy(), Vector2.zeroVector.copy());
            for (double h=minH; h<maxH; h+=(maxH-minH)/numTests) {
                engine.h = h;
                ArrayList<Vector2> positions = engine.simulateShot(initialSpeed.copy(), ball, terrain);
                Vector2 result = positions.get(positions.size()-1);
                double error = expectedResult.copy().translate(result.copy().scale(-1)).length();
                fw.append(h+", "+error+", "+Math.log10(h)+", "+Math.log10(error)+"\n");
                ball.state.position = ballStart.copy();
                System.out.println(h+", x="+result.x+", y="+result.y);
            }
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests a physics engine for a given step size.
     * This can only be used if the acceleration is constant.
     * Creates a .csv file storing lines in the form:
     *     - t, xA(t), yA(t), x(t), y(t)
     *
     * - xA(t) -> The actual value of x at time t
     * - yA(t) -> The actual value of y at time t
     * - x(t) -> The found value of x at time t
     * - y(t) -> The found value of y at time t
     *
     * Example:
     *     - xA(t) = xActual[0] if t >= 0 & t < tSplit[0]
     *               xActual[1] if t >= tSplit[0] & t < tSplit[1]
     *               ...
     *               xActual[n] if t >= tSplit[n-1]
     *
     * <Same for yA(t)>
     *
     * @param engine The engine to test
     * @param h The step size to be used
     * @param p0 The starting position of the ball
     * @param v0 The initial speed of the ball
     * @param xActual The string representations of the actual x function parts
     * @param yActual The string representations of the actual y function parts
     * @param tSplit The values of t for which the functions get split (must be in ascending order)
     * @param a The constant acceleration of the system
     */
    public static void testEngine(PhysicsEngine engine, double h, Vector2 p0, Vector2 v0, String xActual[], String yActual[], double[] tSplit, Vector2 a, Terrain terrain) {
        if (xActual.length != yActual.length || xActual.length != tSplit.length+1) {
            throw new RuntimeException("Wrong number of functions or t splits.");
        }
        try {
            File f = new File("src/physics/results/results-"+engine.getClass().getName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            Ball ball = new Ball(p0.copy(), Vector2.zeroVector.copy());
            engine.h = h;
            Function[] yAs = new Function[yActual.length];
            Function[] xAs = new Function[xActual.length];
            for (int i=0; i<yActual.length; i++) {
                xAs[i] = new Function(xActual[i]);
                yAs[i] = new Function(yActual[i]);
            }
            ArrayList<Vector2> positions = engine.simulateShot(v0.copy(), ball, terrain);
            for (int i=0; i<positions.size(); i++) {
                Vector2 position = positions.get(i);
                double t = h*i;
                double x = position.x;
                double y = position.y;
                // Find where the t belongs in tSplit
                int pos = 0;
                for (int j=1; j<=tSplit.length; j++) {
                    if (tSplit[pos] > t) {
                        break;
                    }
                    pos = j;
                }
                Function yA = yAs[pos];
                Function xA = xAs[pos];

                double xValActual = xA.evaluate(new String[] {"t", "vx0", "ax", "x0"}, new double[] {t, v0.x, a.x, p0.x});
                double yValActual = yA.evaluate(new String[] {"t", "vy0", "ay", "y0"}, new double[] {t, v0.y, a.y, p0.y});

                fw.append(t+", "+xValActual+", "+yValActual+", "+x+", "+y+"\n");
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
        PhysicsEngine rk = new RungeKutta();
        PhysicsEngine ei =  new EulerIntegration();
        //rk.terrain = new Terrain("0", 0.2, 0.1, new Vector2(-50, -50), new Vector2(50, 50));
        //ei.terrain = rk.terrain;
        //rk.h = 0.09060939999999999;//0.05;//0.0493507;
        //rk.simulateShot(new Vector2(2, 0), new Ball(new Vector2(0, 0), Vector2.zeroVector.copy()));
        //PhysicsEngine.testEngine(ei, 0.0001, 0.1, 1000, new Vector2(0.5096839959, 0), new Vector2(0, 0), new Vector2(1, 0));
        //PhysicsEngine.testEngine(rk, 0.0001, 0.1, 1000, new Vector2(0.5096839959, 0), new Vector2(0, 0), new Vector2(1, 0));

        PhysicsEngine.testEngine(
                rk,
                0.05,
                new Vector2(0, 0),
                new Vector2(1, 0),
                new String[] {"x0 + t*(2*vx0 + ax*t)/2", "0.5096839959"},
                new String[] {"0", "0"},
                new double[] {1.019367992},
                new Vector2(-0.1*9.81, 0),
                new Terrain("0", 0.2, 0.1, new Vector2(-50, -50), new Vector2(50, 50))
        );

        //rk.terrain = new Terrain("0.1*x+1", 0.2, 0.05, new Vector2(-50, -50), new Vector2(50, 50));
        //PhysicsEngine.testEngine(rk, 0.0001, 0.1, 1000, new Vector2(1.359157322, 0), new Vector2(0, 0), new Vector2(2, 0));
        /*for (double h=0.001; h<1; h+=0.001) {
            System.out.println("h="+h);
            rk.h = h;
            rk.simulateShot(new Vector2(1, 0), new Ball(new Vector2(0, 0), Vector2.zeroVector.copy()));
        }*/
        //rk.simulateShot(new Vector2(2, 0), new Ball(new Vector2(0, 0), Vector2.zeroVector.copy()));
    }
}
