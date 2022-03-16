package Data_storage;

public class Target {
    public Vector2 position;
    public double radius;

    public void print(){
        System.out.print("Target position: ");
        System.out.println(position);
        System.out.print("Radius: ");
        System.out.println(radius);
    }
    
    public void setPosition(Vector2 position){
        this.position = position;
    }
    
    public void setRadius(double radius){
        this.radius = radius;
    }
}
