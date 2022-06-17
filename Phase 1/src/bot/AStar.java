package bot;

import java.util.LinkedList;

import datastorage.GameState;
import datastorage.Terrain;
import reader.GameStateLoader;
import utility.math.Vector2;

public class AStar {
    public static void main(String[] args) {
        lol();
    }

    private static void lol() {
        GameState gameState = GameStateLoader.readFile();
        AStar aStar = new AStar(gameState.getTerrain());

        double distance = aStar.getDistanceToTarget(gameState.getTerrain().ballStartingPosition, 2);
        System.out.println("Distance: " + distance);
        distance = aStar.getDistanceToTarget(gameState.getTerrain().ballStartingPosition.translated(2,2), 1);
        System.out.println("Distance: " + distance);

        double negatives = 0;
        for (int i = 0; i < aStar.map.length; i++) {
            for (int j = 0; j < aStar.map[i].length; j++) {
                if (aStar.map[i][j] == -1) {
                    negatives++;
                }
            }
        }
        double percentage = negatives / (double) (aStar.map.length * aStar.map[0].length) * 100;
        System.out.println("Obstacles take up this percentage of map: " + percentage);
    }

    /**
     * Instantiate a new class for each new {@code Terrain}.
     * Only call the {@code getDistanceToTarget} method for heuristic purposes
     */
    public AStar(Terrain terrain) {
        this.terrain = terrain;
        checkForNullTerrain();
        mapCreator = new PathfindingMapCreator(terrain);
    }

    private double[][] map;
    private Node[][] createdNodes;

    private Node originNode;
    private Node targetNode;
    private Vector2 topLeftPos;

    private boolean doDebugMessages = true;
    private Terrain terrain;
    private PathfindingMapCreator mapCreator;

    public int SQUARES_PER_GAME_UNIT = -1; // How many squares will the map used by AStar pathfinding generate per
                                           // game unit.
    // A game unit is a distance between two vectors (0,0) and (0,1);
    // The map's size is calculated the "topLeftCorner" and "bottomRightCorner"
    // vectors

    /**
     * @param ball the ball to check its distance from the target
     * @return the {@code length} of the shortest path from the ball to the target
     *         while avoiding water and obstacles.
     *         Returns {@code -1} if an unobstructed path does not exist
     */
    public double getDistanceToTarget(Vector2 ballPosition, int squaresPerGameUnit) {
        checkSquares(squaresPerGameUnit);
        checkForNullPosition(ballPosition);

        setupSearch(ballPosition);
        checkForNullMapAndTarget();

        double distanceInGridUnits = aStarPathfinding();
        double distanceInGameUnits = translateResultToGameUnits(distanceInGridUnits);
        double ballOffset = ballPosition.distanceTo(terrain.target.position);
        System.out.println(ballOffset);
        return distanceInGameUnits + ballOffset;
    }

    // region Startup
    private void checkSquares(int squaresPerGameUnit) {
        if (squaresPerGameUnit <= 0) {
            throw new IndexOutOfBoundsException("squaresPerGameUnit was not in the correct range");
        }
        if (SQUARES_PER_GAME_UNIT != squaresPerGameUnit) {
            SQUARES_PER_GAME_UNIT = squaresPerGameUnit;
            updateMaps();
            return;
        }
        SQUARES_PER_GAME_UNIT = squaresPerGameUnit;
        createNodeMap();
    }

    private void updateMaps() {
        setMapCorners();
        setMap(mapCreator.getMap(SQUARES_PER_GAME_UNIT));
        createNodeMap();
        setTarget(terrain);
    }

    private void setMapCorners() {
        int topLeftX = ((int) (terrain.topLeftCorner.x * SQUARES_PER_GAME_UNIT) / SQUARES_PER_GAME_UNIT);
        int topLeftY = ((int) (terrain.topLeftCorner.y * SQUARES_PER_GAME_UNIT) / SQUARES_PER_GAME_UNIT);
        topLeftPos = new Vector2(topLeftX, topLeftY);
    }

    private void setMap(double[][] newMap) {
        map = newMap;
    }

    private void createNodeMap() {
        createdNodes = new Node[map.length][map[0].length];
    }

    private void setTarget(Terrain terrain) {
        if (terrain.target == null) {
            throw new NullPointerException("Target was null");
        }

        int gridXPosition = translateToGridXPosition(terrain.target.position.x);
        int gridYPosition = translateToGridYPosition(terrain.target.position.y);
        targetNode = new Node(gridXPosition, gridYPosition);
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

    private void setupSearch(Vector2 position) {
        int originXPosition = translateToGridXPosition(position.x);
        int originYPosition = translateToGridYPosition(position.y);
        originNode = new Node(originXPosition, originYPosition);
        originNode.setTarget(targetNode);
        originNode.setOrigin(originNode);
        targetNode.setTarget(targetNode);
        targetNode.setOrigin(originNode);
    }
    // endregion

    /**
     * @return the {@code length} of the shortest path from the ball to the target
     *         while avoiding water and obstacles.
     *         Returns {@code -1} if an unobstructed path does not exist.
     */
    private double aStarPathfinding() {
        LinkedList<Node> uncheckedNodes = new LinkedList<>();
        addCreatedNode(originNode);

        boolean foundPath = false;
        Node currentNode = originNode;

        while (!foundPath) {
            createSorroundingNodes(currentNode, uncheckedNodes);
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

    private void createSorroundingNodes(Node origin, LinkedList<Node> uncheckedNodes) {
        // All 8 sorrounding tiles are checked
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, 1, 0);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, 1, 1);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, 0, 1);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, -1, 1);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, -1, 0);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, -1, -1);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, 0, -1);
        tryCreateNodeAtDeltaPosition(origin, uncheckedNodes, 1, -1);
    }

    private void tryCreateNodeAtDeltaPosition(Node connectedTo, LinkedList<Node> uncheckedNodes, int deltaX,
            int deltaY) {
        int newXPos = connectedTo.xPosition + deltaX;
        int newYPos = connectedTo.yPosition + deltaY;

        if (nodeExistsAtPosition(newXPos, newYPos)) {
            Node nodeAtPosition = createdNodes[newYPos][newXPos];
            // If the distance from this node to the origin is closer, updates the distance
            // to be shorter
            nodeAtPosition.updateDistanceToOrigin(connectedTo);
            return;
        }
        if (canCreateNodeAt(newXPos, newYPos)) {
            // Creates node at position
            double costToEnter = map[newYPos][newXPos];
            Node newNode = new Node(newXPos, newYPos, connectedTo, costToEnter);
            addCreatedNode(newNode);
            int insertSpot = findInsertSpot(newNode, uncheckedNodes);
            uncheckedNodes.add(insertSpot, newNode);
        }
    }

    private boolean nodeExistsAtPosition(int x, int y) {
        if (!isPositionInRange(x, y)) {
            return false;
        }
        return createdNodes[y][x] != null;
    }

    private int findInsertSpot(Node newNode, LinkedList<Node> uncheckedNodes) {
        for (int i = 0; i < uncheckedNodes.size(); i++) {
            Node checkedNode = uncheckedNodes.get(i);
            if (checkedNode.nodeValue >= newNode.nodeValue
                    && checkedNode.distanceToTarget >= newNode.distanceToTarget) {
                return i;
            }
        }
        return uncheckedNodes.size();
    }

    private void addCreatedNode(Node node) {
        createdNodes[node.yPosition][node.xPosition] = node;
    }

    private boolean canCreateNodeAt(int xPos, int yPos) {
        // Position in game units
        if (!isPositionInRange(xPos, yPos)) {
            return false;
        }
        boolean spotIsBlocked = map[yPos][xPos] == -1; // There is an unpassable obstacle on this spot
        if (spotIsBlocked) {
            return false;
        }
        return true;
    }

    private boolean isPositionInRange(int xPos, int yPos) {
        boolean xPositionInRange = xPos > 0 && xPos < createdNodes[0].length;
        boolean yPositionInRange = yPos > 0 && yPos < createdNodes.length;
        return xPositionInRange && yPositionInRange;
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
        return uncheckedNodes.getFirst();
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
    /**
     * @return a game unit position translated into a grid position used by the
     *         pathfinding algorithm
     */
    private int translateToGridXPosition(double axisPosition) {
        return (int) ((axisPosition - topLeftPos.x) * SQUARES_PER_GAME_UNIT);
    }

    /**
     * @return a game unit position translated into a grid position used by the
     *         pathfinding algorithm
     */
    private int translateToGridYPosition(double axisPosition) {
        return (int) ((axisPosition - topLeftPos.y) * SQUARES_PER_GAME_UNIT);
    }
    // endregion

    private double translateResultToGameUnits(double gridDistance) {
        return (gridDistance / 10) / (double) SQUARES_PER_GAME_UNIT;
    }
}