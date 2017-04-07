omentari de
*   múltiples 
*   linies.
*/

//Definim una funció que intenta allunyar un objecte si aquest es proper
def allunya(){
    while(distance(0,true) < 0.3) {
        shoot(1); 
    }
}
//Li assignem a la variable "a" un moviment frontal de 0.3 unitats. 
a = mf-0.3;
i = 0;
while(i < 3){
	allunya();
    //Allunyem l'objecte i ens avancem cap a ell.
    move(a);
    i = i + 1;
}
rotate(-45);
follow_line(1);
