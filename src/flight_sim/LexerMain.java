package flight_sim;

import java.util.ArrayList;
import java.util.Scanner;

public class LexerMain<V> implements Lexer {
    private Scanner scan;
    private ArrayList<String[]> lines = new ArrayList<>();
    private String[] stringarray=null;

    public LexerMain(String v) {
        scan = new Scanner(v);
    }
    public LexerMain(String[] s)
    {
        stringarray=s;
    }
    public LexerMain(V v) {
        scan = new Scanner((Readable) v);

    }
    public ArrayList<String[]> lex() {
        if(stringarray!=null)
        {
            for (String s:stringarray) {

                lines.add(s.replaceFirst("=", " = ").replaceFirst("\t","").split("\\s+"));
            }

        }
        else
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine().replaceFirst("="," = ").split("\\s+"));
            }
        return lines;

    }
}
