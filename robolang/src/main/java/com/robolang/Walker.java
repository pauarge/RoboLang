package com.robolang;

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
                        // TODO: .returns(getRetType(child)
                        .returns(void.class);
                // TODO: Add parameters
                mainClass.addMethod(func.build());
                break;
        }
    }

    private Type getRetType(Tree t) {
        // TODO: Get return type of a function
        assert t.getText().equals("FUNCTION");
        return null;
    }

    private String getParams(Tree t) {
        // TODO: Get parameters of a function
        assert t.getText().equals("PARAMS");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.getChildCount(); i++) {

        }
        return sb.toString();
    }

    private Type getType(Tree t) {
        return void.class;
    }
}
