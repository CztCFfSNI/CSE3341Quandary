package ast;

import java.util.List;

import ast.Stmt;

import java.io.PrintStream;

public class StmtList extends Stmt {

    final List<Stmt> sl;

    public StmtList(List<Stmt> sl, Location loc) {
        super(loc);
        this.sl = sl;
    }

    public List<Stmt> getSl() {
        return sl;
    }
}
