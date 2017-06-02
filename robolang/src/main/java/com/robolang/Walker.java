package com.robolang;

import com.squareup.javapoet.*;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class Walker {
    private Tree root;
    private String className;
    private String path;
    private TypeSpec.Builder mainClass;
    private Map<String, Type> symTable;
    private Map<String, String> portMap;
    private double wheelDiameter;
    private double trackWidth;

    public Walker(Tree t, String className, String path) {
        this.root = t;
        this.className = className;
        this.path = path;
        this.symTable = new HashMap<>();
        this.portMap = new HashMap<>();
        for (Method m : Common.class.getMethods()) {
            symTable.put("def_" + m.getName(), m.getGenericReturnType());
        }
        assert root.getText().equals("LIST_INSTR");
        portMap.put("A", "rightMotor");
        portMap.put("B", "leftMotor");
        portMap.put("C", "shootMotor");
        portMap.put("S1", "touchSensor");
        portMap.put("S2", "colorSensor");
        portMap.put("S3", "touchSensor");
        portMap.put("S4", "ultrasonicSensor");
        portMap.put("BRIGHT", "Button.RIGHT");
        portMap.put("BESCAPE", "Button.ESCAPE");
        portMap.put("BLEFT", "Button.LEFT");
        portMap.put("BENTER", "Button.ENTER");
        this.wheelDiameter = 4.3;
        this.trackWidth = 14.2;
    }

    private void addField(ClassName Class, ClassName PortClass, String port, TypeSpec.Builder mClass, String varName) {
        FieldSpec fieldSpec = FieldSpec.builder(Class, varName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T($T." + port + ")", Class, PortClass).build();
        mClass.addField(fieldSpec);
    }

    public String getCode() {
        ClassName NXTRegulatedMotorClass = ClassName.get("lejos.nxt", "NXTRegulatedMotor");
        ClassName MotorPortClass = ClassName.get("lejos.nxt", "MotorPort");
        ClassName SensorPortClass = ClassName.get("lejos.nxt", "SensorPort");
        ClassName TouchSensorClass = ClassName.get("lejos.nxt", "TouchSensor");
        ClassName ColorSensorClass = ClassName.get("lejos.nxt", "ColorSensor");
        ClassName UltrasonicSensorClass = ClassName.get("lejos.nxt", "UltrasonicSensor");
        ClassName DifferentialPilotClass = ClassName.get("lejos.robotics.navigation", "DifferentialPilot");
        ClassName ButtonClass = ClassName.get("lejos.nxt", "Button");

        MethodSpec.Builder mainFunc = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args");

        FieldSpec diffPilot = FieldSpec.builder(DifferentialPilotClass, "pilot")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T(4.3f, 14.3f,rightMotor,leftMotor)", DifferentialPilotClass).build();


        mainClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        addField(NXTRegulatedMotorClass, MotorPortClass, "B", mainClass, "leftMotor");
        addField(NXTRegulatedMotorClass, MotorPortClass, "A", mainClass, "rightMotor");
        addField(NXTRegulatedMotorClass, MotorPortClass, "C", mainClass, "armMotor");
        addField(NXTRegulatedMotorClass, MotorPortClass, "C", mainClass, "sensorMotor");
        addField(NXTRegulatedMotorClass, MotorPortClass, "C", mainClass, "shootMotor");
        addField(TouchSensorClass, SensorPortClass, "S1", mainClass, "touchSensor");
        addField(ColorSensorClass, SensorPortClass, "S2", mainClass, "colorSensor");
        addField(TouchSensorClass, SensorPortClass, "S3", mainClass, "touchSensor2");
        addField(UltrasonicSensorClass, SensorPortClass, "S4", mainClass, "ultrasonicSensor");


        FieldSpec fieldSpec = FieldSpec.builder(ButtonClass, "button")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("Button.ENTER").build();

        mainClass.addField(diffPilot).addField(fieldSpec);
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
        CodeBlock c;

        switch (t.getType()) {
            case TParser.TRUE:
            case TParser.FALSE:
            case TParser.NUM:
            case TParser.VAR:
                block.add(t.getText());
                return block.build();

            case TParser.ADD:
                return genInstrBlock(t, "+");

            case TParser.AND:
                return genInstrBlock(t, "&&");

            case TParser.ASSIGN:
                assert t.getChild(0).getType() == TParser.VAR;
                Type type = getType(t.getChild(1), null);
                String auxName = getFunctionName(t) + "_" + t.getChild(0).getText();
                boolean firstTime = false;
                if (!symTable.containsKey(auxName)) {
                    firstTime = true;
                    symTable.put(auxName, type);
                }
                if (firstTime)
                    block.add(type.toString() + " ");
                block.add(t.getChild(0).getText());
                block.add("=");
                block.add(getNodeCode(t.getChild(1)));
                return block.build();

            case TParser.COND:
                Tree ifstm = t.getChild(0);
                CodeBlock cond = getNodeCode(ifstm.getChild(0));
                block.beginControlFlow("if" + cond);
                Tree instr = ifstm.getChild(1);
                for (int i = 0; i < instr.getChildCount(); ++i) {
                    block.addStatement(getNodeCode(instr.getChild(i)).toString());
                }
                block.endControlFlow();
                int k = 1;
                while (k < t.getChildCount()) {
                    if (t.getChild(k).getType() == TParser.ELIF) {
                        Tree elif = t.getChild(k);
                        cond = getNodeCode(elif.getChild(0));
                        block.beginControlFlow("else if" + cond);
                        instr = elif.getChild(1);
                        for (int i = 0; i < instr.getChildCount(); ++i) {
                            block.addStatement(getNodeCode(instr.getChild(i)).toString());
                        }
                        block.endControlFlow();
                    } else if (t.getChild(k).getType() == TParser.ELSE) {
                        Tree elstm = t.getChild(k);
                        block.beginControlFlow("else");
                        instr = elstm.getChild(0);
                        for (int i = 0; i < instr.getChildCount(); ++i) {
                            block.addStatement(getNodeCode(instr.getChild(i)).toString());
                        }
                        block.endControlFlow();
                    }
                    k++;
                }
                return block.build();

            case TParser.DIV:
                return genInstrBlock(t, "/");

            case TParser.EQ:
                return genInstrBlock(t, "==");

            case TParser.DOLLAR:
                return getNodeCode(t.getChild(1));

            case TParser.FOR:
                String list2 = getNodeCode(t.getChild(1)).toString();
                block.beginControlFlow("for (String " + t.getChild(0).getText() + " : " + list2 + ")");
                for (int i = 0; i < t.getChild(2).getChildCount(); ++i) {
                    block.addStatement(getNodeCode(t.getChild(2).getChild(i)).toString());
                }
                block.endControlFlow();
                return block.build();

            case TParser.FUNCALL:
                boolean motorChanged = false;
                boolean structureChanged = false;
                StringBuilder sb = new StringBuilder();
                String funcname = t.getChild(0).getText();
                boolean first = true;
                if (funcname.equals("setDefault")) {
                    if (first) {
                        first = false;
                        portMap.clear();
                    }
                    Tree sdParams = t.getChild(1);
                    String portName = sdParams.getChild(0).getText();
                    String port = sdParams.getChild(1).getText();
                    portName = portName.substring(1, portName.length() - 1);
                    port = port.substring(1, port.length() - 1);
                    switch (portName) {
                        case "leftMotor":
                            motorChanged = true;
                            sb.append("leftMotor = new NXTRegulatedMotor(MotorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "leftMotor");
                            else
                                portMap.put(port, "leftMotor");
                            break;
                        case "rightMotor":
                            motorChanged = true;
                            sb.append("rightMotor = new NXTRegulatedMotor(MotorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "rightMotor");
                            else
                                portMap.put(port, "rightMotor");
                            break;
                        case "armMotor":
                            sb.append("armMotor = new NXTRegulatedMotor(MotorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "armMotor");
                            else
                                portMap.put(port, "armMotor");
                            break;
                        case "sensorMotor":
                            sb.append("sensorMotor = new NXTRegulatedMotor(MotorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "sensorMotor");
                            else
                                portMap.put(port, "sensorMotor");
                            break;
                        case "shootMotor":
                            sb.append("shootMotor = new NXTRegulatedMotor(MotorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "shootMotor");
                            else
                                portMap.put(port, "shootMotor");
                            break;
                        case "touchSensor":
                            sb.append("touchSensor = new TouchSensor(SensorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "touchSensor");
                            else
                                portMap.put(port, "touchSensor");
                        case "touchSensor2":
                            sb.append("touchSensor2 = new TouchSensor(SensorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "touchSensor2");
                            else
                                portMap.put(port, "touchSensor2");
                            break;
                        case "colorSensor":
                            sb.append("colorSensor = new ColorSensor(SensorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "colorSensor");
                            else
                                portMap.put(port, "colorSensor");
                            break;
                        case "ultrasonicSensor":
                            sb.append("ultrasonicSensor = new UltrasonicSensor(SensorPort." + port + ")");
                            if (portMap.containsKey(port))
                                portMap.replace(port, "ultrasonicSensor");
                            else
                                portMap.put(port, "ultrasonicSensor");
                            break;
                        case "wheelDiameter":
                            structureChanged = true;
                            wheelDiameter = Double.parseDouble(port);
                            break;
                        case "trackWidth":
                            structureChanged = true;
                            trackWidth = Double.parseDouble(port);
                            break;
                    }
                } else {
                    boolean common = false;
                    for (Method m : Common.class.getMethods()) {
                        if (m.getName().equals(funcname)) {
                            sb.append("Common.");
                            common = true;
                            break;
                        }
                    }
                    sb.append(funcname);
                    sb.append("(");
                    int n = t.getChild(1).getChildCount();
                    for (int i = 0; i < n; ++i) {
                        sb.append(getNodeCode(t.getChild(1).getChild(i)).toString());
                        if (i != n - 1) sb.append(",");
                    }
                    if (funcname.equals("moveBack") || funcname.equals("moveFront") || funcname.equals("rotateLeft")
                            || funcname.equals("rotateRight")) {
                        sb.append(", pilot");
                    } else if (funcname.equals("shoot")) {
                        sb.append(", shootMotor");
                    } else if (funcname.equals("followLine") || funcname.equals("followLine2")) {
                        sb.append("colorSensor, rightMotor, leftMotor");
                    } else if (funcname.equals("forward") || funcname.equals("backward") || funcname.equals("stop")) {
                        sb.append("pilot");
                    } else if (funcname.equals("explore")) {
                        sb.append("pilot,touchSensor,touchSensor2,ultrasonicSensor");
                    } else if (funcname.equals("party")) {
                        sb.append("pilot,colorSensor");
                    } else if (funcname.equals("getUltrasonicDistance")) {
                        sb.append("ultrasonicSensor");
                    } else if (funcname.equals("exploreUltrasonic")) {
                        sb.append("pilot," + "ultrasonicSensor");
                    }
                    if (common && t.getParent().getType() == TParser.DOLLAR) {
                        if (t.getChild(1).getChildCount() != 0) sb.append(",");
                        String portName = t.getParent().getChild(0).getText();
                        sb.append(portMap.get(portName.substring(1, portName.length())));
                    }
                    sb.append(")");
                }
                if (!structureChanged) block.add(sb.toString());
                if (motorChanged) block.add(";\n");
                if (motorChanged || structureChanged) {
                    block.add("pilot = new DifferentialPilot(" + wheelDiameter + "," + trackWidth + ",rightMotor,leftMotor)");
                }
                return block.build();

            case TParser.FUNCTION:
                MethodSpec.Builder f = MethodSpec.methodBuilder(t.getChild(0).getText())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC);
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
                Type retType = getReturn(t);
                f.returns(retType);
                mainClass.addMethod(f.build());
                symTable.put("def_" + t.getChild(0).getText(), retType);
                return null;

            case TParser.GET:
                return genInstrBlock(t, ">=");

            case TParser.GT:
                return genInstrBlock(t, ">");

            case TParser.IMPORT:
                TLexer lexer = new TLexer();
                try {
                    String fileName = path + t.getChild(0).getText() + ".rl";
                    lexer.setCharStream(new ANTLRFileStream(fileName, "UTF8"));
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    TParser parser = new TParser(tokens);
                    Tree importTree = (Tree) parser.prog().getTree();
                    for (int i = 0; i < importTree.getChildCount(); i++) {
                        getNodeCode(importTree.getChild(i));
                    }
                } catch (RecognitionException ex) {
                    System.err.println("The input file contains invalid Robolang code.");
                } catch (IOException ex) {
                    System.err.println("Could not import specified file.");
                }
                return null;

            case TParser.LET:
                return genInstrBlock(t, "<=");

            case TParser.LT:
                return genInstrBlock(t, "<");

            case TParser.MOD:
                return genInstrBlock(t, "%");

            case TParser.NEQ:
                return genInstrBlock(t, "!=");

            case TParser.NOT:
                c = getNodeCode(t.getChild(0));
                block.add("(!");
                block.add(c);
                block.add(")");
                return block.build();

            case TParser.OR:
                return genInstrBlock(t, "||");

            case TParser.RETURN:
                block.add("return ");
                block.add(getNodeCode(t.getChild(0)));
                return block.build();

            case TParser.SUB:
                if (t.getChildCount() == 1) {
                    c = getNodeCode(t.getChild(0));
                    block.add("(-");
                    block.add(c);
                    block.add(")");
                    return block.build();
                }
                return genInstrBlock(t, "-");

            case TParser.TIMES:
                return genInstrBlock(t, "*");

            case TParser.WHILE:
                CodeBlock condWhile = getNodeCode(t.getChild(0));
                block.beginControlFlow("while(" + condWhile + ")");
                Tree instrWhile = t.getChild(1);
                for (int i = 0; i < instrWhile.getChildCount(); ++i) {
                    block.addStatement(getNodeCode(instrWhile.getChild(i)).toString());
                }
                block.endControlFlow();
                return block.build();

            case TParser.STRING:
                block.add(t.getText());
                return block.build();

            default:
                return null;
        }
    }

    private Type getType(Tree t0, Tree t1) {
        switch (t0.getType()) {

            case TParser.DOLLAR:
                return getType(t0.getChild(1), null);

            case TParser.ADD:
            case TParser.DIV:
            case TParser.MOD:
            case TParser.TIMES:
                Type tp0 = getType(t0.getChild(0), t1);
                Type tp1 = getType(t0.getChild(1), t1);
                assert tp0 == tp1;
                return tp0;

            case TParser.SUB:
                if (t0.getChildCount() == 2) {
                    Type tp2 = getType(t0.getChild(0), t1);
                    Type tp3 = getType(t0.getChild(1), t1);
                    assert tp2 == tp3;
                    return tp2;
                } else return float.class;
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

            case TParser.STRING:
            case TParser.PORT:
                return String.class;

            case TParser.NUM:
                return double.class;

            case TParser.VAR:
                assert t1 != null;
                String func = getFunctionName(t0);
                if (!symTable.containsKey(func + "_" + t0.getText())) {
                    Tree t = findInTree(t0.getText(), t1);
                    if (t == null) {
                        symTable.put(func + "_" + t0.getText(), double.class);
                    } else {
                        symTable.put(func + "_" + t0.getText(), getType(t, null));
                    }
                }
                return symTable.get(func + "_" + t0.getText());

            case TParser.FUNCALL:
                return symTable.get("def_" + t0.getChild(0).getText());

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
        while (t.getParent() != null && t.getType() != TParser.FUNCTION) {
            t = t.getParent();
        }
        if (t.getParent() == null) {
            return "main";
        } else {
            return t.getChild(0).getText();
        }
    }

    private CodeBlock genInstrBlock(Tree t, String ins) {
        CodeBlock.Builder block = CodeBlock.builder();
        CodeBlock c0 = getNodeCode(t.getChild(0));
        CodeBlock c1 = getNodeCode(t.getChild(1));
        block.add("(");
        block.add(c0);
        block.add(ins);
        block.add(c1);
        block.add(")");
        return block.build();
    }
}
