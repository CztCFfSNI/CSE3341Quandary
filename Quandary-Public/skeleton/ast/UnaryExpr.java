package ast;

public class UnaryExpr extends Expr{

    public static final int NEGATIVE = 1;

    final int operator;
    final Expr expr;

    public UnaryExpr(int operator, Expr expr, Location loc) {
        super(loc);
        this.operator = operator;
        this.expr = expr;
    }

    public int getOperator() {
        return operator;
    }

    public Expr getExpr() {
        return expr;
    }
    
    public String simpleString() {
        String s = null;
        switch (operator) {
            case NEGATIVE:  s = "-"; break;
        }
        return s + " " + expr;
    }
    
}
