package botheuristics;

import java.util.LinkedList;

import Data_storage.Ball;
import Data_storage.Terrain;
import Data_storage.Vector2;

public class AStar {
    /**
     * Instantiate a new class for each new Terrain.
     * Only call the "getDistanceToTarget" method for heuristic purposes
     */
    public AStar(Terrain terrain){
        checkForNullTerrain();

        this.terrain = terrain;
        setMap(getMap());
        setTarget(terrain);
        checkForNullMapAndTarget();
    }
    
    private double[][] map;
    
    private Node originNode;
    private Node targetNode;

    private boolean doDebugMessages = true;
    private Terrain terrain;
    
    private void setMap(double[][] newMap){
        map = newMap;
    }
    
    private void setTarget(Terrain terrain){
        int gridXPosition = translateToGridPosition(terrain.target.position.x);
        int gridYPosition = translateToGridPosition(terrain.target.position.y);
        targetNode = new Node(gridXPosition, gridYPosition);
    }

    public double getDistanceToTarget(Ball ball){
        checkForNullBall(ball);
        checkForNullMapAndTarget();

        int originXPosition = translateToGridPosition(ball.state.position.x);
        int originYPosition = translateToGridPosition(ball.state.position.y);
        setupSearch(originXPosition, originYPosition);
        return aStarPathfinding(originXPosition, originYPosition);
    }

    private void checkForNullBall(Ball ball){
        if (ball == null){
            throw new NullPointerException("Ball was null");
        }
    }

    private void checkForNullTerrain(){
        if (terrain == null) {
            throw new NullPointerException("Terrain was null");
        }
    }
    
    private void checkForNullMapAndTarget(){
        if (map == null) {
            throw new NullPointerException("Map instance was null");
        }
        if (targetNode == null) {
            throw new NullPointerException("Target instance was null");
        }

    }
    
    private void setupSearch(int originXPosition, int originYPosition){
        originNode = new Node(originXPosition, originYPosition);
        originNode.setTarget(targetNode);
        originNode.setOrigin(originNode);
        targetNode.setTarget(targetNode);
        targetNode.setOrigin(originNode);
    }

    /**
     * @return a game unit position translated into a grid position used by the pathfinding algorithm
     */
    private int translateToGridPosition(double axisPosition){
        return (int) (terrain.target.position.x * terrain.SQUARES_PER_GAME_UNIT);
    }

    private double aStarPathfinding(int originXPosition, int originYPosition){
        LinkedList<Node> createdNodes = new LinkedList<>();
        LinkedList<Node> uncheckedNodes = new LinkedList<>();
        createdNodes.add(originNode);

        boolean foundPath = false;
        Node currentNode = originNode;

        while (!foundPath) {
            createSorroundingNodes(currentNode, createdNodes, uncheckedNodes);
            currentNode = findNodeWithLowestValue(uncheckedNodes);

            if (doDebugMessages) {
                System.out.println(currentNode);
            }
            if (currentNode == null) {
                return -1;
                //This means that there exists no path to the target
            }
            uncheckedNodes.remove(currentNode);

            if (currentNode.equals(targetNode)) {
                foundPath = true;
            }
        }
        return currentNode.distanceToOrigin;
    }

    private void createSorroundingNodes(Node origin, LinkedList<Node> createdNodes, LinkedList<Node> uncheckedNodes){
        // All 8 sorrounding tiles are checked
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, 1, 0);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, 1, 1);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, 0, 1);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, -1, 1);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, -1, 0);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, -1, -1);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, 0, -1);
        tryCreateNodeAtDeltaPosition(origin, createdNodes, uncheckedNodes, 1, -1);
    }

    private void tryCreateNodeAtDeltaPosition(Node connectedTo, LinkedList<Node> createdNodes,LinkedList<Node> uncheckedNodes, int deltaX, int deltaY){
        int newXPos = connectedTo.xPosition + deltaX;
        int newYPos = connectedTo.yPosition + deltaY;

        Node nodeAtPosition = getNodeAtPosition(newXPos, newYPos, createdNodes);
        boolean nodeExistsAtPosition = nodeAtPosition != null;
        if (nodeExistsAtPosition) {
            //If the distance from this node to the origin is closer, updates the distance to be shorter
            nodeAtPosition.updateDistanceToOrigin(connectedTo);
            return;
        }
        if (canCreateNodeAt(newXPos, newYPos)) {
            //Creates node at position
            double costToEnter = map[newYPos][newXPos];
            Node newNode = new Node(newXPos, newYPos, connectedTo, costToEnter);
            createdNodes.add(newNode);
            uncheckedNodes.add(newNode);
        }
    }

    private boolean canCreateNodeAt(int xPos, int yPos){
        boolean xPositionInRange = xPos >= 0 && xPos < map[0].length;
        boolean yPositionInRange = yPos >= 0 && yPos < map.length;
        if (!(xPositionInRange && yPositionInRange)) {
            return false;
        }
        boolean spotIsBlocked = map[yPos][xPos] == -1; // There is an unpassable obstacle on this spot
        if (spotIsBlocked) {
            return false;
        }
         return true;
    }

    private Node getNodeAtPosition(int xPos, int yPos, LinkedList<Node> createdNodes){
        for (Node node : createdNodes) {
            if (node.xPosition == xPos && node.yPosition == yPos) {
                return node;
            }
        }
        return null;
    }

    private Node findNodeWithLowestValue(LinkedList<Node> createdNodes){
        if (createdNodes.size() == 0) {
            return null;
        }

        Node nodeWithLowestValue = createdNodes.getFirst();
        for (Node node : createdNodes) {
            if (node.nodeValue < nodeWithLowestValue.nodeValue) {
                nodeWithLowestValue = node;
                continue;
            }
            if (node.nodeValue == nodeWithLowestValue.nodeValue) {
                if (node.distanceToTarget < nodeWithLowestValue.distanceToTarget) {
                    nodeWithLowestValue = node;
                }
            }
        }
        return nodeWithLowestValue;
    }

    public class Node{
        public Node(int xPosition, int yPosition){
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            connectedTo = null;
            costToEnter = 0;
        }
        public Node(int xPosition, int yPosition, Node connectedTo, double costToEnter){
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.connectedTo = connectedTo;
            this.costToEnter = costToEnter;
            distanceToOrigin = distanceToNode(connectedTo) + connectedTo.distanceToOrigin;
            setTarget(targetNode);
        }
        
        public void setOrigin(Node origin){
            distanceToOrigin = distanceToNode(origin);
            updateNodeValue();
        }
        
        public void setTarget(Node target){
            distanceToTarget = distanceToNode(target);
            updateNodeValue();
        }
        
        private void updateNodeValue(){
            nodeValue = distanceToOrigin + distanceToTarget + costToEnter;
        }

        public int xPosition;
        public int yPosition;

        public Node connectedTo;

        private double distanceToTarget;
        private double distanceToOrigin;
        private double costToEnter;

        public double nodeValue;

        public double distanceToNode(Node node){
            if (node == null) {
                return 0;
            }

            double xDistance = Math.abs(xPosition - node.xPosition);
            double yDistance = Math.abs(yPosition - node.yPosition);
            
            double totalDistance = 0;
            if (xDistance < yDistance) {
                totalDistance += xDistance * 14;
                yDistance -= xDistance;
                totalDistance += yDistance * 10;
            }else{
                totalDistance += yDistance * 14;
                xDistance -= yDistance;
                totalDistance += xDistance * 10;
            }
            return totalDistance;
        }

        public void updateDistanceToOrigin(Node checkNode){
            double checkDistance = distanceToNode(checkNode);
            double newDistance = checkNode.distanceToOrigin + checkDistance;

            if (distanceToOrigin > newDistance) {
                distanceToOrigin = newDistance;
                connectedTo = checkNode;
                updateNodeValue();
            }
        }

        @Override
        public String toString(){
            return "x: " + xPosition + " y: " + yPosition;
        }

        
        public boolean equals(Node node){
            boolean xPositionEquals = xPosition == node.xPosition;
            boolean yPositionEquals = yPosition == node.yPosition;
            return xPositionEquals && yPositionEquals;
        }
    }

    //region Generate Grid from Terrain
    public double[][] getMap() {
        double[][] map = createEmptyMap();
        // We add half a tile, to get a height value at the center of each square
        double halfTileOffset = (1d / (double) terrain.SQUARES_PER_GAME_UNIT) / 2d;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {

                double value = terrain.terrainFunction.valueAt(x + halfTileOffset, y + halfTileOffset);
                boolean cannotGoHere = value <= 0 || !terrain.isPointInObstacle(translateGridPositionIntoGameUnits(x, y));
                if (cannotGoHere) {
                    map[y][x] = -1; // This value signifies an unpassable obstacle
                    continue;
                }
                map[y][x] = 1;
            }
        }
        return map;
    }

    private double[][] createEmptyMap() {
        int xSquares = (int) getTerrainWidth() * terrain.SQUARES_PER_GAME_UNIT;
        int ySquares = (int) getTerrainHeight() * terrain.SQUARES_PER_GAME_UNIT;
        double[][] map = new double[ySquares][xSquares];
        return map;
    }

    private Vector2 translateGridPositionIntoGameUnits(int xPos, int yPos) {
        double newX = (double) xPos / (double) terrain.SQUARES_PER_GAME_UNIT;
        double newY = (double) yPos / (double) terrain.SQUARES_PER_GAME_UNIT;
        return new Vector2(newX, newY);
    }

    /**
     * @return terrain width in game units
     */
    public double getTerrainWidth() {
        return Math.abs(terrain.bottomRightCorner.x - terrain.topLeftCorner.x);
    }

    /**
     * @return terrain height in game units
     */
    public double getTerrainHeight() {
        return Math.abs(terrain.bottomRightCorner.y - terrain.topLeftCorner.y);
    }
    //endregion
}
