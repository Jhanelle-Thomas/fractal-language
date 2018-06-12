/* Specification for Fractal tokens */

// user customisations

package fractal.syntax;
import java_cup.runtime.*;
import fractal.sys.FractalException;
import fractal.sys.FractalLexerException;

// JFlex directives

%%

%cup
%public

%class FractalLexer
%throws FractalException

%type java_cup.runtime.Symbol

%eofval{
	return new Symbol(sym.EOF);
%eofval}

%eofclose false

%char
%column
%line

%{
    private Symbol mkSymbol(int id) {
        return new Symbol(id, yyline, yycolumn);
    }

    private Symbol mkSymbol(int id, Object val) {
        return new Symbol(id, yyline, yycolumn, val);
    }

    public int getChar() {
	return yychar + 1;
    }

    public int getColumn() {
    	return yycolumn + 1;
    }

    public int getLine() {
	return yyline + 1;
    }

    public String getText() {
	return yytext();
    }
%}

nl = [\n\r]

cc = ([\b\f]|{nl})

ws = {cc}|[\t ]

digit = [0-9]

alpha = [_a-zA-Z?]

alphnum = {digit}|{alpha}

%%

<YYINITIAL>	{nl}	{
			 //skip newline
			}
<YYINITIAL>	{ws}	{
			 // skip whitespace
			}

<YYINITIAL>	"//".*	{
			 // skip line comments
			}

<YYINITIAL> {
    "+"			{return mkSymbol(sym.PLUS);}
    "-"			{return mkSymbol(sym.MINUS);}
    "*"			{return mkSymbol(sym.MUL);}
    "/"			{return mkSymbol(sym.DIV);}
    "%"			{return mkSymbol(sym.MOD);}
    "<"			{return mkSymbol(sym.LT);}
    ">"			{return mkSymbol(sym.GT);}
    "="			{return mkSymbol(sym.EQUAL);}

    "("			{return mkSymbol(sym.LPAREN);}
    ")"			{return mkSymbol(sym.RPAREN);}
    "["			{return mkSymbol(sym.LBRACE);}
    "]"			{return mkSymbol(sym.RBRACE);}

    ":"			{return mkSymbol(sym.COLON);}
    "@"			{return mkSymbol(sym.AT);}
    "!"			{return mkSymbol(sym.EXCLAM);}

    "and"			{return mkSymbol(sym.AND);}
    "or"			{return mkSymbol(sym.OR);}
    "not"			{return mkSymbol(sym.NOT);}

    "home"			{return mkSymbol(sym.HOME);}
    "render"			{return mkSymbol(sym.RENDER);}
    "clear"			{return mkSymbol(sym.CLEAR);}
    "save"			{return mkSymbol(sym.SAVE);}
    "restore"			{return mkSymbol(sym.RESTORE);}

    "fractal"			{return mkSymbol(sym.FRACTAL);}
    "self"			{return mkSymbol(sym.SELF);}
    "end"			{return mkSymbol(sym.END);}
    "def" | "define"			{return mkSymbol(sym.DEF);}
    "rep" | "repeat"			{return mkSymbol(sym.REP);}

    "fd" | "forward"			{return mkSymbol(sym.FWD);}
    "bk" | "back"			{return mkSymbol(sym.BACK);}
    "lt" | "left"			{return mkSymbol(sym.LEFT);}
    "rt" | "right"			{return mkSymbol(sym.RIGHT);}
    "pu" | "penup"			{return mkSymbol(sym.PU);}
    "pd" | "pendown"			{return mkSymbol(sym.PD);}


    {digit}+ 		{
			 // INTEGER
	       		 return mkSymbol(sym.INT,
			 	         new Integer(yytext()));
	       		}

	{digit}*\.{digit}+ 		{
			 // REAL NUMBERS
	       		 return mkSymbol(sym.REAL,
			 	         new Double(yytext()));
	       		}

    {alpha}{alphnum}*   {
    		      	 // IDENTIFIERS
	       		 return mkSymbol(sym.ID, yytext());
	       		}

    .			{ // Unknown token (leave this in the last position)
    			  throw new FractalLexerException(yytext(), getLine(),
							  getColumn());
    			}
}
