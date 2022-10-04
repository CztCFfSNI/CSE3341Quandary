package ast;

public class CompCond extends Cond {

    public static final int LESSEQUAL = 1;
    public static final int LARGEEQUAL = 2;
    public static final int ISEQUAL = 3;
    public static final int NOTEQUAL = 4;
    public static final int LESS = 5;
    public static final int LARGER = 6;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public CompCond(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    public String toString() {
        String s = null;
        switch (operator) {
            case LESSEQUAL: s = "<="; break;
            case LARGEEQUAL: s = ">="; break;
            case ISEQUAL: s = "=="; break;
            case NOTEQUAL: s = "!="; break;
            case LESS: s = "<";  break;
            case LARGER: s = ">";  break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
