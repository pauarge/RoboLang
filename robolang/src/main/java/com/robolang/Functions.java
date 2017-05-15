package com.robolang;


import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Functions {
    private Map<String, MethodSpec.Builder> map;

    public Functions() {
        map = new HashMap<>();
    }

    private void createMethod(MethodSpec.Builder action, Boolean move, String direction, String name) {

        action.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        action.returns(void.class);
        action.addStatement("NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A)");
        action.addStatement("NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B)");
        if (move) {
            action.addParameter(double.class, "units");
            action.addStatement("A.rotate((int)(" + direction + "units*360))"); //Motor dret
            action.addStatement("B.rotate((int)(" + direction + "units*360))"); //Motor esquerre
        } else {
            action.addParameter(int.class, "degrees");
            String dirAux;
            if (direction.equals("-")) dirAux = "";
            else dirAux = "-";
            action.addStatement("A.rotate(" + direction + "degrees)");
            action.addStatement("B.rotate(" + dirAux + "degrees)");
        }
        map.put(name, action);
    }

    public void run() {

        MethodSpec.Builder move_front = MethodSpec.methodBuilder("move_front");
        createMethod(move_front, true, "", "move_front");

        MethodSpec.Builder move_back = MethodSpec.methodBuilder("move_back");
        createMethod(move_back, true, "-", "move_back");

        MethodSpec.Builder rotate_left = MethodSpec.methodBuilder("rotate_left");
        createMethod(rotate_left, false, "", "rotate_left");

        MethodSpec.Builder rotate_right = MethodSpec.methodBuilder("rotate_right");
        createMethod(rotate_right, false, "-", "rotate_right");

        MethodSpec.Builder asList = MethodSpec.methodBuilder("asList");
        asList.addCode("private List asList(String[] list){ List<String> l = new ArrayList<>(); for(int i = 0; i < list.length; ++i) l.add(list[i]); return l;}");
    }

    public Map<String, MethodSpec.Builder> getMap() {
        return map;
    }
}
