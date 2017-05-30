

def ultrasonicDistance() {
    return $S4.getDistance();
}

//El robot es dirigeix cap a on tingi més recorregut.
// dist0(0 graus), dist1 (90 graus), dist2 (180) ...

def goMaxDistance(dist0, dist1, dist2, dist3) {
    if(dist0 >= dist1 and dist0 >= dist2 and dist0 >= dist3) {moveFront(dist0 - 10);}
    elif(dist1 >= dist2 and dist1 >= dist0 and dist0 >= dist3){
        rotateRight(90);
        moveFront(dist1 - 10);
    }
    elif(dist2 >= dist1 and dist2 >= dist0 and dist2 >= dist3) {
        rotateRight(180);
        moveFront(dist2 - 10);
    }
    else {
        rotateRight(270);
        moveFront(dist3 - 10);
    }

}



