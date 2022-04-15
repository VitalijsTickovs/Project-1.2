package Data_storage;

import Physics.UtilityClass;

public class ObstacleBox extends Rectangle implements IObstacle {
  public double bounciness; // The percentage of momentum that the ball loses after bouncing.

  // This is basically friction for bounces

  public ObstacleBox(Vector2 bottomLeftCorner, Vector2 topRightCorner){
    super(bottomLeftCorner, topRightCorner);
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
  public CollisionData getCollisionData(Vector2 currentPosition, Vector2 previousPosition) {

    Vector2[] wall = getWallCollision(previousPosition, currentPosition);

    CollisionData collisionData = new CollisionData();
    
    Vector2 wallDirectionVector = wall[1].translated(wall[0].reversed());
    Vector2 normal = wallDirectionVector.getPerpendicularVector();
    collisionData.collisionNormal = normal;
    
    collisionData.bounciness = bounciness;
    collisionData.collisionPosition = wall[2];

    return collisionData;
  }

  /**
   *
   * @return a list containing two corners of the wall and the collision point
   * or null if the object did not collide with any wall
   */
  private Vector2[] getWallCollision(Vector2 firstPosition, Vector2 secondPosition) {
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
    crossPoints[0] = UtilityClass.findEpisodeIntersection(firstPosition, secondPosition, leftWall[0], leftWall[1]);
    Vector2[] rightWall = getRightWall();
    crossPoints[1] = UtilityClass.findEpisodeIntersection(firstPosition, secondPosition, rightWall[0], rightWall[1]);
    Vector2[] topWall = getTopWall();
    crossPoints[2] = UtilityClass.findEpisodeIntersection(firstPosition, secondPosition, topWall[0], topWall[1]);
    Vector2[] bottomWall = getBottomWall();
    crossPoints[3] = UtilityClass.findEpisodeIntersection(firstPosition, secondPosition, bottomWall[0], bottomWall[1]);

    return crossPoints;
  }

  private Vector2[] getBottomWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = bottomLeftCorner;
    wall[1] = new Vector2(topRightCorner.x, bottomLeftCorner.y);
    return wall;
  }

  private Vector2[] getTopWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = topRightCorner;
    wall[1] = new Vector2(bottomLeftCorner.x, topRightCorner.y);
    return wall;
  }

  private Vector2[] getRightWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = topRightCorner;
    wall[1] = new Vector2(topRightCorner.x, bottomLeftCorner.y);
    return wall;
  }

  private Vector2[] getLeftWall() {
    Vector2[] wall = new Vector2[2];
    wall[0] = bottomLeftCorner;
    wall[1] = new Vector2(bottomLeftCorner.x, topRightCorner.y);
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
