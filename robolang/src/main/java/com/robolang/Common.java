package com.robolang;

import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;


public class Common {

    public static void print(String val) {
        System.out.println(val);
    }

    public static void print(double val) {
        System.out.println(val);
    }

    public static void forward(DifferentialPilot pilot) {
        pilot.forward();
    }

    public static void move_front(double units, DifferentialPilot pilot) {
        pilot.travel(units);
    }

    public static void backward(DifferentialPilot pilot) {
        pilot.backward();
    }

    public static void move_back(double units, DifferentialPilot pilot) {
        pilot.travel(-units);
    }

    public static void rotate_left(int degrees, DifferentialPilot pilot) {
        pilot.rotate((double) -degrees);
    }

    public static void rotate_right(int degrees, DifferentialPilot pilot) {
        pilot.rotate((double) degrees);
    }

    private static NXTRegulatedMotor getMotorPort(String port) {
        if (port.equals("A")) return new NXTRegulatedMotor(MotorPort.A);
        else if (port.equals("B")) return new NXTRegulatedMotor(MotorPort.B);
        else if (port.equals("C")) return new NXTRegulatedMotor(MotorPort.C);
        else return null;
    }

    private static Button getButton(int id) {
        switch (id) {
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

    public static void move(double units, NXTRegulatedMotor X) {
        X.rotate((int) units * 360);
    }

    public static void waitToBePressed(int id) {
        Button B = getButton(id);
        B.waitForPressAndRelease();

        TouchSensor ts = new TouchSensor(SensorPort.S1);
    }

    public static void explore(DifferentialPilot pilot, TouchSensor T1, TouchSensor T2, UltrasonicSensor U) {
        pilot.forward();
        while (Button.ESCAPE.isUp()) {
            if (T1.isPressed() || T2.isPressed()) {
                pilot.stop();
                move_back(10, pilot);
                // TODO: Maybe use getDistances and use average?
                while (U.getDistance() < 25) pilot.rotate(15);
                pilot.forward();
            }
        }
        pilot.stop();
    }

    public static void exploreUltrasonic(DifferentialPilot pilot, UltrasonicSensor U){
        pilot.forward();
        while(Button.ESCAPE.isUp()){
            if(U.getDistance() < 20){
                pilot.stop();
                move_back(10, pilot);
                while (U.getDistance() < 25) pilot.rotate(15);
            }
            pilot.forward();
        }
        pilot.stop();
    }

    public static void followLine(ColorSensor L, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        LCD.clearDisplay();
        System.out.println("Calibrate black value");
        System.out.println("Press ENTER to set value");
        waitToBePressed(Button.ID_ENTER);
        L.setLow(L.getNormalizedLightValue());
        System.out.println(L.getLow());
        waitForPress();
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        waitToBePressed(Button.ID_ENTER);
        L.setHigh(L.getNormalizedLightValue());
        System.out.println(L.getHigh());
        waitForPress();
        LCD.clearDisplay();
        A.setAcceleration(A.getAcceleration()/4);
        B.setAcceleration(B.getAcceleration()/4);
        B.setSpeed(B.getSpeed()/2);
        A.setSpeed(A.getSpeed()/2);
        while (Button.ESCAPE.isUp()) {
            if(L.getNormalizedLightValue() < L.getHigh()-10) {
                B.stop();
                A.forward(); //Right
            }
            else{
                A.stop();
                B.forward();
            }
        }
    }

    public static void shoot(int balls, NXTRegulatedMotor C) {
        C.setSpeed(C.getMaxSpeed());
        for (int i = 0; i < balls; ++i) {
            C.rotate(360);
        }
        C.setSpeed(360);
    }

    public static void party(DifferentialPilot pilot, ColorSensor L) {
        while (Button.ESCAPE.isUp()) {
            try {
                pilot.rotate(25);
                Sound.beepSequenceUp();
                L.getNormalizedLightValue();
                Sound.beep();
                Sound.beepSequence();
                Sound.buzz();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                continue;
            }
        }
    }

    public static void waitForPress() {
        Button.waitForAnyPress();
    }

    public static void setSpeed(NXTRegulatedMotor A, float speed) {
        A.setSpeed(speed);
    }

    public static void setAcceleration(NXTRegulatedMotor A, int acc) {
        A.setAcceleration(acc);
    }

    public static void beep() {
        Sound.beep();
    }

    public static void beepSequence() {
        Sound.beepSequence();
    }

    public static void beepSequenceUp() {
        Sound.beepSequenceUp();
    }

    public static void buzz() {
        Sound.buzz();
    }

    public static double getUltrasonicDistance(UltrasonicSensor US) {
        return US.getDistance();
    }

    public static double getDistance(UltrasonicSensor US) {
        return US.getDistance();
    }

    public static boolean isPressed(TouchSensor TS) {
        return TS.isPressed();
    }

    public static double getSpeed(NXTRegulatedMotor M) {
        return M.getSpeed();
    }

    public static double getAcceleration(NXTRegulatedMotor M) {
        return M.getAcceleration();
    }

    public static double getMaxSpeed(NXTRegulatedMotor M) {
        return M.getMaxSpeed();
    }
}
