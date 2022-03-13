package Data_storage;

public class Ball {
    public BallState state;
    public double radius;
    public double mass;
    public boolean ballStopped;

    public void addForce(Vector2 force) {
        Vector2 velocityChange = force.scale(1 / mass);
        state.velocity.translate(velocityChange);
        ballStopped = false;
    }
}
