test = "Hola nom";

if(distance(45,true) > distance(45,false)) {
    rotate(45,true);
} else {
    rotate(45,false);
}

while(distance() > 0.5){
    move_front(0.5);
}
def random_moves(){
    moves = [mf-4,ra-45,mb-10,rh-3,mf-7];
    for(i in moves){
        move(i);
    }
}
i = 0;
while(i < 7) {
    random_moves();
    i = i + 1;
}
