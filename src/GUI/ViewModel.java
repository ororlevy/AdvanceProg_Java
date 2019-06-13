package GUI;

import Model.ClientSim;
import Model.Model;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ViewModel extends Observable implements Observer {
    public DoubleProperty throttle;
    public DoubleProperty rudder;
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public StringProperty ip;
    public StringProperty port;
    private ClientSim clientSim;
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    public DoubleProperty startX;
    public DoubleProperty startY;
    public DoubleProperty offset;
    public StringProperty script;
    public DoubleProperty heading;
    private int data[][];
    private Model model;

    public void setData(int[][] data)
    {
        this.data = data;
        model.GetPlane();
    }

    public ViewModel() {
        this.clientSim = new ClientSim();
        throttle=new SimpleDoubleProperty();
        rudder=new SimpleDoubleProperty();
        aileron=new SimpleDoubleProperty();
        elevator=new SimpleDoubleProperty();
        ip=new SimpleStringProperty();
        port=new SimpleStringProperty();
        airplaneX=new SimpleDoubleProperty();
        airplaneY=new SimpleDoubleProperty();
        startX=new SimpleDoubleProperty();
        startY=new SimpleDoubleProperty();
        offset=new SimpleDoubleProperty();
        script=new SimpleStringProperty();
        heading=new SimpleDoubleProperty();

    }


    public void setModel(Model model){
        this.model=model;
    }

    public void setThrottle(){
        String[] data={"set /controls/engines/current-engine/throttle "+throttle.getValue()};
        clientSim.Send(data);
    }

    public void setRudder(){
        String[] data={"set /controls/flight/rudder "+rudder.getValue()};
        clientSim.Send(data);
    }

    public void setJoystick(){
        String[] data = {
                "set /controls/flight/aileron " + aileron.getValue(),
                "set /controls/flight/elevator " + elevator.getValue(),
        };
        clientSim.Send(data);
    }

    public void connect(){
        clientSim.Connect(ip.getValue(),Integer.parseInt(port.getValue()));
    }

    public void parse(){
        Scanner scanner=new Scanner(script.getValue());
        ArrayList<String> list=new ArrayList<>();
        while(scanner.hasNextLine())
        {
            list.add(scanner.nextLine());
        }
        String[] tmp=new String[list.size()];
        for(int i=0;i<list.size();i++)
        {
            tmp[i]=list.get(i);
        }
        model.parse(tmp);
    }

    public void execute(){
        model.execute();
    }
    public void stopAutoPilot(){
        model.stopAutoPilot();
    }
    @Override
    public void update(Observable o, Object arg) {
        if(o==model)
        {
            String[] tmp=(String[])arg;
            double x=Double.parseDouble(tmp[0]);
            double y=Double.parseDouble(tmp[1]);
            x=(x-startX.getValue()+offset.getValue());
            x/=offset.getValue();
            y=(y-startY.getValue()+offset.getValue())/offset.getValue();
            airplaneX.setValue(x);
            airplaneY.setValue(y);
            heading.setValue(Double.parseDouble(tmp[2]));
            setChanged();
            notifyObservers();
        }
    }
}
