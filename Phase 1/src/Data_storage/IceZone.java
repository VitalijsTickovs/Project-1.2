package Data_storage;

public class IceZone extends Zone {
    private double staticFriction = 0.01;
    private double kinematicFriction = 0.05;

    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKinematicFriction() {
        return kinematicFriction;
    }
}
