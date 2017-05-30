def moveNoCrash(){
    forward();
    while(not($BESCAPE.isPressed())){
        if($S4.getDistance() < 20){
            stop();
            moveBack(10);
            while($S4.getDistance() < 25){
                rotateRight(15);
            }
            forward();
        }
    }
    stop();
}

def setSpeedAndAcceleration(speed, acceleration){
    $A.setSpeed(speed); $B.setSpeed(speed);
    $A.setAcceleration(acceleration); $B.setAcceleration(acceleration);
}

s = 100;
a = $A.getAcceleration()/8;
setSpeedAndAcceleration(s,a);
i = 0;
while(i < 4){
    moveFront(50);
    moveBack(50);
    s = s + 100;
    a = a*2;
    setSpeedAndAcceleration(s,a);
    i = i + 1;
}
i = 0;
while(i < 4){
    rotateLeft(360);
    rotateRight(360);
    s = s - 100;
    a = a/2;
    setSpeedAndAcceleration(s,a);
    i = i + 1;
}

moveNoCrash();


