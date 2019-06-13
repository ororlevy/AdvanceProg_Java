package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Model extends Observable implements Observer {
    private ClientSim clientSim;
    public static volatile boolean stop=false;
    private Interpter interpter;

    public Model() {
        clientSim=new ClientSim();
        interpter=new Interpter();
    }

    public void GetPlane(){
            new Thread(()->{
                Socket socket = null;
                try {
                    socket = new Socket("127.0.0.1", 5402);
                    System.out.println("Connect to server");
                    PrintWriter out=new PrintWriter(socket.getOutputStream());
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(!stop){
                        out.println("dump /position");
                        out.flush();
                        String line;
                        ArrayList<String> lines = new ArrayList<>();
                        while (!(line = br.readLine()).equals("</PropertyList>")){
                            if(!line.equals(""))
                                lines.add(line);
                        }
                        String longtitude = lines.get(2);
                        String latitude = lines.get(3);
                        String[] x=longtitude.split("[<>]");
                        //System.out.println(x[2]);
                        String[] y=latitude.split("[<>]");
                        //System.out.println(y[2]);
                        br.readLine();
                        out.println("get /instrumentation/heading-indicator/indicated-heading-deg");
                        out.flush();
                        String[] h=br.readLine().split(" ");
                        int tmp=h[3].length();
                        String[] data={x[2],y[2],h[3].substring(1,tmp-1)};
                        this.setChanged();
                        this.notifyObservers(data);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }).start();
        }

    public void parse(String[] script){
        interpter.interpet(script);
    }

    public void execute()
    {
        interpter.execute();
    }

    public void stopAutoPilot()
    {
        interpter.stop();
    }
    @Override
    public void update(Observable o, Object arg) {

    }
}
