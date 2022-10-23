package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionCallExpr extends Expr {

    final String funcName;
    final List<Expr> arguments;

    public FunctionCallExpr(String funcName, List<Expr> arguments, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.arguments = arguments;
    }

    public String getFuncName() {
        return funcName;
    }

    public List<Expr> getArgu() {
        return arguments;
    }

}