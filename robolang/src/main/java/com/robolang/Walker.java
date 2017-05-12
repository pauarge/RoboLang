package com.robolang;

import com.squareup.javapoet.*;
import org.antlr.runtime.tree.Tree;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;


public class Walker {

    private Tree root;
    private String className;
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
        MethodSpec.Builder mainFunc = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args");

        mainClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        getChildCode(root, mainFunc);
        mainClass.addMethod(mainFunc.build());

        JavaFile javaFile = JavaFile.builder("com.robolang", mainClass.build()).build();
        return javaFile.toString();
    }

    private void getChildCode(Tree t, MethodSpec.Builder func) {
        assert t.getType() == TParser.LIST_INSTR;
        for (int i = 0; i < t.getChildCount(); i++) {
            CodeBlock block = getNodeCode(t.getChild(i));
            if (block != null) {
                func.addStatement(block.toString());
            }
        }
    }

    private CodeBlock getNodeCode(Tree t) {
        CodeBlock.Builder block = CodeBlock.builder();
        CodeBlock c0;
        CodeBlock c1;

        switch (t.getType()) {
            case TParser.ADD:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add(c0);
                block.add("+");
                block.add(c1);
                return block.build();

            case TParser.AND:
                return null;

            case TParser.ASSIGN:
                Type type = getType(t.getChild(1), null);
                assert t.getChild(0).getType() == TParser.VAR;
                block.add(type.toString() + " ");
                block.add(t.getChild(0).getText());
                block.add("=");
                block.add(getNodeCode(t.getChild(1)));
                return block.build();

            case TParser.FUNCTION:
                MethodSpec.Builder f = MethodSpec.methodBuilder(t.getChild(0).getText())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .returns(getReturn(t));
                addParams(t, f);
                if (t.getChildCount() > 2 && t.getChild(2).getType() == TParser.LIST_INSTR)
                    getChildCode(t.getChild(2), f);
                mainClass.addMethod(f.build());
                return null;

            case TParser.NUM:
                block.add(t.getText());
                return block.build();

            default:
                return null;
        }
    }

    private Type getType(Tree t0, Tree t1) {
        switch (t0.getType()) {

            case TParser.ADD:
            case TParser.DIV:
            case TParser.MOD:
            case TParser.SUB:
            case TParser.TIMES:
                Type tp0 = getType(t0.getChild(0), t1);
                Type tp1 = getType(t0.getChild(1), t1);
                assert tp0 == tp1;
                return tp0;

            case TParser.AND:
            case TParser.EQ:
            case TParser.FALSE:
            case TParser.GET:
            case TParser.GT:
            case TParser.LET:
            case TParser.LT:
            case TParser.NEQ:
            case TParser.NOT:
            case TParser.OR:
            case TParser.TRUE:
                return boolean.class;

            case TParser.MR:
            case TParser.STRING:
            case TParser.ARRAY_EXPR:
                return String.class;

            case TParser.NUM:
                return double.class;

            case TParser.VAR:
                assert t1 != null;
                Tree t = findInTree(t0.getText(), t1);
                return getType(t, null);

            case TParser.ARRAY:
                return ArrayTypeName.class;

            case TParser.FUNCALL:
                return getReturn(t0);

            case TParser.ASSIGN:
                return void.class;

            default:
                return void.class;
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
        assert t.getType() == TParser.FUNCTION;
        Tree params = t.getChild(1);
        for (int i = 0; i < params.getChildCount(); i++) {
            Type type = getType(params.getChild(i), t.getChild(2));
            func.addParameter(type, params.getChild(i).getText());
        }
    }

    private Tree findInTree(String varName, Tree t) {
        if (t.getText().equals(varName)) {
            // We get an assignation of our variable, we get the tree at which is assigned
            if (t.getParent().getType() == TParser.ASSIGN)
                return t.getParent().getChild(1);
        }

        for (int i = 0; i < t.getChildCount(); i++) {
            Tree tmp = findInTree(varName, t.getChild(i));
            if (tmp != null) return tmp;
        }

        return null;
    }
}
