package Physics;

import Data_storage.Vector2;

public class UtilityClass {
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
}
