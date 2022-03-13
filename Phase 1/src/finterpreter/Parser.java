package finterpreter;

import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    public static ParserNode parse(ArrayList<Token> tokens) {
        ParserNode currentNode = new ParserNode();
        for (Token t : tokens) {
            if (t.getType() == Token.Type.OPEN) {
                ParserNode newNode = new ParserNode();
                currentNode.setLeftChild(newNode);
                newNode.setParent(currentNode);
                currentNode = newNode;
            } else if (t.getType() == Token.Type.NUM || t.getType() == Token.Type.E
                    || t.getType() == Token.Type.PI || t.getType() == Token.Type.WORD) {
                currentNode.setElement(t);
                if (currentNode.getParent() == null) {
                    ParserNode newNode = new ParserNode();
                    newNode.setLeftChild(currentNode);
                    currentNode.setParent(newNode);
                    currentNode = newNode;
                } else {
                    currentNode = currentNode.getParent();
                }
            } else if (t.getType() == Token.Type.CLOSE) {
                if (currentNode.getParent() == null) {
                    ParserNode newNode = new ParserNode();
                    newNode.setLeftChild(currentNode);
                    currentNode.setParent(newNode);
                    currentNode = newNode;
                } else {
                    currentNode = currentNode.getParent();
                }
            } else if (t.getType() == Token.Type.FUNCTION) {
                currentNode.setElement(t);
            } else if (t.getType() == Token.Type.PLUS || t.getType() == Token.Type.MINUS 
                    || t.getType() == Token.Type.MULT || t.getType() == Token.Type.DIV
                    || t.getType() == Token.Type.POW) {
                while (currentNode.getElement() != null) {
                    if (currentNode.getParent() == null) {
                        ParserNode newNode = new ParserNode();
                        newNode.setLeftChild(currentNode);
                        currentNode.setParent(newNode);
                        currentNode = newNode;
                    } else {
                        currentNode = currentNode.getParent();
                    }
                }
                currentNode.setElement(t);
                ParserNode newNode = new ParserNode();
                newNode.setParent(currentNode);
                currentNode.setRightChild(newNode);
                currentNode = newNode;
            }
        }
        // Find the root node
        while (currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
        }
        // Remove empty nodes
        removeEmptyNodes(currentNode);
        // Find the root again
        while (currentNode.getElement() == null) {
            currentNode = currentNode.getLeftChild();
        }
        while (currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
        }
        return currentNode;
    }

    public static void removeEmptyNodes(ParserNode root) {
        if (root.getElement() == null) {
            if (root.getParent() != null) {
                if (root.getParent().getLeftChild() == root) {
                    if (root.getLeftChild() != null) {
                        root.getParent().setLeftChild(root.getLeftChild());
                    } else if (root.getRightChild() != null) {
                        root.getParent().setLeftChild(root.getRightChild());
                    } else {
                        root.getParent().setLeftChild(null);
                    }
                } else if (root.getParent().getRightChild() == root) {
                    if (root.getLeftChild() != null) {
                        root.getParent().setRightChild(root.getLeftChild());
                    } else if (root.getRightChild() != null) {
                        root.getParent().setRightChild(root.getRightChild());
                    } else {
                        root.getParent().setRightChild(null);
                    }
                }
            } else {
                if (root.getLeftChild() != null) {
                    root.getLeftChild().setParent(null);
                }
            }
        }
        if (root.getLeftChild() != null) {
            removeEmptyNodes(root.getLeftChild());
        }
        if (root.getRightChild() != null) {
            removeEmptyNodes(root.getRightChild());
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String function = s.nextLine();
        s.close();

        ArrayList<Token> tokens = Lexer.lex(function);
        ParserNode root = parse(tokens);
        System.out.println(root.getValue());

    }
}
