import funcs;


distFront = ultrasonicDistance();
rotateRight(90);
distRight = ultrasonicDistance();
rotateRight(90);
distBack = ultrasonicDistance();
rotateRight(90);
distLeft = ultrasonicDistance();
rotateRight(90);

goMaxDistance(distFront, distRight, distBack, distLeft);


