def shoot2(a) {
    while(a > 0) {
        $x1 = true;
        $x1 = false;
        a = a-1;
    }
}
    
x = 2;
y = x*3;
b = [rh-45,mf-10];
if(x < y) {
  for(i in [mf-2,mb-5,rh-90]) {
    move(i);
    x = 2 +2 ;
  }
}
elif (x > y and true) { 
    move_back(5);
    while (x > 0 and y >= x) {
        shoot2(2);
        move_front(1);
    }
}
elif (true or y < x) {
    explore();
}
else { b = b[2] + [mf-5]; }

