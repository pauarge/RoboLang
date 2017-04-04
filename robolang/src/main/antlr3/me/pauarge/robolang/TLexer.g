// This is a sample lexer generated by the ANTLR3 Maven Archetype
// generator. It shows how to use a superclass to implement
// support methods and variables you may use in the lexer actions
// rather than create a messy lexer that has the Java code embedded
// in it.
//

lexer grammar TLexer;

options {

   language=Java;  // Default

   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and 
   // variables will be placed.
   //
   superClass = AbstractTLexer;
}

// What package should the generated source exist in?
//
@header {

    package me.pauarge.robolang;
}

// This is just a simple lexer that matches the usual suspects
//

SEMI    : ';' ;

WHILE   :   'while';
FOR     :   'for';
IF      :   'if';
ELIF    :   'elif';
ELSE    :   'else';
AND     :   'and';
OR      :   'or';
NOT     :   'not';
GT      :   '>';
LT      :   '<';
GET     :   '>=';
LET     :   '<=';
EQ      :   '==';
ASSIGN  :   '=';
ADD     :   '+';
SUB     :   '-';
TIMES   :   '*';
DIV     :   '/';
MOD     :   '%';
VAR     :   ('a'..'z''A'..'Z')+ ('0'..'9''a'..'z''A'..'Z' | '_')* ;
DEF     :   'def';
NUM     :   ('0'..'9')+ ('.' ('0'..'9')+)?;
FRONT   :   'move_front';
BACK    :   'move_back';
ROTATE  :   'rotate';
RETURN  :   'return';
PRINT   :   'print';
LPAR    :   '(';
RPAR    :   ')';
LBRA    :   '{';
RBRA    :   '}';
DOLLAR  :   '$';
COMMA   :   ',';
REF     :   '&';

// C-style comments
COMMENT	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    	| '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    	;

// Strings (in quotes) with escape sequences
STRING  :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
        ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {skip();}
    ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
