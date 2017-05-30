/*
a = $S2.getColor();
b = 1;
while(b < 3) {
  //  $A.move(true);
  //  delay(1000);
  //  $A.stop();
    b = b + 1;
    $C.rotation_move(360);
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

move_front(20);

backward();
delay(500);
stop();

$A.move(true);
$A.stop_motor();

move_back(20);

rotate_left(45);
rotate_right(45);

while($S2.getColor() > 2){
    a = 2;
}

