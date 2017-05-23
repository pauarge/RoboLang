package com.robolang;

import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

import javax.microedition.sensor.SensorInfo;


public class Common {

    public static void print(String val) {
        System.out.println(val);
    }

    public static void print(double val) {
        System.out.println(val);
    }

    public static void forward(DifferentialPilot pilot, ColorSensor L) {
        //pilot.forward();
        Delay d = new Delay();
        waitForPress();
        SensorPort.S2.activate();
        L.calibrateHigh();
        System.out.println("High: " + L.getHigh());
        waitForPress();
        L.calibrateLow();
        System.out.println("Low: " + L.getLow());
        waitForPress();
        LCD.clearDisplay();
        while(true){
            System.out.println("LightValue: "+ L.getLightValue());
            System.out.println("Floodlight: " + L.getFloodlight());
            System.out.println("Normalized Light: " + L.getNormalizedLightValue());
            d.msDelay(100);
            LCD.clearDisplay();
        }
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

    public static void move(String port, double units) {
        NXTRegulatedMotor X = getMotorPort(port);
        X.rotate((int) units * 360);
    }

    public static void waitToBePressed(int id) {
        Button B = getButton(id);
        B.waitForPressAndRelease();

        TouchSensor ts = new TouchSensor(SensorPort.S1);
        
    }

    public static void explore(DifferentialPilot pilot, TouchSensor T, UltrasonicSensor U) {
        pilot.forward();
        while (Button.ESCAPE.isUp()) {
            if (T.isPressed()) {
                pilot.stop();
                pilot.rotate(-180);
                // TODO: Maybe use getDistances and use average?
                while (U.getDistance() < 10) {
                    pilot.rotate(10);
                }
                pilot.forward();
            }
        }
        pilot.stop();
    }

    public static void followLine(ColorSensor L, NXTRegulatedMotor A, NXTRegulatedMotor B) {
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        LCD.clearDisplay();
        System.out.println(L.getLow());
        waitToBePressed(Button.ID_ENTER);
        L.calibrateLow();
        LCD.clearDisplay();
        System.out.println("Calibrate black value");
        System.out.println("Press ENTER to set value");
        LCD.clearDisplay();
        System.out.println(L.getHigh());
        waitToBePressed(Button.ID_ENTER);
        L.calibrateHigh();
        A.setSpeed(A.getSpeed()/3);
        while (Button.ESCAPE.isUp()) {
            if(L.getNormalizedLightValue() < 200)
                A.rotate(10); //Right
            else{
                A.stop();
                B.rotate(10);
            }
            //while (L.readValue() != L.getHigh()) ;
            //while(L.getNormalizedLightValue() < 200);
             //Left
        }
    }

    public static void shoot(int balls, NXTRegulatedMotor C) {
        C.setSpeed(C.getMaxSpeed());
        for (int i = 0; i < balls; ++i) {
            C.rotate(360);
        }
        C.setSpeed(360);
    }

    public static void party(DifferentialPilot pilot) {
        while (Button.ESCAPE.isUp()) {
            try {
                pilot.rotate(10);
                Sound.beepSequenceUp();
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

    public static boolean touched() {
        return true;
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
}
