package finterpreter;

public class ParserNode {
    private Token element;
    private ParserNode leftChild;
    private ParserNode rightChild;
    private double replaceValue;
    private ParserNode parent;

    public ParserNode(Token element) {
        this.element = element;
        this.leftChild = null;
        this.rightChild = null;
        this.replaceValue = 0;
        this.parent = null;
    }

    public ParserNode() {
        this(null);
    }

    public void setReplaceValue(double val) {
        this.replaceValue = val;
    }

    public void setElement(Token element) {
        this.element = element;
    }

    public void setLeftChild(ParserNode child) {
        this.leftChild = child;
    }

    public void setRightChild(ParserNode child) {
        this.rightChild = child;
    }

    public void setParent(ParserNode parent) {
        this.parent = parent;
    }

    public Token getElement() {
        return element;
    }

    public ParserNode getParent() {
        return parent;
    }

    public ParserNode getLeftChild() {
        return leftChild;
    }

    public ParserNode getRightChild() {
        return rightChild;
    }

    public double getValue() {
        if (element == null) {
            throw new RuntimeException("Null element");
        }
        if (element.getType() == Token.Type.NUM) {
            return Double.parseDouble(element.getText());
        } else if (element.getType() == Token.Type.PI) {
            return Math.PI;
        } else if (element.getType() == Token.Type.E) {
            return Math.E;
        } else if (element.getType() == Token.Type.PLUS) {
            return leftChild.getValue() + rightChild.getValue();
        } else if (element.getType() == Token.Type.MINUS) {
            return leftChild.getValue() - rightChild.getValue();
        } else if (element.getType() == Token.Type.MULT) {
            return leftChild.getValue() * rightChild.getValue();
        } else if (element.getType() == Token.Type.DIV) {
            return leftChild.getValue() / rightChild.getValue();
        } else if (element.getType() == Token.Type.POW) {
            return Math.pow(leftChild.getValue(), rightChild.getValue());
        } else if (element.getType() == Token.Type.WORD) {
            return replaceValue;
        } else if (element.getType() == Token.Type.FUNCTION) {
            if (element.getText().equals("sin")) {
                return Math.sin(leftChild.getValue());
            } else if (element.getText().equals("cos")) {
                return Math.cos(leftChild.getValue());
            } else if (element.getText().equals("tan")) {
                return Math.tan(leftChild.getValue());
            } else {
                throw new RuntimeException("Unknown function name "+element.getText()+" at position "+element.getPosition());
            }
        } else {
            throw new RuntimeException("Non-operator/value token type "+element.getText()+" at position "+element.getPosition());
        }
    }
}
