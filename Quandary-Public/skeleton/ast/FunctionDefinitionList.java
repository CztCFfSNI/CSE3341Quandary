package ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionDefinitionList extends ASTNode {

    private static final String mainFunction = "main";

    final List<FunctionDefinition> functionDefs;

    public FunctionDefinitionList(List<FunctionDefinition> functionDefs, Location loc) {
        super(loc);
        this.functionDefs = functionDefs;
    }

    public List<FunctionDefinition> getFL() {
        return functionDefs;
    }

    public FunctionDefinition getMain() {
        if (functionDefs != null && functionDefs.size() > 0) {
            for (int i = 0; i < functionDefs.size(); i++) {
                if (functionDefs.get(i).getVar().getIdent().equals(mainFunction)) {
                    return functionDefs.get(i);
                }
            }
        }
        return null;
    }
}