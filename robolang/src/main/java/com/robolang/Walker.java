package com.robolang;


import org.antlr.runtime.tree.Tree;

public class Walker {
    private Tree t;
    private String className;

    public Walker(Tree t, String className) {
        this.t = t;
        this.className = className;
    }

    public String getCode() {
        String tmp = "public class %s {";
        tmp += "public static void main(String[] args) {";
        tmp += "System.out.println(\"Hello World\");";
        tmp += "}";
        tmp += "}";
        return String.format(tmp, className);
    }
}
