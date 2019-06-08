package flight_sim;

import Commands.CommandExpression;

public class ParsherAutoPilot {
    ParserMain p;
    public static volatile boolean stop=true;

    public ParsherAutoPilot(ParserMain p) {
        this.p = p;
    }
    public void parse() {
        p.parse();
    }

    public void execute(){
        new Thread(()->{
            while(true)
            {
                int i=0;
                while(!stop){
                    p.comds.get(i).calculate();
                    i++;
                }

            }
        }).start();

    }
}
