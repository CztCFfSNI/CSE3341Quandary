package ast;

public class ConstExpr extends Expr {

    final Long value;

    public ConstExpr(long value, Location loc) {
        super(loc);
        this.value = value;
    }

    public ConstExpr(Location loc) {
        super(loc);
        this.value = null;
    }

    public Long getValue() {
        return value;
    }

    public String toString() {
        if (value == null) return "nil";
        else return value.toString();
    }
}
