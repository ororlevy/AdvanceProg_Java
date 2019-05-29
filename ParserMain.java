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
    public static HashMap<String, String> varLocations = new HashMap<>();

    public ParserMain(ArrayList<String[]> lines) {
    	varLocations.put("airspeed-indicator_indicated-speed-kt","/instrumentation/airspeed-indicator/indicated-speed-kt");
    	varLocations.put("altimeter_indicated-altitude-ft","/instrumentation/altimeter/indicated-altitude-ft");
    	varLocations.put("altimeter_pressure-alt-ft","/instrumentation/altimeter/pressure-alt-ft");
    	varLocations.put("attitude-indicator_indicated-pitch-deg","/instrumentation/attitude-indicator/indicated-pitch-deg");
    	varLocations.put("attitude-indicator_indicated-roll-deg","/instrumentation/attitude-indicator/indicated-roll-deg");
    	varLocations.put("attitude-indicator_internal-pitch-deg","/instrumentation/attitude-indicator/internal-pitch-deg");
    	varLocations.put("attitude-indicator_internal-roll-deg","/instrumentation/attitude-indicator/internal-roll-deg");
    	varLocations.put("encoder_indicated-altitude-ft","/instrumentation/encoder/indicated-altitude-ft");
    	varLocations.put("encoder_pressure-alt-ft","/instrumentation/encoder/pressure-alt-ft");
    	varLocations.put("gps_indicated-altitude-ft","/instrumentation/gps/indicated-altitude-ft");
    	varLocations.put("gps_indicated-ground-speed-kt","/instrumentation/gps/indicated-ground-speed-kt");
    	varLocations.put("gps_indicated-vertical-speed","/instrumentation/gps/indicated-vertical-speed");
    	varLocations.put("indicated-heading-deg","/instrumentation/heading-indicator/indicated-heading-deg");
    	varLocations.put("magnetic-compass_indicated-heading-deg","/instrumentation/magnetic-compass/indicated-heading-deg");
    	varLocations.put("slip-skid-ball_indicated-slip-skid","/instrumentation/slip-skid-ball/indicated-slip-skid");
    	varLocations.put("turn-indicator_indicated-turn-rate","/instrumentation/turn-indicator/indicated-turn-rate");
    	varLocations.put("vertical-speed-indicator_indicated-speed-fpm","/instrumentation/vertical-speed-indicator/indicated-speed-fpm");
    	varLocations.put("flight_aileron","/controls/flight/aileron");
    	varLocations.put("flight_elevator","/controls/flight/elevator");
    	varLocations.put("flight_rudder","/controls/flight/rudder");
    	varLocations.put("flight_flaps","/controls/flight/flaps");
    	varLocations.put("engine_throttle","/controls/engines/engine/throttle");
    	varLocations.put("engine_rpm","/engines/engine/rpm");
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
        symTbl.put("airspeed-indicator_indicated-speed-kt",new Var("/instrumentation/airspeed-indicator/indicated-speed-kt"));
        symTbl.put("altimeter_indicated-altitude-ft",new Var("/instrumentation/altimeter/indicated-altitude-ft"));
        symTbl.put("altimeter_pressure-alt-ft",new Var("/instrumentation/altimeter/pressure-alt-ft"));
        symTbl.put("attitude-indicator_indicated-pitch-deg",new Var("/instrumentation/attitude-indicator/indicated-pitch-deg"));
        symTbl.put("attitude-indicator_indicated-roll-deg",new Var("/instrumentation/attitude-indicator/indicated-roll-deg"));
        symTbl.put("attitude-indicator_internal-pitch-deg",new Var("/instrumentation/attitude-indicator/internal-pitch-deg"));
        symTbl.put("attitude-indicator_internal-roll-deg",new Var("/instrumentation/attitude-indicator/internal-roll-deg"));
        symTbl.put("encoder_indicated-altitude-ft",new Var("/instrumentation/encoder/indicated-altitude-ft"));
        symTbl.put("encoder_pressure-alt-ft",new Var("/instrumentation/encoder/pressure-alt-ft"));
        symTbl.put("gps_indicated-altitude-ft",new Var("/instrumentation/gps/indicated-altitude-ft"));
        symTbl.put("gps_indicated-ground-speed-kt",new Var("/instrumentation/gps/indicated-ground-speed-kt"));
        symTbl.put("gps_indicated-vertical-speed",new Var("/instrumentation/gps/indicated-vertical-speed"));
        symTbl.put("indicated-heading-deg",new Var("/instrumentation/heading-indicator/indicated-heading-deg"));
        symTbl.put("magnetic-compass_indicated-heading-deg",new Var("/instrumentation/magnetic-compass/indicated-heading-deg"));
        symTbl.put("slip-skid-ball_indicated-slip-skid",new Var("/instrumentation/slip-skid-ball/indicated-slip-skid"));
        symTbl.put("turn-indicator_indicated-turn-rate",new Var("/instrumentation/turn-indicator/indicated-turn-rate"));
        symTbl.put("vertical-speed-indicator_indicated-speed-fpm",new Var("/instrumentation/vertical-speed-indicator/indicated-speed-fpm"));
        symTbl.put("flight_aileron",new Var("/controls/flight/aileron"));
        symTbl.put("flight_elevator",new Var("/controls/flight/elevator"));
        symTbl.put("flight_rudder",new Var("/controls/flight/rudder"));
        symTbl.put("flight_flaps",new Var("/controls/flight/flaps"));
        symTbl.put("engine_throttle",new Var("/controls/engines/engine/throttle"));
        symTbl.put("engine_rpm",new Var("/engines/engine/rpm"));
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
