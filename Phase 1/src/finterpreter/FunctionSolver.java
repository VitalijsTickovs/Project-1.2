package finterpreter;

import java.util.Scanner;

public class FunctionSolver {
    public static double solve(String function, String[] varNames, double[] varValues) {
        if (varNames.length != varValues.length) {
            throw new RuntimeException("Variable names and values must have the same number of elements");
        }
        ParserNode root = Parser.parse(Lexer.lex(function));
        assignValues(root, varNames, varValues);
        return root.getValue();
    }

    private static void assignValues(ParserNode root, String[] varNames, double[] varValues) {
        if (root.getElement().getType() == Token.Type.WORD) {
            for (int i=0; i<varNames.length; i++) {
                if (root.getElement().getText().equals(varNames[i])) {
                    root.setReplaceValue(varValues[i]);
                    break;
                }
            }
        }
        if (root.getLeftChild() != null) {
            assignValues(root.getLeftChild(), varNames, varValues);
        }
        if (root.getRightChild() != null) {
            assignValues(root.getRightChild(), varNames, varValues);
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String function = s.nextLine();
        s.close();

        System.out.println(solve(function, new String[] {"x", "y"}, new double[] {1.0, 2.0}));
    }
}
