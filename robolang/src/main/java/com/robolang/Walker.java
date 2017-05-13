package com.robolang;

import com.squareup.javapoet.*;
import org.antlr.runtime.tree.Tree;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Walker {

    private Tree root;
    private int ForCount;
    private String className;
    private TypeSpec.Builder mainClass;
    private Map<String, Type> symTable;

    private ClassName lejosButton = ClassName.get("lejos.nxt", "Button");
    private ClassName lejosDelay = ClassName.get("lejos.nxt", "Delay");
    private ClassName lejosNXTRegulatedMotor = ClassName.get("lejos.nxt", "NXTRegulatedMotor");

    public Walker(Tree t, String className) {
        ForCount = 0;
        this.root = t;
        this.className = className;
        this.symTable = new HashMap<>();
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
                block.add("(");
                block.add(c0);
                block.add("+");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.AND:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("&&");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.OR:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("||");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.DIV:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("/");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.GT:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add(">");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.GET:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add(">=");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.ASSIGN:
                Type type = getType(t.getChild(1), null);
                String auxName = getFunctionName(t)+"_"+t.getChild(0).getText();
                boolean firstTime = false;
                if (!symTable.containsKey(auxName)) {
                    firstTime = true;
                    symTable.put(auxName, type);
                }
                assert t.getChild(0).getType() == TParser.VAR;
                if(type == ArrayTypeName.class){
                    block.add("String ");
                    block.add(t.getChild(0).getText() + "[]");
                    block.add("=");
                    block.add(getNodeCode(t.getChild(1)));
                }
                else {
                    if(firstTime) {
                        block.add(type.toString() + " ");
                    }
                    block.add(t.getChild(0).getText());
                    block.add("=");
                    block.add(getNodeCode(t.getChild(1)));
                }
                return block.build();

            case TParser.ARRAY:
                int size = t.getChildCount();
                String s = "{";
                boolean first = true;
                for(int i = 0; i < size; ++i) {
                    if(first) {
                        first = false;
                    }
                    else {
                        s += ",";
                    }
                    s =  s + "\"" +t.getChild(i).getText() + "\"";
                }
                s += "}";
                block.add(s);
                return block.build();


            case TParser.FUNCTION:
                MethodSpec.Builder f = MethodSpec.methodBuilder(t.getChild(0).getText())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .returns(getReturn(t));
                addParams(t, f);
                if (t.getChildCount() == 3) {
                    if (t.getChild(2).getType() == TParser.LIST_INSTR) {
                        getChildCode(t.getChild(2), f);
                    } else {
                        f.addStatement(getNodeCode(t.getChild(2)).toString());
                    }
                } else if (t.getChildCount() == 4) {
                    getChildCode(t.getChild(2), f);
                    f.addStatement(getNodeCode(t.getChild(3)).toString());
                }
                mainClass.addMethod(f.build());
                return null;

            case TParser.TRUE:
            case TParser.FALSE:
            case TParser.NUM:
            case TParser.VAR:
                block.add(t.getText());
                return block.build();

            case TParser.LT:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("<");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.LET:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("<=");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.EQ:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("==");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.TIMES:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c0);
                block.add("*");
                block.add(c1);
                block.add(")");
                return block.build();

            case TParser.SUB:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c1);
                block.add("-");
                block.add(c0);
                block.add(")");
                return block.build();

            case TParser.MOD:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c1);
                block.add("%");
                block.add(c0);
                block.add(")");
                return block.build();

            case TParser.NOT:
                c0 = getNodeCode(t.getChild(0));
                block.add("(");
                block.add("!");
                block.add(c0);
                block.add(")");
                return block.build();

            case TParser.NEQ:
                c0 = getNodeCode(t.getChild(0));
                c1 = getNodeCode(t.getChild(1));
                block.add("(");
                block.add(c1);
                block.add("!=");
                block.add(c0);
                block.add(")");
                return block.build();

            case TParser.FOR:
                ++ForCount;
                String name = "aux_for" + ForCount;
                block.add("String "+name);
                block.add("[] =");
                block.add(getNodeCode(t.getChild(1)));
                block.add(";\n");
                block.beginControlFlow("for (String "+t.getChild(0).getText()+ " : " + name + ")");
                for(int i = 0; i < t.getChild(2).getChildCount(); ++i) {
                    block.addStatement(getNodeCode(t.getChild(2).getChild(i)).toString());
                }
                block.endControlFlow();
                return block.build();

            case TParser.RETURN:
                block.add("return ");
                block.add(getNodeCode(t.getChild(0)));
                return block.build();

            case TParser.COND:
                Tree ifstm = t.getChild(0);
                CodeBlock cond = getNodeCode(ifstm.getChild(0));
                block.beginControlFlow("if"+cond);
                Tree instr = ifstm.getChild(1);
                for(int i = 0; i < instr.getChildCount(); ++i) {
                    block.addStatement(getNodeCode(instr.getChild(i)).toString());
                }
                block.endControlFlow();
                int k = 1;
                while (k < t.getChildCount()) {
                    if(t.getChild(k).getType() == TParser.ELIF) {
                        Tree elif = t.getChild(k);
                        cond = getNodeCode(elif.getChild(0));
                        block.beginControlFlow("else if" + cond);
                        instr = elif.getChild(1);
                        for(int i = 0; i < instr.getChildCount(); ++i) {
                            block.addStatement(getNodeCode(instr.getChild(i)).toString());
                        }
                        block.endControlFlow();
                    }
                    else if (t.getChild(k).getType() == TParser.ELSE) {
                        Tree elstm = t.getChild(k);
                        block.beginControlFlow("else");
                        instr = elstm.getChild(0);
                        for(int i = 0; i < instr.getChildCount(); ++i) {
                            block.addStatement(getNodeCode(instr.getChild(i)).toString());
                        }
                        block.endControlFlow();
                    }
                    ++k;
                }
                return block.build();

            case TParser.WHILE:
                CodeBlock condWhile = getNodeCode(t.getChild(0));
                block.beginControlFlow("while"+condWhile);
                Tree instrWhile = t.getChild(1);
                for(int i = 0; i < instrWhile.getChildCount(); ++i) {
                    block.addStatement(getNodeCode(instrWhile.getChild(i)).toString());
                }
                block.endControlFlow();
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
                String func = getFunctionName(t0);
                if(!symTable.containsKey(func + "_" + t0.getText())) {
                    Tree t = findInTree(t0.getText(), t1);
                    symTable.put(func + "_" + t0.getText(), getType(t, null));
                }
                return symTable.get(func + "_" + t0.getText());

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
            return getType(return_tree.getChild(0), list_instr);
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

    private String getFunctionName(Tree t) {
        while(t.getParent() != null && t.getType() != TParser.FUNCTION) {
            t = t.getParent();
        }
        if(t.getParent() == null) {
            return "main";
        }
        else {
            return t.getChild(0).getText();
        }
    }
}
