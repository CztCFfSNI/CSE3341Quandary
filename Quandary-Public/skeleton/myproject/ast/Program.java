package ast;

import java.util.*;
import java.io.PrintStream;

public class Program extends ASTNode {

    // final String func;
    // final String arg;
    // final StmtList sl;
    final FunctionDefinitionList functionDefs;

    public Program(List<FunctionDefinition> funcDefs, Location loc) {
        super(loc);
        this.functionDefs = new FunctionDefinitionList(funcDefs, loc);
    }

    public FunctionDefinitionList getFL() {
        return functionDefs;
    }

    // public Program(String funcName, String arg, List<Stmt> sl, Location loc) {
    //     super(loc);
    //     this.func = funcName;
    //     this.arg = arg;
    //     this.sl = new StmtList(sl, loc);
    // }

    // public StmtList getSl() {
    //     return sl;
    // }

    // public String getArg() {
    //     return arg;
    // }

    // public String toString() {
    //     String s = "int " + func + "(int " + arg + ") " + sl;
    //     return s;
    // }

}
