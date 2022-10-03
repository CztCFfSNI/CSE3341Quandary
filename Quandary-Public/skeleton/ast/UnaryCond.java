package ast;

public class UnaryCond extends Cond {

    public static final int NOT = 1;

    final int operator;
    final Cond cond;

    public UnaryCond(int operator, Cond cond, Location loc) {
        super(loc);
        this.operator = operator;
        this.cond = cond;
    }

    public int getOperator() {
        return operator;
    }

    public Cond getCond() {
        return cond;
    }

    @Override
    public String toString() {
        return "!( " + cond + ")";
    }

    
}
