package ast;

import java.util.concurrent.atomic.*;

public class QRef extends QVal {
    public QObj referent;
    private AtomicBoolean lock = new AtomicBoolean(false);

    public QRef(QObj referent) {
        this.referent = referent;
    }

    public String toString() {
        if (referent == null) return "nil";
        else return referent.toString();
    }

    public void acq() {
        while (!this.lock.compareAndSet(false, true)) {}
    }

    public void rel() {
        lock.set(false);
    }
}

