package ast;

import java.util.HashMap;

import ast.QVal;
import interpreter.Interpreter;

public class QThread extends Thread {

    private final FunctionDefinition f;
    private final Expr e;
    public QVal v;

    public QThread(Expr e, FunctionDefinition f) {
        this.e = e;
        this.f = f;
    }

    public Expr getExpr() {
        return e;
    }

    @Override
    public void run() {
		v = Interpreter.getInterpreter().evaluate(e, f);
    }
}