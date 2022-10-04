package ast;

public class DeclStmt extends Stmt {
    
    final String identifier;
    final Expr expr;

    public DeclStmt(String ident, Expr expr, Location loc) {
        super(loc);
        this.identifier = ident;
        this.expr = expr;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "int " + this.identifier + " = " + this.expr;
    }
}
