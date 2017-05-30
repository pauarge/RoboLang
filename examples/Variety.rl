


def recursiveFunc(speed,acc,iteration){
    if(iteration <= 8) {
        forward();
        $A.setSpeed(speed);
        $B.setSpeed(speed);
        $A.setAcceleration(acc);
        $B.setAcceleration(acc);
        delay(500);
        recursiveFunc(speed*2,acc*2,iteration+1);
    }
}

recursiveFunc($A.getSpeed(),20,0);
stop();

a = 6 + 4;
b = 10 - a;
print(b);

b = 101;
b = b % 10;
print(b);

b = (b * (10 - 3))/2;
print(b);
waitForPress();
print("Press to continue and clear LCR");
clearDisplay();

color = $S2.getColor();
print("Color: ");
print(color);
waitForPress();