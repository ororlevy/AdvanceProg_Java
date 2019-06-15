package Model;

import server_side.*;
import test_server.TestServer;
import test_server.TestSetter;

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
    private static Socket socketpath;
    private  static PrintWriter outpath;
    private  static BufferedReader in;

    public Model() {
        clientSim=new ClientSim();
        interpter=new Interpter();

        Server s=new MySerialServer(); // initialize
		CacheManager cm=new FileCacheManager();
		MyClientHandler ch=new MyClientHandler(cm);
		s.open(6688,new ClientHandlerPath(ch));
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
                        String[] data={"plane",x[2],y[2],h[3].substring(1,tmp-1)};
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

    public void connectPath(String ip,int port){
        try {
            socketpath=new Socket(ip,port);
            outpath=new PrintWriter(socketpath.getOutputStream());
            in=new BufferedReader(new InputStreamReader(socketpath.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectManual(String ip,int port){
        clientSim.Connect(ip,port);
    }

    public void send(String[] data)
    {
        clientSim.Send(data);
    }

    public void findPath(int planeX,int planeY,int markX,int markY,int[][] data)
    {

        new Thread(()->{

                int j,i;
                System.out.println("\tsending problem...");
                for (i = 0; i < data.length; i++) {
                    System.out.print("\t");
                    for (j = 0; j < data[i].length - 1; j++) {
                        outpath.print(data[i][j] + ",");
                        //System.out.print(data[i][j] + ",");
                    }
                    outpath.println(data[i][j]);

                    //System.out.println(data[i][j]);
                }
                outpath.println("end");
                outpath.println(planeX+","+planeY);
                outpath.println(markX+","+markY);
                outpath.flush();
                String usol = null;
                try {
                    usol = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("\tsolution received");
                System.out.println(usol);
                String[]tmp=usol.split(",");
                String[] notfiy=new String[tmp.length+1];
                notfiy[0]="path";
                for(i=0;i<tmp.length;i++)
                    notfiy[i+1]=tmp[i];
                this.setChanged();
                this.notifyObservers(notfiy);


        }).start();



    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
