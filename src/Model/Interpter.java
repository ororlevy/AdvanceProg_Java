package Model;

import flight_sim.*;

public class Interpter {
    ParsherAutoPilot parser;
    Lexer lexer;

   public void interpet(String[] list){
       lexer=new LexerMain(list);
       parser=new ParsherAutoPilot(new ParserMain(lexer.lex()));
       parser.parse();
   }
   public void execute()
   {
       ParsherAutoPilot.stop=false;
       parser.execute();

   }
}
