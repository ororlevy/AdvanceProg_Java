package Commands;

import Experssions.ShuntingYard;
import flight_sim.ParserMain;
import flight_sim.Var;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectCommand implements Command {
	public static volatile boolean stop=false;
	@Override
	public void doCommand(String[] array) {
		stop=false;
		new Thread(()->{
			try {
				Socket socket= null;
				try {
					socket = new Socket(array[1], (int) ShuntingYard.calc(array[2]));
					PrintWriter out=new PrintWriter(socket.getOutputStream());
					while(!stop){
						if(ParserMain.symTbl.get("simX").hasChanged()) {
							out.println("set simX" + ParserMain.symTbl.get("simX"));
							out.flush();
						}
						if(ParserMain.symTbl.get("simY").hasChanged()) {
							out.println("set simY" + ParserMain.symTbl.get("simY"));
							out.flush();
						}
						if(ParserMain.symTbl.get("simZ").hasChanged()) {
							out.println("set simZ" + ParserMain.symTbl.get("simZ"));
							out.flush();
						}
					}
					out.println("bye");
					out.close();
					socket.close();


				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();


	}

}
