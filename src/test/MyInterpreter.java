package test;

import flight_sim.LexerMain;
import flight_sim.ParserMain;

public class MyInterpreter {

	public static  int interpret(String[] lines){
		LexerMain lex=new LexerMain(lines);
		ParserMain p=new ParserMain(lex.lex());
		p.parse();
		return (int)p.execute();
	}
}
