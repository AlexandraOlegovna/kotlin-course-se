grammar Exp;

file
    : block
    ;

block
    : statement*
    ;

blockWithBraces
    : '{' block '}'
    ;

statement
    : function
    | variable
    | expression
    | whileStatement
    | ifStatement
    | assignment
    | returnStatement
    ;

function
    : 'fun' Identifier '(' parameterNames ')' blockWithBraces
    ;

variable
    : 'var' Identifier ('=' expression)?
    ;

parameterNames
    : (Identifier (',' Identifier)*)?
    ;

whileStatement
    : 'while' '(' expression ')' blockWithBraces
    ;

ifStatement
    : 'if' '(' expression ')' blockWithBraces ('else' blockWithBraces)?
    ;

assignment
    : Identifier '=' expression
    ;

returnStatement
    : 'return' expression
    ;

expression
    : binaryE
    | atomicE
    ;

atomicE
    : functionCall          #functionCallAtomicExpr
    | Identifier            #indentifierAtomicExpr
    | Literal               #literalAtomicExpr
    | '(' expression ')'    #expressionsAtomicExpr
    ;

functionCall
    : Identifier '(' arguments ')'
    ;

arguments
    : (expression (',' expression)*)?
    ;

binaryE
    : atomicE op=('*'|'/'|'%') expression
    | atomicE op=('+'|'-') expression
    | atomicE op=('>'|'<'|'>='|'<=') expression
    | atomicE op=('=='|'!=') expression
    | atomicE op='&&' expression
    | atomicE op='||' expression
    ;

Identifier
    : [a-zA-Z_] ([a-zA-Z_] | [0-9])*
    ;

Literal
    : ([1-9] [0-9]* | '0')
    ;

COMMENT
    : '//' ~[\n]* -> skip
    ;

WS
    : (' ' | '\t' | '\r'| '\n') -> skip
    ;