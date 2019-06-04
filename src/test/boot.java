package test;

import flight_sim.LexerMain;
import flight_sim.ParserMain;

import java.io.File;

public class boot {
    public static void main(String[] args){
        String[] script={
                "openDataServer 5400 10",
                " connect 127.0.0.1 5402",
                "var breaks = bind /controls/flight/speedbrake",
                "var throttle = bind /controls/engines/current-engine/throttle",
                "var heading = bind /instrumentation/heading-indicator/offset-deg",
                "var airspeed = bind /instrumentation/airspeed-indicator/indicated-speed-kt",
                "var roll = bind /instrumentation/attitude-indicator/indicated-roll-deg",
                "var pitch = bind /instrumentation/attitude-indicator/internal-pitch-deg",
                "var rudder = bind /controls/flight/rudder",
                "var aileron = bind /controls/flight/aileron",
                "var elevator = bind /controls/flight/elevator",
                "var alt = bind /instrumentation/altimeter/indicated-altitude-ft",
                "breaks = 0",
                "throttle = 1",
                "var h0 = heading",
                "while alt < 1000 {",
                  "rudder = (h0 – heading)/20",
                  "aileron = - roll / 70",
                  "elevator = pitch / 50",
                  "print alt",
                  "sleep 250",
                "}",
                "print done",
        };
        String[] script2={
                "openDataServer 5400 10"

        };
        double d=7.0;
        
        LexerMain lex=new LexerMain("script.txt");
        ParserMain par=new ParserMain(lex.lex());
        par.parse();
    }
}
