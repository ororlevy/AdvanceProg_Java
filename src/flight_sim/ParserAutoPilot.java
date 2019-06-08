package flight_sim;

import Commands.CommandExpression;
import Commands.DisconnectCommand;
import Commands.LoopCommand;

public class ParserAutoPilot {
    ParserMain p;
    public static volatile boolean stop=true;
    public static Thread  exe;

    public ParserAutoPilot(ParserMain p) {
        this.p = p;
    }
    public void parse() {
        p.parse();
    }

    public void execute(){
        exe=new Thread(()->{
            while(true)
            {
                int i=0;
                while(!stop){
                    p.comds.get(i).calculate();
                    i++;
                }

            }
        });

        exe.start();

    }
}
