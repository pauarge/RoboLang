/*
a = $S2.getColor();
b = 1;
while(b < 3) {
  //  $A.move(true);
  //  delay(1000);
  //  $A.stop();
    b = b + 1;
    $C.rotationMove(360);
}

if($A.getSpeed() > 1){
    $A.setSpeed(5);
}
*/

print(200.1);
print("Hola");
delay(1000);
clearDisplay();

forward();
delay(500);
stop();

moveFront(20);

backward();
delay(500);
stop();

$A.move(true);
$A.stopMotor();

moveBack(20);

$BESCAPE.waitToBePressed();

rotateLeft(45);
rotateRight(45);

while($S2.getColor() > 2){
    a = 2;
}

$B.rotationMove(360);


explore();

exploreUltrasonic();

followLine();

followLine2();

shoot(3);

party();

waitForPress();

$C.setSpeed(360);

$C.setAcceleration(3);

dist = $S4.getDistance();

bool = $S1.isPressed();

bool2 = $BESCAPE.isPressed();


print($C.getSpeed());
print($C.getAcceleration());

clearDisplay();

max = $A.getMaxSpeed();

print($S2.getColor());



