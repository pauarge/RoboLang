tokens {
    LIST_INSTRUCTIONS;
    FUNCTION;
}

prog        :   list_instr -> ^(LIST_INSTRUCTIONS list_instr);

list_instr  :   (instr ';'!)+ ;

instr       :   while
            |   for
            |   if
            |   assign
            |   func
            ;
            
while       :   WHILE^ '('! expr ')'! '{'! list_instr '}'! ;

expr        :   boolterm (OR^ boolterm)* ;

boolterm    :   boolfact (AND^ boolfact)* ;

boolfact    :   num_expr ((EQUAL^ | NOT_EQUAL^ | LT^ | LE^ | GT^ | GE^) num_expr)? ;

num_expr    :   term ( (ADD^ | SUB^) term)* ;

term        :   factor ( (TIMES^ | DIV^ | MOD^) factor)* ;

factor      :   (ADD^ | SUB^)? atom ;

atom        :   VAR
            |   funcall
            |   NUM
            |   '$'^ VAR
            ;

funcall     :   VAR '(' expr_list ')' -> ^(FUNCTION VAR ^(e));
expr_list   :   expr (','! expr)* ;




WHILE   :   'while';
FOR     :   'for';
IF      :   'if';
ELSE IF :   'elif';
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
VAR     :   ('a'..'z''A'..'Z')+ ('0'..'9''a'..'z''A'..'Z')*;
DEF     :   'def';
NUM     :   ('0'..'9')+ ('.' ('0'..'9')+)?;
FRONT   :   'move_front';
BACK    :   'move_back';
ROTATE  :   'rotate';
RETURN  :   'return';
PRINT   :   'print';

// C-style comments
COMMENT	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    	| '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    	;

// Strings (in quotes) with escape sequences
STRING  :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
        ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    ;

// White spaces
WS  	: ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    	;
