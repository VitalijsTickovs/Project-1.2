package Data_storage;

public class SandZone extends Zone {

    public double staticFriction = 0.4;
    public double kineticFriction = 0.3;

    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKinematicFriction() {
        return kineticFriction;
    }
}