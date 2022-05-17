package physics;

import java.util.ArrayList;

import datastorage.*;
import physics.collisionsystems.ICollisionSystem;
import physics.solvers.IODESolver;
import physics.stoppingconditions.IStoppingCondition;
import utility.math.Vector2;

public class PhysicsEngine {

    public final double G = 9.81; // Gravitational constant

    public final IODESolver odeSolver;
    public final IStoppingCondition stoppingCondition;
    public final ICollisionSystem collisionSystem;

    /**
     * Constructor. Creates a new instance of the physics engine
     * @param odeSolver The ODE solver to use
     * @param stoppingCondition The stopping condition to use
     * @param collisionSystem The collision system to use
     */
    public PhysicsEngine(IODESolver odeSolver, IStoppingCondition stoppingCondition, ICollisionSystem collisionSystem) {
        this.odeSolver = odeSolver;
        this.stoppingCondition = stoppingCondition;
        this.collisionSystem = collisionSystem;
    }

    /**
     * Simulates a shot and stores the positions until the ball stops
     * @param initialSpeed The initial speed of the ball
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

        while (tempBall.state.velocity.length() != 0) {
            // Clamp the velocity to the ball max speed
            if (tempBall.state.velocity.length() > Ball.maxSpeed) {
                tempBall.state.velocity.normalize().scale(Ball.maxSpeed);
            }
            // Store the previous state
            BallState prevState = tempBall.state.copy();
            // Perform a single step using the ODE solver
            tempBall.state = odeSolver.calculateNewBallState(tempBall.state, terrain, this);
            // Perform collisions
            tempBall.state = collisionSystem.modifyStateDueToCollisions(tempBall.state, prevState, ball.radius, terrain);
            // Check if velocity should be 0
            if (stoppingCondition.shouldStop(tempBall.state, prevState, odeSolver.getStepSize())) {
                Vector2 slope = new Vector2(
                        terrain.xDerivativeAt(tempBall.state.position),
                        terrain.yDerivativeAt(tempBall.state.position)
                );
                // If the static friction is smaller than the slope then set the velocity to the slope
                if (terrain.getStaticFriction(tempBall.state.position) < slope.length()) {
                    tempBall.state.velocity = slope.copy().reversed();
                } else {
                    // Otherwise, make the velocity 0
                    tempBall.state.velocity = Vector2.zeroVector();
                }
            }
            // Check if in water, and if so, stop
            if (tempBall.getZCoordinate(terrain) < 0) {
                tempBall.state.velocity = Vector2.zeroVector();
            }
            // Store the new position
            coordinates.add(tempBall.state.position.copy());
        }

        return coordinates;
    }

    /**
     * Gets the x-acceleration
     * @param state The ball state to calculate the acceleration for
     * @param terrain The terrain to calculate the acceleration on
     * @return The x-acceleration value
     */
    public double xAcceleration(BallState state, Terrain terrain) {
        double friction = terrain.getKineticFriction(state.position);
        Vector2 slope = new Vector2(
                terrain.xDerivativeAt(state.position),
                terrain.yDerivativeAt(state.position)
        );
        double downHillForce = -G * slope.x;
        double frictionForce = G * friction * state.velocity.x / state.velocity.length();
        return (downHillForce - frictionForce);
    }

    /**
     * Gets the x-acceleration
     * @param state The ball state to calculate the acceleration for
     * @param terrain The terrain to calculate the acceleration on
     * @return The x-acceleration value
     */
    public double yAcceleration(BallState state, Terrain terrain) {
        double friction = terrain.getKineticFriction(state.position);
        Vector2 slope = new Vector2(
                terrain.xDerivativeAt(state.position),
                terrain.yDerivativeAt(state.position)
        );
        double downHillForce = -G * slope.y;
        double frictionForce = G * friction * state.velocity.y / state.velocity.length();
        return (downHillForce - frictionForce);
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
    /*public static void testEngine(PhysicsEngine engine, double minH, double maxH, int numTests, Vector2 expectedResult, Vector2 ballStart, Vector2 initialSpeed, Terrain terrain) {
        try {
            File f = new File("src/physics/results/results-"+engine.getClass().getName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            Ball ball = new Ball(ballStart.copy(), Vector2.zeroVector());
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
    }*/

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
     * @param hStart The smallest step size to use
     * @param hEnd The largest step size to use
     * @param hNum The total number of step sizes to test
     * @param numSteps The number of steps to take for simulation
     * @param p0 The starting position of the ball
     * @param v0 The initial speed of the ball
     * @param xActual The string representations of the actual x function parts
     * @param yActual The string representations of the actual y function parts
     * @param tSplit The values of t for which the functions get split (must be in ascending order)
     * @param a The constant acceleration of the system
     * @param terrain The terrain to use
     */
    /*public static void testEngine(PhysicsEngine engine, double hStart, double hEnd, double hNum, int numSteps, Vector2 p0, Vector2 v0, String xActual[], String yActual[], double[] tSplit, Vector2 a, Terrain terrain) {
        if (xActual.length != yActual.length || xActual.length != tSplit.length+1) {
            throw new RuntimeException("Wrong number of functions or t splits.");
        }
        try {
            File f = new File("Phase 1/src/Physics/results/results-"+engine.getClass().getName()+"-"+System.nanoTime()+".csv");
            FileWriter fw = new FileWriter(f);
            for (double h = hStart; h<=hEnd; h+=(hEnd-hStart)/hNum) {
                Ball ball = new Ball(p0.copy(), Vector2.zeroVector());
                engine.h = h;
                Function[] yAs = new Function[yActual.length];
                Function[] xAs = new Function[xActual.length];
                for (int i=0; i<yActual.length; i++) {
                    xAs[i] = new Function(xActual[i]);
                    yAs[i] = new Function(yActual[i]);
                }
                ArrayList<Vector2> positions = engine.simulateShot(v0.copy(), ball, terrain);
                for (int i=0; i<positions.size(); i++) {
                    double t = h * i;
                    if (i == numSteps) {
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

                        System.out.println(x);

                        fw.append(h + ", " + xValActual + ", " + yValActual + ", " + x + ", " + y + ", " + error + "\n");

                        break;
                    }
                }
            }
            /*Vector2 result = positions.get(positions.size()-1);
            double error = expectedResult.copy().translate(result.copy().scale(-1)).length();
            fw.append(h+", "+error+", "+Math.log10(h)+", "+Math.log10(error)+"\n");
            ball.state.position = ballStart.copy();
            System.out.println(h+", x="+result.x+", y="+result.y);*/
            /*fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public static void main(String[] args) {
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
                0.0001,
                0.1,
                1000,
                10,
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
    //}
}
