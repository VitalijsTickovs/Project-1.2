package Data_storage;

import Physics.UtilityClass;

public class ObstacleBox extends Rectangle implements IObstacle {
  public double bounciness; // The percentage of momentum that the ball loses after bouncing.

  // This is basically friction for bounces

  public ObstacleBox(Vector2 bottomLeftCorner, Vector2 topRightCorner){
        super(bottomLeftCorner, topRightCorner);
    }
  public static void main(String[] args) {
    Vector2 bottomLeft = new Vector2(-2, -2);
    Vector2 topRight = new Vector2(2, 2);

    ObstacleBox box = new ObstacleBox(bottomLeft, topRight);

    Vector2 pos = new Vector2(-1,-2.5);
    Vector2 pos2 = new Vector2(-1,-1.9);
    Vector2[] wall = box.getWall(pos, pos2);

    System.out.println(wall[0]);
    System.out.println(wall[1]);

  }

  @Override
  public boolean isBallColliding(Vector2 ballPos, double radius) {
    return isCircleInside(ballPos, radius);
  }

  @Override
  public boolean isPositionColliding(Vector2 position) {
    return isPositionInside(position);
  }

  @Override
  public double getBounciness() {
    return bounciness;
  }

  @Override
  public void bounceVector(
    Vector2 position,
    Vector2 velocity,
    double h,
    double ballRadius
  ) {
    Vector2[] wall = getWall(position, position.translate(velocity.scale(h)));
    Vector2 intersectionPosition = UtilityClass.findLineIntersection(
      position,
      position.copy().translate(velocity.scale(h)),
      wall[0],
      wall[1]
    );
    Vector2 positionToBall = intersectionPosition.translate(
      position.reversed()
    );
    double distanceToBall = positionToBall.length();
    double moveBall = distanceToBall - ballRadius;
    Vector2 moveToObstacleVector = positionToBall
      .normalized()
      .scale(moveBall * h);

    position.translate(moveToObstacleVector);
    Vector2 normal = getCollisionNormal(position, velocity);
    // If bounciness equals 0.8, the returned velocity vector will be 20% shorter
    Vector2 bouncedVelocity = velocity.reflected(normal).scale(bounciness);
    velocity = bouncedVelocity;
    Vector2 movePastObstacleVector = velocity
      .normalized()
      .scale((distanceToBall - moveBall) * h);
    position.translate(movePastObstacleVector);
  }

  /**
   *
   * @return a list containing two points laying on the same line as the wall that the object collided with and the collision point
   * or null if the object did not collide with any wall
   */
  private Vector2[] getWall(Vector2 firstPosition, Vector2 secondPosition) {
    Vector2[] collisionPoints = getAllCrossPoints(firstPosition,secondPosition);
    Vector2 closestPoint = UtilityClass.getClosestPoint(firstPosition, collisionPoints);
    Vector2[] wallAndClosestPoint = new Vector2[3];

    if (closestPoint == null) {
      return null;
    }

    boolean collidedFromLeft = closestPoint.equals(collisionPoints[0]);
    if (collidedFromLeft) {
      Vector2[] leftWall = getLeftWall();
      wallAndClosestPoint[0] = leftWall[0];
      wallAndClosestPoint[1] = leftWall[1];
      wallAndClosestPoint[2] = closestPoint;

      return wallAndClosestPoint;
    }
    boolean collidedFromRight = closestPoint.equals(collisionPoints[1]);
    if (collidedFromRight) {
      Vector2[] rightWall = getRightWall();
      wallAndClosestPoint[0] = rightWall[0];
      wallAndClosestPoint[1] = rightWall[1];
      wallAndClosestPoint[2] = closestPoint;

      return wallAndClosestPoint;
    }
    boolean collidedFromTop = closestPoint.equals(collisionPoints[2]);
    if (collidedFromTop) {
      Vector2[] topWall = getTopWall();
      wallAndClosestPoint[0] = topWall[0];
      wallAndClosestPoint[1] = topWall[1];
      wallAndClosestPoint[2] = closestPoint;

      return wallAndClosestPoint;
    }
    boolean collidedFromBottom = closestPoint.equals(collisionPoints[3]);
    if (collidedFromBottom) {
      Vector2[] bottomWall = getBottomWall();
      wallAndClosestPoint[0] = bottomWall[0];
      wallAndClosestPoint[1] = bottomWall[1];
      wallAndClosestPoint[2] = closestPoint;

      return wallAndClosestPoint;
    }
    return null;
  }

  private Vector2[] getAllCrossPoints(Vector2 firstPosition, Vector2 secondPosition){
    Vector2[] crossPoints = new Vector2[4];
    
    Vector2[] leftWall = getLeftWall();
    crossPoints[0] = UtilityClass.findLineIntersection(firstPosition, secondPosition, leftWall[0], leftWall[1]);
    Vector2[] rightWall = getRightWall();
    crossPoints[1] = UtilityClass.findLineIntersection(firstPosition, secondPosition, rightWall[0], rightWall[1]);
    Vector2[] topWall = getTopWall();
    crossPoints[2] = UtilityClass.findLineIntersection(firstPosition, secondPosition, topWall[0], topWall[1]);
    Vector2[] bottomWall = getBottomWall();
    crossPoints[3] = UtilityClass.findLineIntersection(firstPosition, secondPosition, bottomWall[0], bottomWall[1]);

    return crossPoints;
  }

  private Vector2[] getBottomWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = bottomLeftCorner;
    wall[1] = bottomLeftCorner.translate(new Vector2(-1, 0));
    return wall;
  }

  private Vector2[] getTopWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = topRightCorner;
    wall[1] = topRightCorner.translate(new Vector2(-1, 0));
    return wall;
  }

  private Vector2[] getRightWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = topRightCorner;
    wall[1] = topRightCorner.translate(new Vector2(0, -1));
    return wall;
  }

  private Vector2[] getLeftWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = bottomLeftCorner;
    wall[1] = bottomLeftCorner.translate(new Vector2(0, 1));
    return wall;
  }

  public Vector2 getCollisionNormal(Vector2 position, Vector2 velocity) {
    boolean collidedFromLeft = position.x < bottomLeftCorner.x;
    if (collidedFromLeft) {
      return Vector2.leftVector;
    }
    boolean collidedFromRight = position.x > topRightCorner.x;
    if (collidedFromRight) {
      return Vector2.rightVector;
    }
    boolean collidedFromTop = position.y > topRightCorner.y;
    if (collidedFromTop) {
      return Vector2.forwardVector;
    }
    // collidedFromBottom = position.y < downLeftCorner.y;
    return Vector2.backwardVector;
  }

  @Override
  public void print() {
    System.out.println("Box: ");
    System.out.print("Down left corner: ");
    System.out.println(bottomLeftCorner);
    System.out.print("Top right corner: ");
    System.out.println(topRightCorner);
    System.out.print("Bounciness: ");
    System.out.println(bounciness);
  }
}
