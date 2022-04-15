package Data_storage;

public class Line2D {

    public Line2D(){
        
    }
    
    public Line2D(double coefficient, Vector2 passByPoint){
        this.firstPosition = passByPoint;
        this.secondPosition = new Vector2(passByPoint.x + 1, passByPoint.y + coefficient);
    }

    public Line2D(Vector2 firstPosition, Vector2 secondPosition){
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
    }

    Vector2 firstPosition;
    Vector2 secondPosition;

    public Line2D copy() {
        return new Line2D(firstPosition, secondPosition);
    }

    public double getCoefficient(){
        return (firstPosition.y - secondPosition.y) / (firstPosition.x - secondPosition.x);
    }

    public Line2D getPerpendicularLineAtPoint(Vector2 point){
        double invertedTangentCoefficient = -1 / getCoefficient();
        return new Line2D(invertedTangentCoefficient, point);
    }
    
    public Line2D getParallelLineAtPoint(Vector2 point){
        return new Line2D(getCoefficient(), point);
    }

    public Vector2 getPointAtX(double x){
        if (getCoefficient() == Double.POSITIVE_INFINITY) {
            return new Vector2(x, 0);
        }

        double horizontalToPoint = x - firstPosition.x;
        return firstPosition.translated(new Vector2(horizontalToPoint, firstPosition.y + getCoefficient() * horizontalToPoint));
    }

    public Vector2 getPointAtY(double y){
        if (getCoefficient() == 0) {
            return new Vector2(0, y);
        }

        double heightToPoint = y - firstPosition.y;
        return firstPosition.translated(new Vector2(firstPosition.x + heightToPoint / getCoefficient(), heightToPoint));
    }
}
