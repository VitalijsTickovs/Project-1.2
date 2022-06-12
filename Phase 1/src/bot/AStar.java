package bot;

import java.util.LinkedList;

import datastorage.GameState;
import datastorage.Terrain;
import reader.GameStateLoader;
import utility.math.Vector2;

public class AStar {
    public static void main(String[] args) {
        GameState gameState = GameStateLoader.readFile();
        AStar aStar = new AStar(gameState.getTerrain());

        double negatives = 0;
        for (int i = 0; i < aStar.map.length; i++) {
            for (int j = 0; j < aStar.map[i].length; j++) {
                if (aStar.map[i][j] == -1) {
                    negatives++;
                }
            }
        }
        double percentage = negatives / (double)(aStar.map.length * aStar.map[0].length) * 100;
        System.out.println(negatives + " percentage: " + percentage);
    }

    /**
     * Instantiate a new class for each new {@code Terrain}.
     * Only call the {@code getDistanceToTarget} method for heuristic purposes
     */
    public AStar(Terrain terrain) {
        this.terrain = terrain;
        checkForNullTerrain();
        setMap(getMap());
        setTarget(terrain);
        checkForNullMapAndTarget();
    }

    private double[][] map;

    private Node originNode;
    private Node targetNode;

    private boolean doDebugMessages = true;
    private Terrain terrain;

    public final int SQUARES_PER_GAME_UNIT = 4; // How many squares will the map used by AStar pathfinding generate per
                                                // game unit.
    // A game unit is a distance between two vectors (0,0) and (0,1);
    // The map's size is calculated the "topLeftCorner" and "bottomRightCorner"
    // vectors

    private void setMap(double[][] newMap) {
        map = newMap;
    }

    private void setTarget(Terrain terrain) {
        if (terrain.target == null) {
            throw new NullPointerException("Target was null");
        }

        int gridXPosition = translateToGridPosition(terrain.target.position.x);
        int gridYPosition = translateToGridPosition(terrain.target.position.y);
        targetNode = new Node(gridXPosition, gridYPosition);
    }

    /**
     * @param ball the ball to check its distance from the target
     * @return the {@code length} of the shortest path from the ball to the target
     *         while avoiding water and obstacles.
     *         Returns {@code -1} if an unobstructed path does not exist
     */
    public double getDistanceToTarget(Vector2 position) {
        checkForNullPosition(position);
        checkForNullMapAndTarget();

        int originXPosition = translateToGridPosition(position.x);
        int originYPosition = translateToGridPosition(position.y);
        setupSearch(originXPosition, originYPosition);
        return aStarPathfinding();
    }

    private void checkForNullPosition(Vector2 position) {
        if (position == null) {
            throw new NullPointerException("Position was null");
        }
    }

    private void checkForNullTerrain() {
        if (terrain == null) {
            throw new NullPointerException("Terrain was null");
        }
    }

    private void checkForNullMapAndTarget() {
        if (map == null) {
            throw new NullPointerException("Map instance was null");
        }
        if (targetNode == null) {
            throw new NullPointerException("Target instance was null");
        }

    }

    private void setupSearch(int originXPosition, int originYPosition) {
        originNode = new Node(originXPosition, originYPosition);
        originNode.setTarget(targetNode);
        originNode.setOrigin(originNode);
        targetNode.setTarget(targetNode);
        targetNode.setOrigin(originNode);
    }

    /**
     * @return a game unit position translated into a grid position used by the
     *         pathfinding algorithm
     */
    private int translateToGridPosition(double axisPosition) {
        return (int) (terrain.target.position.x * SQUARES_PER_GAME_UNIT);
    }

    /**
     * @return the {@code length} of the shortest path from the ball to the target
     *         while avoiding water and obstacles.
     *         Returns {@code -1} if an unobstructed path does not exist.
     */
    private double aStarPathfinding() {
        LinkedList<Node> createdNodes = new LinkedList<>();
        LinkedList<Node> uncheckedNodes = new LinkedList<>();
        createdNodes.add(originNode);

        boolean foundPath = false;
        Node currentNode = originNode;

        while (!foundPath) {
            createSorroundingNodes(currentNode, createdNodes, uncheckedNodes);
            currentNode = findNodeWithLowestValue(uncheckedNodes);

            if (doDebugMessages) {
                // System.out.println(currentNode);
            }
            if (currentNode == null) {
                // This means that there exists no path to the target
                return -1;
            }
            uncheckedNodes.remove(currentNode);

            if (currentNode.equals(targetNode)) {
                foundPath = true;
            }
        }
        return currentNode.distanceToOrigin;
    }

    private void createSorroundingNodes(Node origin, LinkedList<Node> createdNodes, LinkedList<Node> uncheckedNodes) {
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

    private void tryCreateNodeAtDeltaPosition(Node connectedTo, LinkedList<Node> createdNodes,
            LinkedList<Node> uncheckedNodes, int deltaX, int deltaY) {
        int newXPos = connectedTo.xPosition + deltaX;
        int newYPos = connectedTo.yPosition + deltaY;

        Node nodeAtPosition = getNodeAtPosition(newXPos, newYPos, createdNodes);
        boolean nodeExistsAtPosition = nodeAtPosition != null;
        if (nodeExistsAtPosition) {
            // If the distance from this node to the origin is closer, updates the distance
            // to be shorter
            nodeAtPosition.updateDistanceToOrigin(connectedTo);
            return;
        }
        if (canCreateNodeAt(newXPos, newYPos)) {
            // Creates node at position
            double costToEnter = map[newYPos][newXPos];
            Node newNode = new Node(newXPos, newYPos, connectedTo, costToEnter);
            createdNodes.add(newNode);
            uncheckedNodes.add(newNode);
        }
    }

    private boolean canCreateNodeAt(int xPos, int yPos) {
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

    private Node getNodeAtPosition(int xPos, int yPos, LinkedList<Node> createdNodes) {
        for (Node node : createdNodes) {
            if (node.xPosition == xPos && node.yPosition == yPos) {
                return node;
            }
        }
        return null;
    }

    /**
     * 
     * @param uncheckedNodes the list containing all created but yet unchecked
     *                       {@code Nodes}
     * @return a {@code Node} from the list with the lowest {@code nodeValue} or
     *         {@code null} if the list was empty.
     *         When the list is empty, it means that there exists no path that would
     *         not pass through an obstacle
     */
    private Node findNodeWithLowestValue(LinkedList<Node> uncheckedNodes) {
        if (uncheckedNodes.size() == 0) {
            return null;
        }

        Node nodeWithLowestValue = uncheckedNodes.getFirst();
        for (Node node : uncheckedNodes) {
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

    public class Node {
        public Node(int xPosition, int yPosition) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            connectedTo = null;
            costToEnter = 0;
        }

        public Node(int xPosition, int yPosition, Node connectedTo, double costToEnter) {
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.connectedTo = connectedTo;
            this.costToEnter = costToEnter;
            distanceToOrigin = getDistanceToNode(connectedTo) + connectedTo.distanceToOrigin;
            setTarget(targetNode);
        }

        /**
         * The sum of {@code distanceToOrigin}, {@code distanceToTarget} and
         * {@code costToEnter}.
         */
        public double nodeValue;
        public int xPosition;
        public int yPosition;
        public Node connectedTo;

        private double distanceToTarget;
        private double distanceToOrigin;
        /**
         * Cost of entering this node taken from the grid map generated using the
         * {@code Terrain}'s {@code terrainFunction}
         */
        private double costToEnter;

        // region Mutator methods
        public void setOrigin(Node origin) {
            distanceToOrigin = getDistanceToNode(origin);
            updateNodeValue();
        }

        public void setTarget(Node target) {
            distanceToTarget = getDistanceToNode(target);
            updateNodeValue();
        }

        public void updateDistanceToOrigin(Node checkNode) {
            double checkDistance = getDistanceToNode(checkNode);
            double newDistance = checkNode.distanceToOrigin + checkDistance;

            if (distanceToOrigin > newDistance) {
                distanceToOrigin = newDistance;
                connectedTo = checkNode;
                updateNodeValue();
            }
        }
        // endregion

        // region Accessor methods
        public double getDistanceToNode(Node node) {
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
            } else {
                totalDistance += yDistance * 14;
                xDistance -= yDistance;
                totalDistance += xDistance * 10;
            }
            return totalDistance;
        }

        @Override
        public String toString() {
            return "x: " + xPosition + " y: " + yPosition;
        }

        public boolean equals(Node node) {
            boolean xPositionEquals = xPosition == node.xPosition;
            boolean yPositionEquals = yPosition == node.yPosition;
            return xPositionEquals && yPositionEquals;
        }
        // endregion

        // region Helper methods
        private void updateNodeValue() {
            nodeValue = distanceToOrigin + distanceToTarget + costToEnter;
        }
        // endregion
    }

    /**
     * Prints the map such that all obstacles are coded as {@code x} and walkable
     * tiles as {@code o}
     */
    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == -1) {
                    // Tile blocked
                    // System.out.print("x");
                } else {
                    // Tile walkable
                    // System.out.print("o");
                }
            }
            System.out.println();
        }
    }

    double HALF_TILE_OFFSET = (1d / (double) SQUARES_PER_GAME_UNIT) / 2d;

    // region Generate Grid from Terrain
    private double[][] getMap() {
        double[][] map = createEmptyMap();
        // We add half a tile, to get a height value at the center of each square

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Vector2 positionInGameUnits = translateGridPositionIntoGameUnits(x, y);
                double value = terrain.getTerrainFunction().valueAt(positionInGameUnits);
                boolean tileIsObstacle = !terrain.isPointInObstacle(positionInGameUnits);

                boolean cannotGoHere = value <= 0 || tileIsObstacle;
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
        int xSquares = (int) getTerrainWidth() * SQUARES_PER_GAME_UNIT;
        int ySquares = (int) getTerrainHeight() * SQUARES_PER_GAME_UNIT;
        double[][] map = new double[ySquares][xSquares];
        return map;
    }

    private Vector2 translateGridPositionIntoGameUnits(int xPos, int yPos) {
        double newX = (double) xPos / (double) SQUARES_PER_GAME_UNIT + HALF_TILE_OFFSET;
        double newY = (double) yPos / (double) SQUARES_PER_GAME_UNIT + HALF_TILE_OFFSET;
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
    // endregion
}
