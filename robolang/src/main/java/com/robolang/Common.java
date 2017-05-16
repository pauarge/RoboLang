package com.robolang;

import lejos.nxt.*;

public class Common {

    public static void print(String val) {
        System.out.println(val);
    }

    public static void print(double val) {
        System.out.println(val);
    }

    public static void move_front(double units) {
        NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A);
        NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B);
        A.rotate((int) (units * 360));
        B.rotate((int) (units * 360));
    }

    public static void move_back(double units) {
        NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A);
        NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B);
        A.rotate((int) (-units * 360));
        B.rotate((int) (-units * 360));
    }

    public static void rotate_left(int degrees) {
        NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A);
        NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B);
        A.rotate(degrees);
        B.rotate(-degrees);
    }

    public static void rotate_right(int degrees) {
        NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A);
        NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B);
        A.rotate(-degrees);
        B.rotate(degrees);
    }

    public static void explore() {

    }

}
