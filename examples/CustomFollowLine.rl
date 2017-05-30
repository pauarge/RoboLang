

def follow(){
    print("calibrate white color");
    print("Press any button to Continue");
    waitForPress();
    $S2.calibrateWhiteColor();
    clearDisplay();
    print("calibrate black color");
    print("Press any button to Continue");
    waitForPress();
    $S2.calibrateBlackColor();
    print("Put the robot on the left side");
    waitForPress();
    clearDisplay();
    $A.setAcceleration($A.getAcceleration()/4);
    $B.setAcceleration($B.getAcceleration()/4);
    $A.setSpeed($A.getSpeed()/2);
    $B.setSpeed($B.getSpeed()/2);

    while(not($BESCAPE.isPressed())){
        if(($S2.getColor() + 70> $S2.getHighValue() and $S2.getColor() -70 < $S2.getHighValue())){
            $B.stop();
            $A.move(true);
        }
        else {
            $A.stop();
            $B.move(true);
        }
    }

}


follow();

