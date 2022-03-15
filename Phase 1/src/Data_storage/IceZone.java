package Data_storage;

public class IceZone extends Zone {
    private double staticFriction = 0.01;
    private double kineticFriction = 0.05;

    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKineticFriction() {
        return kineticFriction;
    }
}
