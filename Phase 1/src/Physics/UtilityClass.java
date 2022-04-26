package Physics;

import Data_storage.Line2D;
import Data_storage.Vector2;

public class UtilityClass {

    /**
     * 
     * @param p1 first point of the first line
     * @param p2 second point of the first line
     * @param p3 first point of the second line
     * @param p4 second point of the second line
     * @return a position the cross point of these two lines, if it is a part of one of the episodes. 
     * If it not, returns null
     */
    public static Vector2 findEpisodeIntersection(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4){
        Vector2 crossPoint = findLineIntersection(p1, p2, p3, p4);
        if (isPointInEpisode(crossPoint, p1, p2) && isPointInEpisode(crossPoint, p3, p4)){
            return crossPoint;
        }
        return null;
    }

    /**
     * 
     * @param point
     * @param bottomLeft
     * @param topRight
     * @return true, if the position is a part of an episode
     */
    public static boolean isPointInEpisode(Vector2 point, Vector2 bottomLeft,Vector2 topRight){
        if (point.distanceTo(bottomLeft) + point.distanceTo(topRight) == bottomLeft.distanceTo(topRight))
            return true; // C is on the line.
        return false;
    }

    /**
     * @return the cross point of these two lines
     */
    public static Vector2 findLineIntersection(Line2D firstLine, Line2D secondLine) {
        return findLineIntersection(firstLine.getPointAtX(0), firstLine.getPointAtX(1),
        secondLine.getPointAtX(0), secondLine.getPointAtX(1));
    }

    /**
     * 
     * @param p1 first point of the first line
     * @param p2 second point of the first line
     * @param p3 first point of the second line
     * @param p4 second point of the second line
     * @return the cross point of these two lines
     */
    public static Vector2 findLineIntersection(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        // (line1 = (A1, B1, C1)) A1*x + B1*y + C1 = 0 | x = (C1 - B1*y)/A1 | y = (C1 -
        // A1*x)/B1
        // (line2 = (A2, B2, C2)) A2*x + B2*y + C2 = 0 | x = (C2 - B2*y)/A2 | y = (C2 -
        // A2*x)/B2
        // k1*x + l1 = k2*x + l2
        // (k1 - k2)*x = l2 - l1
        // x = (l2 - l1)/(k1 - k2)

        double detD = getDeterminant(
                getDeterminant(p1.x, 1, p2.x, 1),
                getDeterminant(p1.y, 1, p2.y, 1),
                getDeterminant(p3.x, 1, p4.x, 1),
                getDeterminant(p3.y, 1, p4.y, 1));

        if (detD == 0) {
            return null;
        }
        double detX = getDeterminant(
                getDeterminant(p1.x, p1.y, p2.x, p2.y),
                getDeterminant(p1.x, 1, p2.x, 1),
                getDeterminant(p3.x, p3.y, p4.x, p4.y),
                getDeterminant(p3.x, 1, p4.x, 1));
        double detY = getDeterminant(
                getDeterminant(p1.x, p1.y, p2.x, p2.y),
                getDeterminant(p1.y, 1, p2.y, 1),
                getDeterminant(p3.x, p3.y, p4.x, p4.y),
                getDeterminant(p3.y, 1, p4.y, 1));

        return new Vector2(detX / detD, detY / detD);
    }

    private static double getDeterminant(double p11, double p12, double p21, double p22) {
        return p11 * p22 - p12 * p21;
    }

    /**
     * 
     * @param closestTo the point that the distance is measured to
     * @param points a list of points
     * @return the closest point or null if all given points are null or closestTo is null
     */
    public static Vector2 getClosestPoint(Vector2 closestTo, Vector2[] points){
        if (closestTo == null || points == null || points.length == 0) {
            return null;
        }
        Vector2 closestPoint = getFirstNotNullPoint(points);
        if (points.length == 1 || closestPoint == null) {
            return closestPoint;
        }
        for (int i = 1; i < points.length; i++) {
            if (points[i] == null) {
                continue;
            }
            if (closestTo.distanceTo(points[i]) < closestTo.distanceTo(closestPoint)) {
                closestPoint = points[i];
            }
        }
        return closestPoint;
    }

    public static Vector2 getFirstNotNullPoint(Vector2[] points){
        if (points == null) {
            return null;
        }
        for (Vector2 vector2 : points) {
            if (vector2 != null) {
                return vector2;
            }
        }
        return null;
    }
}
