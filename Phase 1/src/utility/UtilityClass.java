package utility;

import utility.math.Line2D;
import utility.math.Vector2;

public class UtilityClass {

    private UtilityClass(){
        //Cannot instantiate this class - it's just static
    }

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
        if (crossPoint == null) {
            return null;
        }
        if (isPointInEpisode(crossPoint, p1, p2) && isPointInEpisode(crossPoint, p3, p4)){
            return crossPoint;
        }
        return null;
    }

    /**
     *
     * @param point
     * @param firstPos
     * @param secondPos
     * @return true, if the position is a part of an episode
     */
    public static boolean isPointInEpisode(Vector2 point, Vector2 firstPos, Vector2 secondPos){
        double distanceA = point.distanceTo(firstPos);
        double distanceB = point.distanceTo(secondPos);
        float distanceC =(float) firstPos.distanceTo(secondPos);

        float sum = (float) (distanceA + distanceB);
        if (sum == distanceC)
            return true; // C is on the line.
        return false;
    }

    /**
     * @return the cross point of these two lines
     */
    public static Vector2 findLineIntersection(Line2D firstLine, Line2D secondLine) {
        if (firstLine == null || secondLine == null) {
            return null;
        }

        return findLineIntersection(firstLine.getFirstPosition(), firstLine.getSecondPosition(),
                secondLine.getFirstPosition(), secondLine.getSecondPosition());
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
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return null;
        }

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
