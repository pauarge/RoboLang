parser grammar TParser;

options {
    language  = Java;
    output    = AST;
    tokenVocab = TLexer;
}

tokens {
    LIST_INSTR;
    FUNCTION;
    FUNCALL;
    PARAMS;
    COND;
    PREF;
    PVALUE;
    DOLLAR;
}

@header {
    package com.robolang;
}

prog        :   list_instr -> ^(LIST_INSTR list_instr) ;

list_instr  :   (instr)+ ;

instr       :   loop
            |   cond
            |   assign SEMI!
            |   func
            |   funcall SEMI!
            |   dollar SEMI!
            |   importst SEMI!
            ;

importst    :   IMPORT^ VAR ;

dollar      :   PORT DOT funcall -> ^(DOLLAR PORT funcall);

assign      :   VAR ASSIGN expr -> ^(ASSIGN VAR expr);

func        :   DEF VAR LPAR params RPAR LBRA list_instr? ret? RBRA -> ^(FUNCTION VAR params ^(LIST_INSTR list_instr)? ret?) ;

ret         :   (RETURN^ expr SEMI!);

params      :   list_param? -> ^(PARAMS list_param?) ;

list_param  :   VAR (COMMA! VAR)*;

cond        :   cond2 -> ^(COND cond2);

cond2       :   ifst elifst* elsest?;

ifst        :   IF LPAR expr RPAR LBRA list_instr RBRA -> ^(IF expr ^(LIST_INSTR list_instr)) ;

elifst      :   ELIF LPAR expr RPAR LBRA list_instr RBRA -> ^(ELIF expr ^(LIST_INSTR list_instr));

elsest      :   ELSE LBRA list_instr RBRA -> ^(ELSE ^(LIST_INSTR list_instr));
            
loop        :   WHILE LPAR expr RPAR LBRA list_instr RBRA -> ^(WHILE expr ^(LIST_INSTR list_instr));

expr        :   boolterm (OR^ boolterm)* ;

boolterm    :   boolfact (AND^ boolfact)* ;

boolfact    :   TRUE | FALSE | num_expr ((EQ^ | NEQ^ | LT^ | LET^ | GT^ | GET^) num_expr)? ;

num_expr    :   term ( (ADD^ | SUB^) term)* ;

term        :   factor ( (TIMES^ | DIV^ | MOD^) factor)* ;

factor      :   (ADD^ | SUB^ | NOT^)? atom ;

atom        :   NUM
            |   STRING
            |   VAR
            |   funcall
            |   LPAR! expr RPAR!
            |   dollar
            ;

funcall     :   VAR LPAR expr_list? RPAR -> ^(FUNCALL VAR ^(PARAMS expr_list?)) ;
expr_list   :   expr (COMMA! expr)* ;
