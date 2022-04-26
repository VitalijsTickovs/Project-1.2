package Data_storage;

import java.util.ArrayList;

import Physics.UtilityClass;

public class ObstacleBox extends Rectangle implements IObstacle {
  public ObstacleBox(Vector2 bottomLeftCorner, Vector2 topRightCorner){
    super(bottomLeftCorner, topRightCorner);
  }
  
  // This is basically friction for bounces
  public double bounciness; // The percentage of momentum that the ball keeps after bouncing.

  private double ballRadius; // This is a temporary global variable to not pass it as a parameter everywhere
  
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
  public CollisionData getCollisionData(Vector2 currentPosition, Vector2 previousPosition, double ballRadius) {

    this.ballRadius = ballRadius;
    Vector2[] wall = getCollisionPointAndWall(previousPosition, currentPosition);

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
  private Vector2[] getCollisionPointAndWall(Vector2 firstPosition, Vector2 secondPosition) {
    Vector2[] collisionPoints = getAllCrossPoints(firstPosition, secondPosition);
    Vector2 closestPoint = UtilityClass.getClosestPoint(firstPosition, collisionPoints);
    Vector2[] wallAndClosestPoint = new Vector2[3];

    if (closestPoint == null) {
      return null;
    }
    Vector2[] wall = new Vector2[2];

    boolean collidedFromLeft = closestPoint.equals(collisionPoints[0]);
    if (collidedFromLeft) {
      wall = getLeftWall();
    }
    boolean collidedFromRight = closestPoint.equals(collisionPoints[1]);
    if (collidedFromRight) {
      wall = getRightWall();
    }
    boolean collidedFromTop = closestPoint.equals(collisionPoints[2]);
    if (collidedFromTop) {
      wall = getTopWall();
    }
    boolean collidedFromBottom = closestPoint.equals(collisionPoints[3]);
    if (collidedFromBottom) {
      wall = getBottomWall();
    }

    wallAndClosestPoint[0] = wall[0];
    wallAndClosestPoint[1] = wall[1];
    wallAndClosestPoint[2] = closestPoint;

    return wallAndClosestPoint;
  }

  private Vector2[] getAllCrossPoints(Vector2 firstPosition, Vector2 secondPosition){
    ArrayList<Vector2> allCrossPoints = new ArrayList<>();
    addCrossPointsAtPosition(allCrossPoints, firstPosition);
    addCrossPointsAtPosition(allCrossPoints, secondPosition);

    addCrossPointsWithWalls(allCrossPoints, firstPosition, secondPosition);
    return allCrossPoints.toArray(new Vector2[0]);
  }
  
  private void addCrossPointsAtPosition(ArrayList<Vector2> allCrossPoints,Vector2 position){
    ArrayList<Vector2> crossPointsFirstPosition = getCrossPointsAtPosition(position);

    for (Vector2 point : crossPointsFirstPosition) {
      if (point == null) {
        continue;
      }
      allCrossPoints.add(point);
    }
  }

  private ArrayList<Vector2> getCrossPointsAtPosition(Vector2 position){
    ArrayList<Vector2> allCrossPointsAtPosition = new ArrayList<>();
    allCrossPointsAtPosition.addAll(getCrossPointsInEpisode(getRightWall(), position));
    allCrossPointsAtPosition.addAll(getCrossPointsInEpisode(getLeftWall(), position));
    allCrossPointsAtPosition.addAll(getCrossPointsInEpisode(getTopWall(), position));
    allCrossPointsAtPosition.addAll(getCrossPointsInEpisode(getBottomWall(), position));

    return allCrossPointsAtPosition;
  }
  
  private ArrayList<Vector2> getCrossPointsInEpisode(Vector2[] episode, Vector2 position){
    Vector2[] leftWall = getLeftWall();
    Line2D leftWallLine = new Line2D(leftWall[0], leftWall[1]);
    ArrayList<Vector2> crossPointsWithWall = leftWallLine.getCrossPointsWithCircle(position, ballRadius);

    if (crossPointsWithWall == null) {
      return new ArrayList<Vector2>();
    }
    for (Vector2 point : crossPointsWithWall) {
      if (!UtilityClass.isPointInEpisode(point, episode[0], episode[1])) {
        point = null;
      }
    }
    return crossPointsWithWall;
  }

  private void addCrossPointsWithWalls(ArrayList<Vector2> allCrossPoints, Vector2 firstPosition, Vector2 secondPosition){
    Line2D pathLine = new Line2D(firstPosition, secondPosition);
    Vector2[] crossPointsThroughMiddle = getCrossPointsWithWalls(firstPosition, secondPosition);
    //Now we need the two lines that describe the edges of the ball's path
    Line2D perpendicularToPathAtFirstPosition = pathLine.getPerpendicularLineAtPoint(firstPosition);
    Line2D perpendicularToPathAtSecondPosition = pathLine.getPerpendicularLineAtPoint(secondPosition);
    
    double horizontalOffset = ballRadius / Math.sin(pathLine.getSlopeAngle());
    Vector2 translation = new Vector2(horizontalOffset, 0);
    Line2D firstParallel = pathLine.getLineTranslatedByVector(translation); 
    Line2D secondParallel = pathLine.getLineTranslatedByVector(translation.reversed());
    
    Vector2 firstEpisodeFirstPosition = UtilityClass.findLineIntersection(firstParallel, perpendicularToPathAtFirstPosition);
    Vector2 firstEpisodeSecondPosition = UtilityClass.findLineIntersection(firstParallel, perpendicularToPathAtSecondPosition);
    
    Vector2 secondEpisodeFirstPosition = UtilityClass.findLineIntersection(secondParallel, perpendicularToPathAtFirstPosition);
    Vector2 secondEpisodeSecondPosition = UtilityClass.findLineIntersection(secondParallel, perpendicularToPathAtSecondPosition);
    
    Vector2[] crossPointsThroughFirstParallel = getCrossPointsWithWalls(firstEpisodeFirstPosition, firstEpisodeSecondPosition);
    Vector2[] crossPointsThroughSecondParallel = getCrossPointsWithWalls(secondEpisodeFirstPosition, secondEpisodeSecondPosition);

    addNonNullCrossPoints(allCrossPoints, crossPointsThroughMiddle);
    addNonNullCrossPoints(allCrossPoints, crossPointsThroughFirstParallel);
    addNonNullCrossPoints(allCrossPoints, crossPointsThroughSecondParallel);
    
  }

  private void addNonNullCrossPoints(ArrayList<Vector2> allCrossPoints, Vector2[] crossPoints){
    for (Vector2 point : crossPoints) {
      if (point != null) {
        allCrossPoints.add(point);
      }
    }
  }

  /**
   * @return a list of 4 positions that save the collision point of the ball with the wall. 
   * If a point is null, then no collision with that wall occured
   */
  private Vector2[] getCrossPointsWithWalls(Vector2 firstPosition, Vector2 secondPosition){
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
      return Vector2.upVector;
    }
    // collidedFromBottom = position.y < downLeftCorner.y;
    return Vector2.downVector;
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
