grammar ICSS;

//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: --- all levels
stylesheet: (stylerule | variableAssignment)+ EOF;
stylerule: selector body;

variableAssignment: variableReference assignmentOperator literal semicolon;

body: openBrace (declarations | variableAssignment)+ closebrace;
openBrace: OPEN_BRACE;
closebrace: CLOSE_BRACE;

selector: tagSelector | idSelector | classSelector;
tagSelector: LOWER_IDENT;
classSelector: CLASS_IDENT;
idSelector: ID_IDENT;

declarations: declaration+ | ifClause+;
declaration: propertyName colon expression semicolon;
propertyName: LOWER_IDENT | CAPITAL_IDENT;
expression: (literal | variableReference) | operation;

ifClause: IF boxbracketOpen (variableReference | boolLiteral) boxbracketClose body;

boxbracketOpen: BOX_BRACKET_OPEN;
boxbracketClose: BOX_BRACKET_CLOSE;

assignmentOperator: ASSIGNMENT_OPERATOR;
semicolon: SEMICOLON;
colon: COLON;
variableReference: LOWER_IDENT | CAPITAL_IDENT;

operation: value #valueExpression | operation MUL operation #multiplyOperation | operation PLUS operation #addOperation | operation MIN operation #subtractOperation;
//CH03
value: variableReference | noColorLiteral;
noColorLiteral: pixelLiteral | boolLiteral | scalarLiteral | percentageLiteral | variableReference;
literal: colorLiteral | noColorLiteral;
colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
boolLiteral: TRUE | FALSE;
scalarLiteral: SCALAR;
percentageLiteral: PERCENTAGE;
