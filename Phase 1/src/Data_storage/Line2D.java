package Data_storage;

import Physics.UtilityClass;

public class Line2D {
    
    public Line2D(double slope, Vector2 passByPoint){
        this.firstPosition = passByPoint;
        this.slope = slope;

        if (Double.isInfinite(slope)) {
            this.secondPosition = firstPosition.translated(Vector2.upVector);
            yZero = 0;
            return;
        }

        this.secondPosition = new Vector2(passByPoint.x + 1, passByPoint.y + slope);
        yZero = passByPoint.y - slope * passByPoint.x;
    }
    
    public Line2D(Vector2 firstPosition, Vector2 secondPosition){
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        slope = getSlope();

        if (Double.isInfinite(slope)) {
            yZero = 0;
        }

        yZero = firstPosition.y - slope * firstPosition.x;
    }

    Vector2 firstPosition;
    Vector2 secondPosition;

    double slope;
    double yZero;

    public Line2D copy() {
        return new Line2D(firstPosition, secondPosition);
    }

    /**
     * @return the tangent coefficient of this linear equation.
     * Eg. if "y = ax + b", then it returns "a"
     */
    public double getSlope(){
        if (firstPosition.x == secondPosition.x) {
            return Double.POSITIVE_INFINITY;
        }
        return (firstPosition.y - secondPosition.y) / (firstPosition.x - secondPosition.x);
    }

    /**
     * @return A new line that is perpendicular to this line and passes through the given point
     */
    public Line2D getPerpendicularLineAtPoint(Vector2 point){
        double invertedCoefficient = -1 / slope;
        return new Line2D(invertedCoefficient, point);
    }
    
    
    /**
     * @param point
     * @return A new line that is parallel to this line and passes through the given point
     */
    public Line2D getParallelLineAtPoint(Vector2 point){
        return new Line2D(slope, point);
    }

    public Vector2 getPointAtX(double x){
        if (slope == Double.POSITIVE_INFINITY) {
            if (x == firstPosition.x) {
                return new Vector2(x, 0);
            }
            return null;
        }

        Vector2 point = new Vector2(x, slope * x + yZero);
        return point;
    }

    public Vector2 getPointAtY(double y){
        if (slope == 0) {
            if (y == firstPosition.y) {
                return new Vector2(0, y);
            }
            return null;
        }

        Vector2 point = new Vector2((firstPosition.y - yZero) / slope, y);
        return point;
    }

    /**
     * @return the position at which this line crosses another one. Returns null if they are parallel
     */
    public Vector2 getCrossPointWithLine(Line2D line){
        return UtilityClass.findLineIntersection(firstPosition, secondPosition, line.firstPosition, line.secondPosition);
    }

    public Vector2[] getCrossPointsWithCircle(Vector2 originPosition, double radius){
        if (Double.isInfinite(slope)) {
            return countVerticalCrossPoints(originPosition, radius);
        }

        double yValue = getPointAtX(0).y;

        double a = 1 + slope * slope;
        double b = 2 * (slope * (yValue - originPosition.y) - originPosition.x);
        double c = originPosition.x * originPosition.x + (yValue - originPosition.y) * (yValue - originPosition.y) - radius * radius;

        double discriminant = b * b - 4 * a * c;

        Vector2[] crossPoints = new Vector2[2];

        if (discriminant == 0) {
            double x = ((-b + Math.sqrt(discriminant)) / (2 * a));
            Vector2 crossPoint = getPointAtX(x);
            crossPoints[0] = crossPoint;
            crossPoints[1] = crossPoint;
            return crossPoints;
        }
        if (discriminant > 0) {
            double x1 = ((-b + Math.sqrt(discriminant)) / (2 * a));
            double x2 = ((-b - Math.sqrt(discriminant)) / (2 * a));
            Vector2 crossPoint1 = getPointAtX(x1);
            Vector2 crossPoint2 = getPointAtX(x2);
            crossPoints[0] = crossPoint1;
            crossPoints[1] = crossPoint2;
            return crossPoints;
        }
        return null;
    }

    private Vector2[] countVerticalCrossPoints(Vector2 originPosition, double radius){
        boolean touchesCircle = radius * radius >= (firstPosition.x - originPosition.x) * (firstPosition.x - originPosition.x);
        if (touchesCircle) {
            Vector2[] crossPoints = new Vector2[2];

            double root = Math.sqrt((radius * radius) - (firstPosition.x - originPosition.x) * (firstPosition.x - originPosition.x));
            crossPoints[0] = new Vector2(firstPosition.x, root + originPosition.y);
            crossPoints[1] = new Vector2(firstPosition.x, - root + originPosition.y);

            return crossPoints;
        }
        return null;
    }
}
