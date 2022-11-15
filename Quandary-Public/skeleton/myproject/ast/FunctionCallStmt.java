package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionCallStmt extends Stmt {

    final FunctionCallExpr f;

    public FunctionCallStmt(FunctionCallExpr f, Location loc) {
        super(loc);
        this.f = f;
    }

    public FunctionCallExpr getFCE() {
        return f;
    }

}