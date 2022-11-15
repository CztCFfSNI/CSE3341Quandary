package ast;

public class QInt extends QVal {
    public Long value;

    public QInt(long value) {
        this.value = value;
    }

    public Long returnQInt() {
        return value;
    }

    public String toString() {
        return Long.toString(value);
    }
}
