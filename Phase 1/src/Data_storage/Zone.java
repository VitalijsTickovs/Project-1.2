package Data_storage;

public abstract class Zone extends Rectangle {
    public abstract double getStaticFriction();

    public abstract double getKineticFriction();

    public void print(){
        System.out.print("Down left corner: ");
        System.out.println(downLeftCorner);
        System.out.print("Top right corner: ");
        System.out.println(topRightCorner);
        System.out.print("Static friction");
        System.out.println(getStaticFriction());
        System.out.print("Kinetic friction");
        System.out.println(getKineticFriction());
    }
}
