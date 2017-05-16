package com.robolang;

import lejos.nxt.*;

public class Common {

    public static void print(String val){
        System.out.println(val);
    }

    public static void print(double val){
        System.out.println(val);
    }

    public static void move_front(double units, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        A.rotate((int) (units * 360));
        B.rotate((int) (units * 360));
    }

    public static void move_back(double units, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        A.rotate((int) (-units * 360));
        B.rotate((int) (-units * 360));
    }

    public static void rotate_left(int degrees, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        A.rotate(degrees);
        B.rotate(-degrees);
    }

    public static void rotate_right(int degrees, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        A.rotate(-degrees);
        B.rotate(degrees);
    }

    private static NXTRegulatedMotor getMotorPort(String port) {
        switch (port){
            case "A":
                return new NXTRegulatedMotor(MotorPort.A);

            case "B":
                return new NXTRegulatedMotor(MotorPort.B);

            case "C":
                return new NXTRegulatedMotor(MotorPort.C);

            default:
                return null;
        }
    }

    private static Button getButton(String port) {
        switch (port){
            case "BENTER":
                return Button.ENTER;

            case "BLEFT":
                return Button.LEFT;

            case "BRIGHT":
                return Button.RIGHT;

            case "BESCAPE":
                return Button.ESCAPE;

            default:
                return null;
        }
    }

    public static void move(String port, double units) {
        NXTRegulatedMotor X = getMotorPort(port);
        X.rotate((int) units*360);
    }

    public static void waitToBePressed(String port) {
        Button B = getButton(port);
        B.waitForPressAndRelease();
        LCD.drawString("Finished", 0,0);
    }

    public static void explore() {

    }

    public static void followLine(LightSensor L, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        waitToBePressed("ENTER");
        L.calibrateHigh();
        LCD.clearDisplay();
        System.out.println("Calibrate black value");
        System.out.println("Press ENTER to set value");
        waitToBePressed("BENTER");
        L.calibrateLow();
        while(Button.ESCAPE.isUp()) {
            A.forward(); //Right
            while(L.readValue() != L.getHigh());
            A.stop();
            B.rotate(30); //Left
        }
    }

    public static void shoot(int balls, NXTRegulatedMotor C) {
        C.setSpeed(C.getMaxSpeed());
        for(int i = 0; i < balls; ++i) {
            C.rotate(360);
        }
        C.setSpeed(360);
    }

}
