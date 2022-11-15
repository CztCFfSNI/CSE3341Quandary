package ast;

public class VarDeclStmt extends Stmt {

    final VarDecl v;
    final Expr e;
    
    public VarDeclStmt(VarDecl v, Expr expr, Location loc) {
        super(loc);
        this.v = v;
        this.e = expr;
    }

    public VarDecl getVar() {
        return v;
    }

    public Expr getExpr() {
        return e;
    }
}
