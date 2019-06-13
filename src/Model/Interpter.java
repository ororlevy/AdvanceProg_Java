package Model;

import flight_sim.*;

public class Interpter {
    ParserAutoPilot parser;
    Lexer lexer;

    public Interpter() {
        String[] start= {
                "openDataServer 5400 10",
                "connect 127.0.0.1 5402"
        };
        lexer=new LexerMain(start);
        parser=new ParserAutoPilot(new ParserMain(lexer.lex()));
        parser.parse();
        ParserAutoPilot.stop=false;
        parser.execute();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ParserAutoPilot.stop=true;
    }

    public void interpet(String[] list){
       lexer=new LexerMain(list);
       parser.add(lexer.lex());
       parser.parse();
   }
   public void execute()
   {
       if(parser.i!=0)
           parser.i--;
       parser.Continue();
       ParserAutoPilot.stop=false;

   }
   public void stop(){
        parser.stop();
   }
}
