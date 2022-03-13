package Data_storage;

public class Circle extends Shape {
    public Vector2 originPosition;
    public double radius;

    @Override
    protected boolean isPositionInside(Vector2 objectPosition) {
        Vector2 deltaPos = objectPosition.copy();
        deltaPos.translate(originPosition.getOppositeVector());
        double distance = deltaPos.length();

        boolean isInside = distance < radius;
        if (isInside) {
            return true;
        }
        return false;
    }
}
