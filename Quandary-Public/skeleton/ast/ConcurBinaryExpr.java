package ast;

import ast.*;

public class ConcurBinaryExpr extends Expr {

    final BinaryExpr e;

    public ConcurBinaryExpr(BinaryExpr e, Location loc) {
        super(loc);
        this.e = e;
    }

    public BinaryExpr getExpr() {
        return e;
    }

}
