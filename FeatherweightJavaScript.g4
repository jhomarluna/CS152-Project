grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE     : 'while' ;
FUNCTION  : 'function' ;
VAR       : 'var' ;
PRINT     : 'print';
ID        : [a-zA-Z_][a-zA-Z0-9_]* ;
// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL      :  'true' | 'false' ;
NULL      : 'null' ;
// Symbols
MUL       : '*' ;
DIV       : '/' ;
SUB       : '-' ;
ADD       : '+' ;
MOD       : '%' ;
COMPARE_OPERATERS    :  '>' | '<' | '>=' | '<=' | '==' ;
SEPARATOR : ';' ;


// Whitespace and comments
NEWLINE       : '\r'? '\n' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
WS            : [ \t]+ -> skip ; // ignore whitespace


// ***Parsing rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block                          # while
    | PRINT '(' expr ')'                                # print
    | SEPARATOR                                         # emptyStatement
    ;

expr: expr op=( '*' | '/' | '%' ) expr                  # MulDivMod
    | expr op=( '+' | '-') expr                         # AddSub
    | expr op=( '<' | '<=' | '>' | '>=' | '==') expr    # Cmp
    | functionDeclaration                               # FuncDecl
    | functionApplication                               # FuncApp
    | INT                                               # int
    | '(' expr ')'                                      # parens
    ;

functionDeclaration: FUNCTION '(' parameterList? ')' '{' expr '}' ;

functionApplication: ID '(' argumentList? ')' ;

argumentList: expr (',' expr)* ;

parameterList: ID (',' ID)* ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;
