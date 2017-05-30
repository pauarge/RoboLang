


def RecursiveFunc(speed,acc){
    moveFront(10);
    $A.setSpeed(speed);
    $B.setSpeed(speed);
    $A.setAcceleration(acc);
    $B.setAcceleration(acc);
    RecursiveFunc(speed*2,acc*2);
}

RecursiveFunc();

