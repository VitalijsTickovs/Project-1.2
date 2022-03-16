package Data_storage;

public class Ball {
    public BallState state;
    public double radius;
    public double mass;
    public boolean ballStopped;

    public Ball(Vector2 startPosition, Vector2 startVelocity) {
        state = new BallState(startPosition, startVelocity);
        mass = 1;
    }

    public void addForce(Vector2 force) {
        Vector2 velocityChange = force.scale(1 / mass);
        state.velocity.translate(velocityChange);
        ballStopped = false;
    }

    public double getZCoordinate(Terrain terrain){
        return terrain.terrainFunction.valueAt(state.position.x, state.position.y);
    }
}
