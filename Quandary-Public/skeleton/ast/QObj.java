package ast;

import ast.QInt;

public class QObj extends QVal {
    public QVal left;
    public QVal right;

    public QObj(QVal left, QVal right) {
        this.left = left;
        this.right = right;
    }

    public QVal getLeft() {
        return left;
    }

    public QVal getRight() {
        return right;
    }

    public String toString() {
        String l = "";
        if (left == null) l = "nil";
        else l = left.toString();
        String r = "";
        if (right == null) r = "nil";
        else r = right.toString();

        return "(" + l + " . " + r + ")";
    }

}
