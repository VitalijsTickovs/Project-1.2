package botheuristics;

import java.util.LinkedList;

public class AStar {

    public static void main(String[] args) {
        double[][] map = {
            {1, 1,-1, 1},
            {1, 1,-1, 1},
            {1,-1,-1, 1},
            {1, 1, 1, 1}};
            AStar aStar = new AStar(map, 3, 3);
            double distance = aStar.getPositionDistance(0, 0);
            System.out.println(distance);
    }

    /**
     * 
     * @param newMap use -1 for an unpassable obstacle; values signify the cost to enter this tile
     * @param targetX
     * @param targetY
     */
    public AStar(double[][] newMap, int targetX, int targetY){
        setMap(newMap);
        setTarget(targetX, targetY);
        checkForNullValues();
    }
    
    private double[][] map;
    
    private Node originNode;
    private Node targetNode;
    
    //These 2 methods shall be called before the first call of "getPositionDistance"
    public void setMap(double[][] newMap){
        map = newMap;
    }
    
    public void setTarget(int targetX, int targetY){
        targetNode = new Node(targetX, targetY);
    }
    
    public double getPositionDistance(int originXPosition, int originYPosition){
        checkForNullValues();
        setupSearch(originXPosition, originYPosition);
        return aStarPathfinding(originXPosition, originYPosition);
    }
    
    private void checkForNullValues(){
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

    private double aStarPathfinding(int originXPosition, int originYPosition){
        LinkedList<Node> createdNodes = new LinkedList<>();
        LinkedList<Node> uncheckedNodes = new LinkedList<>();
        createdNodes.add(originNode);

        boolean foundPath = false;
        Node currentNode = originNode;

        while (!foundPath) {
            createSorroundingNodes(currentNode, createdNodes, uncheckedNodes);
            currentNode = findNodeWithLowestValue(uncheckedNodes);
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
        Node nodeWithLowestValue = createdNodes.getFirst();
        for (Node node : createdNodes) {
            if (node.nodeValue < nodeWithLowestValue.nodeValue) {
                nodeWithLowestValue = node;
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
}
