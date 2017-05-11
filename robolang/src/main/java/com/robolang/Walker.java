package com.robolang;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.antlr.runtime.tree.Tree;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;


public class Walker {
    private Tree root;
    private String className;
    private MethodSpec.Builder mainFunc;
    private TypeSpec.Builder mainClass;

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

    private void getNodeCode(Tree t){
        switch (t.getType()) {
            case TParser.FUNCTION:
                MethodSpec.Builder func = MethodSpec.methodBuilder(t.getChild(0).getText())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .returns(getReturn(t));
                // TODO: Add parameters
                mainClass.addMethod(func.build());
                break;
        }
    }

    private Type getReturn(Tree t) {
        assert t.getType() == TParser.FUNCTION;
        if(t.getChildCount() == 2)
            return void.class;
        else if(t.getChildCount() == 3){
            if(t.getChild(2).getType() == TParser.RETURN)
                return getType(t.getChild(2).getChild(0), null);
            else
                return void.class;
        }
        else {
            return getType(t.getChild(3).getChild(0), t.getChild(1));
        }
    }

    private void getParams(Tree t, MethodSpec.Builder func) {
        assert t.getType() == TParser.PARAMS;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.getChildCount(); i++) {
            Type type = getType(t.getChild(i));
            func.addParameter(int.class, t.getChild(i).getText());
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

            default:
                return void.class;
        }
    }

    private Tree findInTree(String varName, Tree t) {
        // TODO
        return null;
    }
}
