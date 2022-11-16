package interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

import java.util.*;     // hash map

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static final HashMap<String, Object> mainVariables = new HashMap<>();
    public static final HashMap<String, FunctionDefinition> functions = new HashMap<>();

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        Long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, new QInt(quandaryArg)).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    QVal executeRoot(Program astRoot, QVal arg) {
        FunctionDefinitionList fdl = (FunctionDefinitionList)astRoot.getFL();
        for (FunctionDefinition f : fdl.getFL()) {
            functions.put(f.getVar().getIdent(), f);
        }

        FunctionDefinition mainFunction = fdl.getMain();
        VarDecl vd = mainFunction.getVar();
        mainFunction.getVariables().put(mainFunction.getParams().get(0).getIdent(), arg);

        for (VarDecl v : mainFunction.getParams()) {
            mainFunction.getType().put(v.getIdent(), v.getType());
            if (v.checkIsMut()) mainFunction.getMut().put(v.getIdent(), true);
            else mainFunction.getMut().put(v.getIdent(), false);
        }

        StmtList sl = (StmtList)mainFunction.getSl();
        for (Stmt s : sl.getSl()) {
            QVal ret = execute(s, mainFunction);
            if (ret != null) return ret;
        }
        return new QInt(0);
    }

    QVal execute(Stmt stmt, FunctionDefinition function) {
        if (stmt instanceof VarDeclStmt) {
            VarDeclStmt s = (VarDeclStmt)stmt;
            VarDecl vd = s.getVar();
            function.getVariables().put(vd.getIdent(), evaluate(s.getExpr(), function));
            function.getType().put(vd.getIdent(), vd.getType());
            if (vd.checkIsMut()) function.getMut().put(vd.getIdent(), true);
            else function.getMut().put(vd.getIdent(), false);
            return null;
        } else if (stmt instanceof DeclStmt) {
            DeclStmt s = (DeclStmt)stmt;
            function.getVariables().put(s.getIdentifier(), evaluate(s.getExpr(), function)); 
            // if (function.getMut().get(s.getIdentifier())) function.getVariables().put(s.getIdentifier(), evaluate(s.getExpr(), function)); 
            // else throw new RuntimeException("It's immutable!");
            return null;
        } else if (stmt instanceof IfStmt) {
            IfStmt s = (IfStmt)stmt;
            if ((Boolean)eval(s.getCond(), function)) return execute(s.getStmt(), function);
            return null;
        } else if (stmt instanceof IfElseStmt) {
            IfElseStmt s = (IfElseStmt)stmt;
            if ((Boolean)eval(s.getCond(), function)) return execute(s.getIf(), function);
            else return execute(s.getElse(), function);
        } else if (stmt instanceof WhileStmt) {
            WhileStmt s = (WhileStmt)stmt;
            while ((Boolean)eval(s.getCond(), function)) {
                QVal temp = execute(s.getStmt(), function);
                if (temp != null) return temp;
            }
            return null;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt s = (ReturnStmt)stmt;
            return evaluate(s.getExpr(), function);
        } else if(stmt instanceof PrintStmt) {
            PrintStmt s = (PrintStmt)stmt;
            System.out.println(evaluate(s.getExpr(), function).toString());
            return null;
        } else if(stmt instanceof StmtList){
            StmtList sl = (StmtList)stmt;
            for (Stmt s : sl.getSl()) {
                QVal temp = execute(s, function);
                if (temp != null) return temp;
            }
            return null;
        } else if(stmt instanceof FunctionCallStmt) {
            FunctionCallStmt s = (FunctionCallStmt)stmt;
            FunctionCallExpr e = s.getFCE();
            evaluate(e, function);
            return null;
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    QVal evaluate(Expr expr, FunctionDefinition localFunction) {
        if (expr instanceof ConstExpr) {
            if (((ConstExpr)expr).getValue() instanceof Long) return new QInt((long)((ConstExpr)expr).getValue());
            else if (((ConstExpr)expr).getValue() == null) return new QRef(null);
            throw new RuntimeException("Not QInt or Nil!");
        } else if (expr instanceof IdentExpr) {
            return (QVal)((localFunction.getVariables().get(((IdentExpr)expr).getValue())));
        } else if (expr instanceof CastExpr) {
            CastExpr castExpr = (CastExpr)expr;
            //VarDecl.TYPE t = castExpr.getType();
            //Expr e = castExpr.getExpr();
            System.out.println(castExpr.getType().equals(VarDecl.TYPE.INT));
            if (castExpr.getType().equals(VarDecl.TYPE.INT)) {
                return ((QInt)evaluate(castExpr.getExpr(), localFunction));
            } else if (castExpr.getType().equals(VarDecl.TYPE.REF)) {
                return ((QRef)evaluate(castExpr.getExpr(), localFunction));
            }
            else return evaluate(castExpr.getExpr(), localFunction);
            //System.out.println(((QInt)evaluate(castExpr.getExpr(), localFunction)).equals(new QRef(null))); 
            //if (evaluate(castExpr, localFunction).equals(new QRef(null)))System.out.println(31221); 
            //localFunction.getType().put(castExpr.getExpr().toString(), castExpr.getType());
            //return (QRef)(localFunction.getVariables().get(castExpr.getExpr().toString()));
        } else if (expr instanceof FunctionCallExpr) {
            FunctionCallExpr functionCallExpr = (FunctionCallExpr)expr;
            String funcName = functionCallExpr.getFuncName();
            List<Expr> arguments = functionCallExpr.getArgu();
            FunctionDefinition func = null;
            func = functions.get(funcName);

            if (funcName.equals("randomInt")) return randomInt(arguments.get(0), localFunction);
            if (funcName.equals("isNil")) return isNil(arguments.get(0), localFunction);
            if (funcName.equals("isAtom")) return isAtom(arguments.get(0), localFunction);
            if (funcName.equals("setLeft")) return setLeft(arguments.get(0), arguments.get(1), localFunction);
            if (funcName.equals("setRight")) return setRight(arguments.get(0), arguments.get(1), localFunction);
            if (funcName.equals("left")) return left(arguments.get(0), localFunction);
            if (funcName.equals("right")) return right(arguments.get(0), localFunction);

            if (func != null) {
                FunctionDefinition function = new FunctionDefinition(func.getVar(), new ArrayList<VarDecl>(), func.getSl().getSl(), func.getLoc());
                List<VarDecl> params = func.getParams();
                HashMap<String, QVal> localVariables = function.getVariables();
                for (int i = 0;i < params.size();i++) {
                    localVariables.put(params.get(i).getIdent(), evaluate(arguments.get(i), localFunction));
                }
                StmtList sl = (StmtList)function.getSl();
                for (Stmt s : sl.getSl()) {
                    QVal ret = execute(s, function);
                    if (ret != null) {
                        return ret;}
                }
            } 
            return null;
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), localFunction)).returnQInt() + ((QInt)evaluate(binaryExpr.getRightExpr(), localFunction)).returnQInt());
                case BinaryExpr.MINUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), localFunction)).returnQInt() - ((QInt)evaluate(binaryExpr.getRightExpr(), localFunction)).returnQInt());
                case BinaryExpr.TIMES: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), localFunction)).returnQInt() * ((QInt)evaluate(binaryExpr.getRightExpr(), localFunction)).returnQInt());
                case BinaryExpr.DOT: 
                return new QRef(new QObj(evaluate(binaryExpr.getLeftExpr(), localFunction), evaluate(binaryExpr.getRightExpr(), localFunction)));
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            switch (unaryExpr.getOperator()) {
                case UnaryExpr.NEGATIVE: return new QInt(-1 * ((QInt)evaluate(unaryExpr.getExpr(), localFunction)).returnQInt());
                default: throw new RuntimeException("Unhandled operator");
            }
        }
        else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    Object eval(Cond cond, FunctionDefinition localFunction) {
        if (cond instanceof CompCond) {
            CompCond c = (CompCond)cond;
            Long v1 = ((QInt)evaluate(c.getLeftExpr(), localFunction)).returnQInt();
            Long v2 = ((QInt)evaluate(c.getRightExpr(), localFunction)).returnQInt();
            switch (c.getOperator()) {
                case CompCond.LESSEQUAL: return v1 <= v2;
                case CompCond.LARGEEQUAL: return v1 >= v2;
                case CompCond.ISEQUAL: return v1.equals(v2);
                case CompCond.NOTEQUAL: return !v1.equals(v2);
                case CompCond.LESS: return v1 < v2;
                case CompCond.LARGER: return v1 > v2;
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (cond instanceof LogicalCond) {
            LogicalCond c = (LogicalCond)cond;
            switch (c.getOperator()) {
                case LogicalCond.AND: return (Boolean)eval(c.getLeftCond(), localFunction) && (Boolean)eval(c.getRightCond(), localFunction);
                case LogicalCond.OR: return (Boolean)eval(c.getLeftCond(), localFunction) || (Boolean)eval(c.getRightCond(), localFunction);
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (cond instanceof UnaryCond) {
            UnaryCond c = (UnaryCond)cond;
            switch (c.getOperator()) {
                case UnaryCond.NOT: return !(Boolean)eval(c.getCond(), localFunction);
                default: throw new RuntimeException("Unhandled operator");
            }
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    QVal randomInt (Expr e, FunctionDefinition f) {
        if (e instanceof ConstExpr) return new QInt((long)(((long)((ConstExpr)e).getValue()) * Math.random()));
        else return new QInt((long)((((QInt)f.getVariables().get(((IdentExpr)e).getValue())).returnQInt()) * Math.random()));
    }

    QVal left (Expr e, FunctionDefinition f) {
        return ((QRef)evaluate(e, f)).referent.left;
    }

    QVal right (Expr e, FunctionDefinition f) {
        return ((QRef)evaluate(e, f)).referent.right;
    }

    QInt isNil (Expr e, FunctionDefinition f) {
        QVal x = evaluate(e, f);
        if (x instanceof QInt) {
            return new QInt(0);
        } else if (x instanceof QRef) {
            if (((QRef)x).referent == null) return new QInt(1);
            else return new QInt(0);
        } else throw new RuntimeException("isNil Exception!");
    }

    QInt isAtom (Expr e, FunctionDefinition f) {
        QVal x = evaluate(e, f);
        if (x instanceof QInt) {
            return new QInt(1);
        } else if (x instanceof QRef) {
            if (((QRef)x).referent == null) return new QInt(1);
            else return new QInt(0);
        } else throw new RuntimeException("isNil Exception!");
    }

    QInt setLeft (Expr e1, Expr e2, FunctionDefinition f) {
        ((QRef)evaluate(e1, f)).referent.left = evaluate(e2, f);
        //f.getVariables().put(((IdentExpr)e1).getValue(), new QRef(new QObj(evaluate(e2, f), right(e1, f))));
        return new QInt(1);
    }

    QInt setRight (Expr e1, Expr e2, FunctionDefinition f) {
        ((QRef)evaluate(e1, f)).referent.right = evaluate(e2, f);
        //f.getVariables().put(((IdentExpr)e1).getValue(), new QRef(new QObj(left(e1, f), evaluate(e2, f))));
        return new QInt(1);
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
