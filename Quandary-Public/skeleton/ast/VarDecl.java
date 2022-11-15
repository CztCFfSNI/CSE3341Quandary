package ast;

public class VarDecl {
    public static enum TYPE {
        INT,
        REF,
        Q
    }

    final boolean isMut;
    final TYPE t;
    final String ident;

    public VarDecl(boolean isMut, TYPE t, String ident) {
        this.isMut = isMut;
        this.t = t;
        this.ident = ident;
    }

    public boolean checkIsMut() {
        return isMut;
    }

    public TYPE getType() {
        return t;
    }

    public String getIdent() {
        return ident;
    }

}