package flight_sim;

import Commands.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ParserMain implements Parser {
    private HashMap<String, CommandExpression> cmdTbl=new HashMap<>();
    private GenericFactory cmdFac=new GenericFactory();
    public static HashMap<String,Var> symTbl;
    private ArrayList<String[]> lines;
    private ArrayList<CommandExpression> comds;
    public static double returnval;

    public ParserMain(ArrayList<String[]> lines) {
        this.comds=new ArrayList<>();
        this.lines = lines;
        symTbl=new HashMap<>();
        cmdFac.insertProduct("openDataServer",OpenDataServer.class);
        cmdFac.insertProduct("connect",ConnectCommand.class);
        cmdFac.insertProduct("while",LoopCommand.class);
        cmdFac.insertProduct("var",DefineVarCommand.class);
        cmdFac.insertProduct("return",ReturnCommand.class);
        cmdFac.insertProduct("=",AssignCommand.class);
        cmdFac.insertProduct("disconnect",DisconnectCommand.class);
        cmdTbl.put("openDataServer", new CommandExpression(new OpenDataServer()));
        cmdTbl.put("connect",new CommandExpression(new ConnectCommand()));
        cmdTbl.put("while",new CommandExpression(new LoopCommand()));
        cmdTbl.put("var",new CommandExpression(new DefineVarCommand()));
        cmdTbl.put("return",new CommandExpression(new ReturnCommand()));
        cmdTbl.put("=",new CommandExpression(new AssignCommand()));
        cmdTbl.put("disconnect",new CommandExpression(new DisconnectCommand()));
        symTbl.put("simX",new Var());
        symTbl.put("simY",new Var());
        symTbl.put("simZ",new Var());
    }

    private ArrayList<CommandExpression> parseCommands(ArrayList<String[]> array){
        ArrayList<CommandExpression> commands=new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            //CommandExpression e=cmdTbl.get(array.get(i)[0]);
            CommandExpression e=new CommandExpression((Command)cmdFac.getNewProduct(array.get(i)[0]));
            if(e.getC()!=null) {
                if (array.get(i)[0].equals("while")) {
                    int index = i;
                    while (!array.get(i)[0].equals("}"))
                        i++;
                    e.setC(this.parseCondition(new ArrayList<>(array.subList(index, i))));
                }
                e.setS(array.get(i));
            }

            else {
                //e = cmdTbl.get(array.get(i)[1]);
                e=new CommandExpression((Command)cmdFac.getNewProduct(array.get(i)[1]));
                e.setS(array.get(i));
            }
            commands.add(e);
        }
        return commands;
    }

    public double parse() {
        this.comds=this.parseCommands(lines);
        for (CommandExpression e:comds) {
            e.calculate();
        }
        return returnval;
    }



    private Command parseCondition(ArrayList<String[]> array) {

        ConditionCommand c=(ConditionCommand)cmdTbl.get(array.get(0)[0]).getC();
        int i=0;
        ArrayList<CommandExpression> tmp=new ArrayList<>();
        CommandExpression cmdtmp=new CommandExpression(new PredicateCommand());
        cmdtmp.setS(array.get(0));
        tmp.add(cmdtmp);
        c.setCommands(tmp);
        c.getCommands().addAll(1,this.parseCommands(new ArrayList<>(array.subList(i+1,array.size()))));
        //c.setCommands(this.parseCommands(new ArrayList<>(array.subList(i+1,array.size()))));
       // c.setLines(new ArrayList<>(array.subList(1, array.size()-1)));
        //c.setLines(new ArrayList<>(array.subList(0,array.size())));
        //cmdTbl.get(array.get(0)[0]).setC(c);
        return c;

    }


}
