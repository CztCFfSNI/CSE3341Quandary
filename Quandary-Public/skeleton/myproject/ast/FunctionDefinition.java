package ast;

import java.util.HashMap;
import java.util.List;

import ast.Location;
import ast.QVal;
import ast.Stmt;

public class FunctionDefinition extends ASTNode {

    final VarDecl v;
    final List<VarDecl> params;
    final StmtList stmts;
    final Location loc;
    final HashMap<String, QVal> variables = new HashMap<>();
    final HashMap<String, VarDecl.TYPE> typeMap = new HashMap<>();
    final HashMap<String, Boolean> mutMap = new HashMap<>();

    public FunctionDefinition(VarDecl v, List<VarDecl> params, List<Stmt> stmts, Location loc) {
        super(loc);
        this.loc = loc;
        this.v = v;
        this.params = params;
        this.stmts = new StmtList(stmts, loc);
    }

    public VarDecl getVar() {
        return v;
    }

    public List<VarDecl> getParams() {
        return params;
    }

    public StmtList getSl() {
        return stmts;
    }

    public Location getLoc() {
        return loc;
    }

    public HashMap<String, QVal> getVariables() {
        return variables;
    }

    public HashMap<String, VarDecl.TYPE> getType() {
        return typeMap;
    }

    public HashMap<String, Boolean> getMut() {
        return mutMap;
    }

}