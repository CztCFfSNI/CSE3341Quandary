package ast;

public class WhileStmt extends Stmt {

    final Cond cond;
    final Stmt stmt;
    
    public WhileStmt(Cond cond, Stmt statement, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt = statement;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }
}
