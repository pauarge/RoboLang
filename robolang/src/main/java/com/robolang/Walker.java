package com.robolang;

import com.squareup.javapoet.*;
import org.antlr.runtime.tree.Tree;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;


public class Walker {

    private Tree root;
    private String className;
    private MethodSpec.Builder mainFunc;
    private TypeSpec.Builder mainClass;

    private ClassName lejosButton = ClassName.get("lejos.nxt", "Button");
    private ClassName lejosDelay = ClassName.get("lejos.nxt", "Delay");
    private ClassName lejosNXTRegulatedMotor = ClassName.get("lejos.nxt", "NXTRegulatedMotor");

    public Walker(Tree t, String className) {
        this.root = t;
        this.className = className;
        assert root.getText().equals("LIST_INSTR");
    }

    public String getCode() {
        mainFunc = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args");
        mainClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(mainFunc.build());

        getChildCode();

        JavaFile javaFile = JavaFile.builder("com.robolang", mainClass.build()).build();
        return javaFile.toString();
    }

    private void getChildCode() {
        for (int i = 0; i < root.getChildCount(); i++) {
            getNodeCode(root.getChild(i));
        }
    }

    private void getNodeCode(Tree t) {
        switch (t.getType()) {
            case TParser.FUNCTION:
                MethodSpec.Builder func = MethodSpec.methodBuilder(t.getChild(0).getText())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .returns(getReturn(t));
                addParams(t, func);
                mainClass.addMethod(func.build());
                break;
        }
    }

    private Type getReturn(Tree t) {
        assert t.getType() == TParser.FUNCTION;
        Tree list_instr;
        Tree return_tree;

        if (t.getChildCount() == 2)
            return void.class;
        else if (t.getChildCount() == 3) {
            if (t.getChild(2).getType() == TParser.LIST_INSTR) {
                return void.class;
            } else {
                return_tree = t.getChild(2);
                return getType(return_tree.getChild(0), null);
            }
        } else {
            list_instr = t.getChild(2);
            return_tree = t.getChild(3);
            return getType(return_tree.getChild(0), list_instr.getChild(0));
        }
    }

    private void addParams(Tree t, MethodSpec.Builder func) {
        assert t.getType() == TParser.LIST_INSTR;
        Tree params = t.getChild(1);
        for (int i = 0; i < params.getChildCount(); i++) {
            Type type = getType(params.getChild(i), t.getChild(2));
            func.addParameter(type, params.getChild(i).getText());
        }
    }

    private Type getType(Tree t0, Tree t1) {
        switch (t0.getType()) {

            case TLexer.ADD:
            case TLexer.DIV:
            case TLexer.MOD:
            case TLexer.SUB:
            case TLexer.TIMES:
                Type tp0 = getType(t0.getChild(0), t1);
                Type tp1 = getType(t0.getChild(1), t1);
                assert tp0 == tp1;
                return tp0;

            case TLexer.AND:
            case TLexer.EQ:
            case TLexer.FALSE:
            case TLexer.GET:
            case TLexer.GT:
            case TLexer.LET:
            case TLexer.LT:
            case TLexer.NEQ:
            case TLexer.NOT:
            case TLexer.OR:
            case TLexer.TRUE:
                return boolean.class;

            case TLexer.MR:
            case TLexer.STRING:
            case TParser.ARRAY_EXPR:
                return String.class;

            case TLexer.NUM:
                return double.class;

            case TLexer.VAR:
                assert t1 != null;
                Tree t = findInTree(t0.getText(), t1);
                return getType(t, null);

            case TParser.ARRAY:
                return ArrayTypeName.class;

            case TParser.FUNCALL:
                return getReturn(t0);

            case TLexer.ASSIGN:
                return void.class;

            default:
                return void.class;
        }
    }

    private Tree findInTree(String varName, Tree t) {
        if (t.getText().equals(varName)){
            // We get an assignation of our variable, we get the tree at which is assigned
            if(t.getParent().getType() == TLexer.ASSIGN)
                return t.getParent().getChild(1);
        }

        for (int i = 0; i < t.getChildCount(); i++) {
            Tree tmp = findInTree(varName, t.getChild(i));
            if (tmp != null) return tmp;
        }

        return null;
    }
}
