lexer grammar TLexer;

options {
   language=Java;  // Default
}

// What package should the generated source exist in?
//
@header {

    package me.pauarge.robolang;
}

// This is just a simple lexer that matches the usual suspects
//

ASSIGN  :   '=';
FOR     :   'for';
IN      :   'in';
WHILE   :   'while';
IF      :   'if';
ELIF    :   'elif';
ELSE    :   'else';
RETURN  :   'return';
DEF     :   'def';
AND     :   'and';
OR      :   'or';
NOT     :   'not';
GET     :   '>=';
LET     :   '<=';
EQ      :   '==';
NEQ     :   '!=';
GT      :   '>';
LT      :   '<';
ADD     :   '+';
SUB     :   '-';
TIMES   :   '*';
DIV     :   '/';
MOD     :   '%';
LPAR    :   '(';
RPAR    :   ')';
LCOR    :   '[';
RCOR    :   ']';
LBRA    :   '{';
RBRA    :   '}';
DOLLAR  :   '$';
COMMA   :   ',';
REF     :   '&';
TRUE    :   'true';
FALSE   :   'false';
NUM     :   ('0'..'9')+ ('.' ('0'..'9')+)?;
MR      :   ('mf'| 'rh' | 'ra' |'mb') '-' ('0'..'9')+ ('.' ('0'..'9')+)?;
VAR     :   ('a'..'z' | 'A'..'Z') ('0'..'9' | 'a'..'z' | 'A'..'Z' | '_')* ;
SEMI    :   ';';

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
