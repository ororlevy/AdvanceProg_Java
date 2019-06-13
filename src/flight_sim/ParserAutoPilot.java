package flight_sim;


import Commands.CommandExpression;

import java.util.ArrayList;

public class ParserAutoPilot {
    ParserMain p;
    public static volatile boolean stop=true;
    public static Thread  exe;
    public int i = 0;
    public ParserAutoPilot(ParserMain p) {
        this.p = p;


    }
    public void parse() {
        p.parse();
        i=0;
    }

    public void execute(){

        exe=new Thread(()->{
            while(true) {
                while (!stop && i < p.comds.size()) {
                    p.comds.get(i).calculate();
                    i++;
                }
            }
        });

        exe.start();

    }
    public void add(ArrayList<String[]> lines){
        p.lines.clear();
        p.lines.addAll(lines);
        ParserMain.symTbl.put("stop",new Var(1));
        for (String[] s:p.lines) {
            if (s[0].equals("while"))
            {
                StringBuilder tmp=new StringBuilder(s[s.length-2]);
                tmp.append("&&stop!=0");
                s[s.length-2]=tmp.toString();
            }
        }
    }
    public void stop(){
        ParserMain.symTbl.get("stop").setV(0);
        ParserAutoPilot.stop=true;
    }
    public void Continue()
    {
        ParserMain.symTbl.get("stop").setV(1);
    }
}
