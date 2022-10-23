package ast;

import java.util.HashMap;
import java.util.List;

import ast.Location;
import ast.Stmt;

public class FunctionDefinition extends ASTNode {

    final String funcName;
    final List<String> params;
    final StmtList stmts;
    final Location loc;
    final HashMap<String, Object> variables = new HashMap<>();

    public FunctionDefinition(String funcName, List<String> params, List<Stmt> stmts, Location loc) {
        super(loc);
        this.loc = loc;
        this.funcName = funcName;
        this.params = params;
        this.stmts = new StmtList(stmts, loc);
    }

    public String getFunctionName() {
        return funcName;
    }

    public List<String> getParams() {
        return params;
    }

    public StmtList getSl() {
        return stmts;
    }

    public Location getLoc() {
        return loc;
    }

    public HashMap<String, Object> getVariables() {
        return variables;
    }

}