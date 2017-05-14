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
                .addParameter(double.class, "units")
                .addStatement("NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B)")
                .addStatement("A.rotate(int(units*360))") //Motor dret
                .addStatement("B.rotate(int(units*360))"); //Motor esquerre

        map.put("move_front", move_front);

        MethodSpec.Builder move_back = MethodSpec.methodBuilder("move_back")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(double.class, "units")
                .addStatement("NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B)")
                .addStatement("A.rotate(int(-units*360))") //Motor dret
                .addStatement("B.rotate(int(-units*360))"); //Motor esquerre

        map.put("move_back", move_back);

        MethodSpec.Builder rotate_left = MethodSpec.methodBuilder("rotate_left")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(int.class, "degrees")
                .addStatement("NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B)")
                .addStatement("A.rotate(degrees)") //Motor dret
                .addStatement("B.rotate(-degrees)"); //Motor esquerre

        map.put("rotate_left", rotate_left);

        MethodSpec.Builder rotate_right = MethodSpec.methodBuilder("rotate_right")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(int.class, "degrees")
                .addStatement("NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A)")
                .addStatement("NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B)")
                .addStatement("A.rotate(-degrees)") //Motor dret
                .addStatement("B.rotate(degrees)"); //Motor esquerre

        map.put("rotate_right", rotate_right);

    }

    public Map<String, MethodSpec.Builder> getMap() {
        return map;
    }
}
