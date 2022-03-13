package Data_storage;

public class BallState {
    public Vector2 position;
    public Vector2 velocity;

    public BallState copy() {
        BallState b = new BallState();
        b.position = this.position;
        b.velocity = this.velocity;
        return b;
    }
}
