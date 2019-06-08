package Model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSim {
    public static volatile boolean stop=false;
    private PrintWriter out;
    public void Connect(String ip,int port){
        new Thread(()->{
            Socket socket = null;
            try {
                socket = new Socket(ip, port);
                System.out.println("Connect to server");
                out=new PrintWriter(socket.getOutputStream());
                while(!stop){


                }

                out.close();
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

    public void Send(String[] data){
        for (String s: data) {
            System.out.println(s);
        }

    }
}
