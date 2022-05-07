package Physics;

import Data_storage.*;

public class RungeKutta extends EulerIntegration {

    @Override
    protected BallState countNewBallState(Ball ball, Terrain terrain) {
        /*BallState newState = ball.state.copy();
        newState.position = countNewPosition(ball.state);
        newState.velocity = countNewVelocity(ball);

        return newState;*/
        //BallState k1 = eulerStep(ball.state, h);
        //BallState k2 = eulerStep(k1, h/2);

        return handleCollisions(ball.state, rk4(ball.state, terrain), terrain);//rk(ball.state);
        //return rk4(ball.state);
    }

    public BallState rk4(BallState state, Terrain terrain) {

        UpdateVector k1 = F(eulerStep(state, F(state, terrain), 0, true, terrain), terrain);
        UpdateVector k2 = F(eulerStep(state, k1, h/2, true, terrain), terrain);
        UpdateVector k3 = F(eulerStep(state, k2, h/2, true, terrain), terrain);
        UpdateVector k4 = F(eulerStep(state, k3, h, true, terrain), terrain);

        /*UpdateVector ak1 = F(state);
        UpdateVector ak2 = F(eulerStep(state, ak1, h/2, false));
        UpdateVector ak3 = F(eulerStep(state, ak2, h/2, false));
        UpdateVector ak4 = F(eulerStep(state, ak3, h, false));*/

        UpdateVector k = k1.copy();
        k = k.translate(k2.copy().scale(2));
        k = k.translate(k3.copy().scale(2));
        k = k.translate(k4);

        // Enforce stopping condition
        if (
                k1.velocity.length() == 0 && k1.acceleration.length() == 0 ||
                        k2.velocity.length() == 0 && k2.acceleration.length() == 0 ||
                        k3.velocity.length() == 0 && k3.acceleration.length() == 0 ||
                        k4.velocity.length() == 0 && k4.acceleration.length() == 0
        ) {
            //System.out.println("Stopping condition in one of the k-s");
            //k.velocity = Vector2.zeroVector.copy();
            k.acceleration = Vector2.zeroVector.copy();
            state.velocity = Vector2.zeroVector.copy();
        }

        /*k.velocity = ak1.velocity.copy();
        k.velocity.translate(ak2.velocity.copy().scale(2));
        k.velocity.translate(ak3.velocity.copy().scale(2));
        k.velocity.translate(ak4.velocity);*/

        /*System.out.println("k1: v="+k1.velocity+" a="+k1.acceleration);
        System.out.println("k2: v="+k2.velocity+" a="+k2.acceleration);
        System.out.println("k3: v="+k3.velocity+" a="+k3.acceleration);
        System.out.println("k4: v="+k4.velocity+" a="+k4.acceleration);
        System.out.println("k: v="+k.velocity+" a="+k.acceleration);*/

        return eulerStep(state, k, h/6, true, terrain);
    }

    public BallState rk2(BallState state, Terrain terrain) {
        UpdateVector k1 = F(state, terrain);
        UpdateVector k2 = F(eulerStep(state, k1, 2*h/3, true, terrain), terrain);

        UpdateVector k = k1.copy().scale(0.25).translate(k2.copy().scale(0.75));

        return eulerStep(state, k, h, true, terrain);
    }
}
