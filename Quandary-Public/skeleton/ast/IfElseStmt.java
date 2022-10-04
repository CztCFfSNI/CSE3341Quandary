package ast;

import ast.Stmt;

public class IfElseStmt extends Stmt {
    
    final Cond cond;
    final Stmt ifStmt;
    final Stmt elseStmt;

    public IfElseStmt(Cond cond, Stmt statement1, Stmt elseStatement, Location loc) {
        super(loc);
        this.cond = cond;
        this.ifStmt = statement1;
        this.elseStmt = elseStatement;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getIf() {
        return ifStmt;
    }

    public Stmt getElse() {
        return elseStmt;
    }

}
