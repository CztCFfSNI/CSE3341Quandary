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

    public static final HashMap<String, Object> variables = new HashMap<>();
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
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
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

    Object executeRoot(Program astRoot, Long arg) {
        FunctionDefinitionList fdl = (FunctionDefinitionList)astRoot.getFL();
        for (FunctionDefinition f : fdl.getFL()) {
            functions.put(f.getFunctionName(), f);
        }

        FunctionDefinition mainFunction = fdl.getMain();
        mainFunction.getVariables().put(mainFunction.getParams().get(0), arg);
        StmtList sl = (StmtList)mainFunction.getSl();
        for (Stmt s : sl.getSl()) {
            Object ret = execute(s, mainFunction);
            if (ret != null) return ret;
        }
        return 0;
    }

    Object execute(Stmt stmt, FunctionDefinition function) {
        if (stmt instanceof DeclStmt) {
            DeclStmt s = (DeclStmt)stmt;
            function.getVariables().put(s.getIdentifier(), (Long)evaluate(s.getExpr(), function));
            return null;
        } else if (stmt instanceof IfStmt) {
            IfStmt s = (IfStmt)stmt;
            if ((Boolean)eval(s.getCond(), function)) return execute(s.getStmt(), function);
            return null;
        } else if (stmt instanceof IfElseStmt) {
            IfElseStmt s = (IfElseStmt)stmt;
            if ((Boolean)eval(s.getCond(), function)) return execute(s.getIf(), function);
            else return execute(s.getElse(), function);
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt s = (ReturnStmt)stmt;
            return (Long)evaluate(s.getExpr(), function);
        } else if(stmt instanceof PrintStmt) {
            PrintStmt s = (PrintStmt)stmt;
            System.out.println((Long)evaluate(s.getExpr(), function));
            return null;
        } else if(stmt instanceof StmtList){
            StmtList sl = (StmtList)stmt;
            for (Stmt s : sl.getSl()) {
                Object temp = execute(s, function);
                if (temp != null) return temp;
            }
            return null;
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    Object evaluate(Expr expr, FunctionDefinition localFunction) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        } else if (expr instanceof IdentExpr) {
            return localFunction.getVariables().get(((IdentExpr)expr).getValue());
        } else if (expr instanceof FunctionCallExpr) {
            FunctionCallExpr functionCallExpr = (FunctionCallExpr)expr;
            String funcName = functionCallExpr.getFuncName();
            List<Expr> arguments = functionCallExpr.getArgu();
            FunctionDefinition func = null;
            func = functions.get(funcName);

            if (funcName.equals("randomInt")) {
                long num;
                if (arguments.get(0) instanceof ConstExpr) num = (long)((ConstExpr) arguments.get(0)).getValue();
                else num = (long)localFunction.getVariables().get(((IdentExpr)arguments.get(0)).getValue());
                return (long)(num * Math.random());
            }
            if (func != null) {
                FunctionDefinition function = new FunctionDefinition(funcName, new ArrayList<String>(), func.getSl().getSl(), func.getLoc());
                List<String> params = func.getParams();
                HashMap<String, Object> localVariables = function.getVariables();
                for (int i = 0;i < params.size();i++) {
                    localVariables.put(params.get(i), (Long)evaluate(arguments.get(i), localFunction));
                }
                StmtList sl = (StmtList)function.getSl();
                for (Stmt s : sl.getSl()) {
                    Object ret = execute(s, function);
                    if (ret != null) return ret;
                }
            } 
            return null;
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return (Long)evaluate(binaryExpr.getLeftExpr(), localFunction) + (Long)evaluate(binaryExpr.getRightExpr(), localFunction);
                case BinaryExpr.MINUS: return (Long)evaluate(binaryExpr.getLeftExpr(), localFunction) - (Long)evaluate(binaryExpr.getRightExpr(), localFunction);
                case BinaryExpr.TIMES: return (Long)evaluate(binaryExpr.getLeftExpr(), localFunction) * (Long)evaluate(binaryExpr.getRightExpr(), localFunction);
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            switch (unaryExpr.getOperator()) {
                case UnaryExpr.NEGATIVE: return -1 * (Long)evaluate(unaryExpr.getExpr(), localFunction);
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
            Long v1 = (Long)evaluate(c.getLeftExpr(), localFunction);
            Long v2 = (Long)evaluate(c.getRightExpr(), localFunction);
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

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
