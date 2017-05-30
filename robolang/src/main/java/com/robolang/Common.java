package com.robolang;

import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;


public class Common {

    public static void print(String val) {
        System.out.println(val);
    }

    public static void print(double val) {
        System.out.println(val);
    }

    public static void clearDisplay() {
        LCD.clearDisplay();
    }

    public static void forward(DifferentialPilot pilot) {
        pilot.forward();
    }

    public static void stop(DifferentialPilot pilot) {
        pilot.stop();
    }

    public static void stopMotor(NXTRegulatedMotor M) {
        M.stop();
    }

    public static void moveFront(double units, DifferentialPilot pilot) {
        pilot.travel(units);
    }

    public static void backward(DifferentialPilot pilot) {
        pilot.backward();
    }

    public static void move(boolean forward, NXTRegulatedMotor M) {
        if(forward) M.forward();
        else M.backward();
    }

    public static void moveBack(double units, DifferentialPilot pilot) {
        pilot.travel(-units);
    }

    public static void rotateLeft(int degrees, DifferentialPilot pilot) {
        pilot.rotate((double) -degrees);
    }

    public static void rotateRight(int degrees, DifferentialPilot pilot) {
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

    public static void rotationMove(double units, NXTRegulatedMotor X) {
        X.rotate((int) units * 360);
    }

    private static void waitButton(int id) {
        Button B = getButton(id);
        B.waitForPressAndRelease();

        TouchSensor ts = new TouchSensor(SensorPort.S1);
    }

    public static void waitToBePressed(Button b) {
        b.waitForPress();
    }

    public static void explore(DifferentialPilot pilot, TouchSensor T1, TouchSensor T2, UltrasonicSensor U) {
        pilot.forward();
        while (Button.ESCAPE.isUp()) {
            if (T1.isPressed() || T2.isPressed()) {
                pilot.stop();
                moveBack(10, pilot);
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
                moveBack(10, pilot);
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
        waitButton(Button.ID_ENTER);
        L.setLow(L.getNormalizedLightValue());
        System.out.println(L.getLow());
        waitForPress();
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        waitButton(Button.ID_ENTER);
        L.setHigh(L.getNormalizedLightValue());
        System.out.println(L.getHigh());
        waitForPress();
        LCD.clearDisplay();
        A.setAcceleration(A.getAcceleration()/5);
        B.setAcceleration(B.getAcceleration()/5);
        B.setSpeed(B.getSpeed()/3);
        A.setSpeed(A.getSpeed()/3);
        DifferentialPilot pilot = new DifferentialPilot(4.3f, 13.7f,A,B);

        while (Button.ESCAPE.isUp()) {
            if((L.getNormalizedLightValue() + 70> L.getHigh() && L.getNormalizedLightValue() -70 < L.getHigh())) {
                B.stop();
                A.rotate(20);
               // print("WHITE: ");
            }
            else{
                //print("BLACK: ");
                A.stop(); //Right
                B.rotate(20);
            }
        }
    }


    public static void followLine2(ColorSensor L, NXTRegulatedMotor A, NXTRegulatedMotor B) {

        LCD.clearDisplay();
        System.out.println("Calibrate black value");
        System.out.println("Press ENTER to set value");
        waitButton(Button.ID_ENTER);
        L.setLow(L.getNormalizedLightValue());
        System.out.println(L.getLow());
        waitForPress();
        LCD.clearDisplay();
        System.out.println("Calibrate white value");
        System.out.println("Press ENTER to set value");
        waitButton(Button.ID_ENTER);
        L.setHigh(L.getNormalizedLightValue());
        System.out.println(L.getHigh());
        waitForPress();
        LCD.clearDisplay();
        A.setAcceleration(A.getAcceleration()/5);
        B.setAcceleration(B.getAcceleration()/5);
        B.setSpeed(B.getSpeed()/3);
        A.setSpeed(A.getSpeed()/3);
        DifferentialPilot pilot = new DifferentialPilot(4.3f, 13.7f,A,B);

        while(Button.ESCAPE.isUp()){

            if(L.getNormalizedLightValue() + 40 > L.getLow() && L.getNormalizedLightValue() - 40 < L.getLow()){
                pilot.forward();
            }
            else{
                pilot.stop();
                findLine(L,pilot);
            }
        }
    }

    private static void findLine(ColorSensor L, DifferentialPilot pilot) {

        boolean found = false;
        int i = 4;
        int k = 0;
        while(!found && Button.ESCAPE.isUp()){
         //   if(k % 2 == 0)
                pilot.rotate(i);
          //  else  pilot.rotate(i*2);
            Delay.msDelay(200);
            found = ((L.getNormalizedLightValue() + 40 > L.getLow()) && (L.getNormalizedLightValue() - 40 < L.getLow()));
            Delay.msDelay(200);
            if(found) break;
            else {
     //           if(k % 2 == 0)
                    pilot.rotate(-i*2);
       //         else pilot.rotate(-i);
            }
            Delay.msDelay(200);
            found = ((L.getNormalizedLightValue() + 40 > L.getLow()) && (L.getNormalizedLightValue() - 40 < L.getLow()));
            Delay.msDelay(200);
            if(found) break;
            pilot.rotate(i);
            i = i + 3;
            k = k+1;
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

    public static void setSpeed(double speed, NXTRegulatedMotor A) {
        A.setSpeed((float) speed);
    }

    public static void setAcceleration(double acc, NXTRegulatedMotor A) {
        A.setAcceleration((int) acc);
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

    public static double getDistance(UltrasonicSensor US) {
        return US.getDistance();
    }

    public static boolean isPressed(TouchSensor TS) {
        return TS.isPressed();
    }

    public static boolean isPressed(Button b) {
        return b.isDown();
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

    public static double getColor(ColorSensor CS) {
        return CS.getNormalizedLightValue();
    }

    public static void calibrateWhiteColor(ColorSensor CS) {
        CS.setHigh(CS.getNormalizedLightValue());
    }

    public static void calibrateBlackColor(ColorSensor CS) {
        CS.setLow(CS.getNormalizedLightValue());
    }

    public static double getHighValue(ColorSensor CS) {
        return CS.getHigh();
    }

    public static double getLowValue(ColorSensor CS) {
        return CS.getLow();
    }

    public static void delay(int microsec) {
        Delay.msDelay(microsec);
    }
}
