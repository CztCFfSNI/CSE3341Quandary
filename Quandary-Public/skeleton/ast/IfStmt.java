package ast;

public class IfStmt extends Stmt {
    
    final Cond cond;
    final Stmt stmt;

    public IfStmt(Cond cond, Stmt statement, Location loc) {
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

    // @Override
    // public String toString() {
    //     return "if (" + this.cond.toString() + ") \n\r\t" + this.stmt.toString();
    // }
}
