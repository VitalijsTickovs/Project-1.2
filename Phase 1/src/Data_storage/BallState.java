package Data_storage;

public class BallState {
    public Vector2 position;
    public Vector2 velocity;
    public double mass;

    public BallState copy() {
        BallState b = new BallState();
        b.position = this.position;
        b.velocity = this.velocity;
        return b;
    }

    public void addForce(Vector2 force){
        Vector2 velocityChange = force.scale(1/mass);
        velocity.translate(velocityChange);
    }
}
