package Data_storage;

public class CollisionData {
    public Vector2 collisionNormal;
    public Vector2 collisionPosition;
    public double bounciness;

    @Override
    public String toString(){
        return "Normal: " + collisionNormal + ", position: " + collisionPosition + ", bounciness: " + bounciness;
    }
}
