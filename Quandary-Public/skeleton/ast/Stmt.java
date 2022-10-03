package ast;

import java.io.PrintStream;

abstract public class Stmt extends ASTNode {

    protected Stmt(Location loc) {
        super(loc);
    }

}