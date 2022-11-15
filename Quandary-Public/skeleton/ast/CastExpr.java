package ast;

public class CastExpr extends Expr {

    final VarDecl.TYPE t;
    final Expr e;

    public CastExpr(VarDecl.TYPE t, Expr e, Location loc) {
        super(loc);
        this.t = t;
        this.e = e;
    }

    public VarDecl.TYPE getType() {
        return t;
    }

    public Expr getExpr() {
        return e;
    }
}
