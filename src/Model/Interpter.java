package Model;

import Commands.CommandExpression;
import Commands.DisconnectCommand;
import flight_sim.*;

public class Interpter {
    ParserAutoPilot parser;
    Lexer lexer;

   public void interpet(String[] list){
       lexer=new LexerMain(list);
       parser=new ParserAutoPilot(new ParserMain(lexer.lex()));
       parser.parse();
   }
   public void execute()
   {
       ParserAutoPilot.stop=false;
       parser.execute();

   }
}
