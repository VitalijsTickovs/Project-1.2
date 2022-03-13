package finterpreter;

/**
 * Used for storing tokens of the function
 */
public class Token {
    public static enum Type {
        // Operations
        PLUS, // +
        MINUS, // -
        MULT, // *
        DIV, // /
        POW, // **
        OPEN, // (
        CLOSE, // )
        // Variables
        WORD, // Any word
        FUNCTION, // A function - defined as a word followed by brackets
        // Constants
        E, // e
        PI, // pi
        // Numbers
        NUM // Any number
    }

    private Type type;
    private String text;
    private int position;

    public Token(Type type, String text, int position) {
        this.type = type;
        this.text = text;
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    @Override 
    public String toString() {
        return "Token([type: "+type.name()+", text: "+text+", position: "+position+"])";
    }
}
