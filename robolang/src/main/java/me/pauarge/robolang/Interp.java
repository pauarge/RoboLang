package me.pauarge.robolang;

import org.antlr.runtime.tree.Tree;

import java.util.HashMap;


public class Interp {
    Tree root;
    private HashMap<String, Tree> functions;

    protected Interp(Tree T) {
        assert T != null;
        root = T;
        mapFunctions();
    }

    public void run() {
        System.out.println(root.getText());
    }

    public String getStackTrace() {
        return "TODO";
    }

    public String getStackTrace(int nitems) {
        return "TODO";
    }

    private void mapFunctions() {
        functions = new HashMap<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            Tree f = root.getChild(i);
            if (f.getType() == TParser.FUNCTION) {
                String fname = f.getChild(0).getText();
                if (functions.containsKey(fname)) {
                    throw new RuntimeException("Multiple definitions of function " + fname);
                }
                functions.put(fname, f);
            }
        }
    }



}
