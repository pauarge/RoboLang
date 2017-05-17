package com.robolang;

import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class Common {

    public static void print(String val){
        System.out.println(val);
    }

    public static void print(double val){
        System.out.println(val);
    }

    public static void move_front(double units, DifferentialPilot pilot) {
        pilot.travel(units);
    }

    public static void move_back(double units, DifferentialPilot pilot) {
        pilot.travel(-units);
    }

    public static void rotate_left(int degrees, DifferentialPilot pilot) {
        pilot.rotate((double)-degrees);
    }

    public static void rotate_right(int degrees, DifferentialPilot pilot) {
        pilot.rotate((double)degrees);
    }

    private static NXTRegulatedMotor getMotorPort(String port) {

        if(port.equals("A")) return new NXTRegulatedMotor(MotorPort.A);
        else if(port.equals("B")) return new NXTRegulatedMotor(MotorPort.B);
        else if(port.equals("C")) return new NXTRegulatedMotor(MotorPort.C);
        else return null;
    }

    private static Button getButton(int id) {
        switch (id){
            case Button.ID_ENTER:
                return Button.ENTER;

            case Button.ID_LEFT:
                return Button.LEFT;

            case Button.ID_RIGHT:
                return Button.RIGHT;

            case Button.ID_ESCAPE:
                return Button.ESCAPE;

            default:
                return null;
        }
    }

    public static void move(String port, double units) {
        NXTRegulatedMotor X = getMotorPort(port);
        X.rotate((int) units*360);
    }

    public static void waitToBePressed(int id) {
        Button B = getButton(id);
        B.waitForPressAndRelease();
        LCD.drawString("Finished", 0,0);
    }

    public static void explore() {

    }

    public static void followLine(LightSensor L, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        waitToBePressed(Button.ID_ENTER);
        L.calibrateHigh();
        LCD.clearDisplay();
        System.out.println("Calibrate black value");
        System.out.println("Press ENTER to set value");
        waitToBePressed(Button.ID_ENTER);
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
