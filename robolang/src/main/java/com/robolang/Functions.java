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
    public void run(){

        MethodSpec.Builder move_front = MethodSpec.methodBuilder("move_front")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(int.class, "units")
                .addStatement("MXTMotor m1 = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("m1.forward()");

        map.put("move_front", move_front);

        MethodSpec.Builder move_back = MethodSpec.methodBuilder("move_front")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(int.class, "units")
                .addStatement("MXTMotor m1 = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("m1.backward()");

        map.put("move_back", move_back);

    }

    public Map<String, MethodSpec.Builder> getMap() {
        return map;
    }
}
