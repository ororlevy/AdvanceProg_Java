package GUI;


import Commands.CommandExpression;
import Commands.DisconnectCommand;
import Model.ClientSim;
import Model.Interpter;
import flight_sim.ParserAutoPilot;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class FlightController implements Initializable {

    @FXML
    private  TextArea TextArea;
    @FXML
    private TextField port;
    @FXML
    private TextField ip;
    @FXML
    private Button submit;
    private Stage stage=new Stage();
    @FXML
    private Slider throttle;
    @FXML
    private Slider rudder;
    @FXML
    private RadioButton auto;
    @FXML
    private Canvas map;
    @FXML
    private RadioButton manual;
    @FXML
    private Circle border;
    @FXML
    private Circle Joystick;
    private ClientSim cls=new ClientSim();
    private Interpter interpter=new Interpter();;
    private static int Who;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;


    public void LoadDate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        int v = fileChooser.showOpenDialog(null);
        ArrayList<String> list = new ArrayList<>();
        if (v == JFileChooser.APPROVE_OPTION) {
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            int[][] mapData;
            ArrayList<String[]> numbers = new ArrayList<>();
            try {

                br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                while ((line = br.readLine()) != null) {
                    numbers.add(line.split(cvsSplitBy));
                }
                mapData = new int[numbers.size()][];
                for (int i = 0; i < numbers.size(); i++) {
                    mapData[i] = new int[numbers.get(i).length];
                    for (int j = 0; j < numbers.get(i).length; j++) {
                        String tmp=numbers.get(i)[j];
                        mapData[i][j] = Integer.parseInt(tmp);
                    }
                }
                //map.setMapData(mapData);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






    public  void LoadText(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        int v=fileChooser.showOpenDialog(null);
        ArrayList<String> list=new ArrayList<>();
        if(v==JFileChooser.APPROVE_OPTION) {
            try {
                Scanner s=new Scanner(new BufferedReader(new FileReader(fileChooser.getSelectedFile())));
                while(s.hasNextLine()) {
                    list.add(s.nextLine());

                }
                String[] stringlist=new String[list.size()];
                for(int i=0;i<list.size();i++)
                {
                    stringlist[i]=list.get(i);
                    TextArea.appendText(list.get(i));
                    TextArea.appendText("\n");
                }
                interpter.interpet(stringlist);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void Connect(){
        Parent root = null;
        try {
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("Popup.fxml"));
            root = fxmlLoader.load();
            stage.setTitle("Popup");
            stage.setScene(new Scene(root));
            if(!stage.isShowing()) {
                stage.show();
                this.Who=0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void Calc(){
        Parent root = null;
        try {
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("Popup.fxml"));
            root = fxmlLoader.load();
            stage.setTitle("Popup");
            stage.setScene(new Scene(root));
            if(!stage.isShowing()) {
                this.Who=1;
                stage.show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Submit(){
        if(this.Who==0) {
            cls.Connect(ip.getText(), Integer.parseInt(port.getText()));
            ip.clear();
            port.clear();
            Stage stage = (Stage) submit.getScene().getWindow();
            stage.close();
        }
        if(this.Who==1)
        {

        }
    }

    public void AutoPilot(){
        Select("auto");
    }
    public void Manual()
    {
        Select("manual");
    }
    public void Select(String s){
        if(s.equals("auto"))
        {
            if(manual.isSelected())
            {
                manual.fire();
            }
            interpter.execute();
        }
        else if(s.equals("manual"))
        {
            if(auto.isSelected())
            {
                auto.fire();
                CommandExpression c=new CommandExpression(new DisconnectCommand());
                c.calculate();
            }

        }
    }

    EventHandler<MouseEvent> circleOnMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    orgTranslateX = ((Circle)(t.getSource())).getTranslateX();
                    orgTranslateY = ((Circle)(t.getSource())).getTranslateY();
                }
            };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    double offsetX = t.getSceneX() - orgSceneX;
                    double offsetY = t.getSceneY() - orgSceneY;
                    double newTranslateX = orgTranslateX + offsetX;
                    double newTranslateY = orgTranslateY + offsetY;
                    if(isInCircle(newTranslateX,newTranslateY)) {
                        ((Circle) (t.getSource())).setTranslateX(newTranslateX);
                        ((Circle) (t.getSource())).setTranslateY(newTranslateY);
                        if(manual.isSelected()) {
                            String[] data = {
                                    "set /controls/flight/aileron " + nirmulX(newTranslateX),
                                    "set /controls/flight/elevator " + nirmulY(newTranslateY),
                            };
                            cls.Send(data);
                        }
                    }
                }
            };
    private double nirmulX(double num){
        double max=(border.getRadius()-Joystick.getRadius())+border.getCenterX();
        double min=border.getCenterX()-(border.getRadius()-Joystick.getRadius());
        double new_max=1;
        double new_min=-1;
        return (((num-min)/(max-min)*(new_max-new_min)+new_min));
    }
    private double nirmulY(double num){
        double min=(border.getRadius()-Joystick.getRadius())+border.getCenterY();
        double max=border.getCenterY()-(border.getRadius()-Joystick.getRadius());
        double new_max=1;
        double new_min=-1;
        return (((num-min)/(max-min)*(new_max-new_min)+new_min));
    }
    private  boolean isInCircle(double x,double y){
        return (Math.pow((x-border.getCenterX()),2)+Math.pow((y-border.getCenterY()),2))<=Math.pow(border.getRadius()-Joystick.getRadius(),2);
    }
    EventHandler<MouseEvent> circleOnMouseReleasedEventHandler =
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {

                    ((Circle)(t.getSource())).setTranslateX(orgTranslateX);
                    ((Circle)(t.getSource())).setTranslateY(orgTranslateY);
                }
            };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.getPath().equals("/C:/Users/ororl/GitHub/PTM1/production/GitHub/GUI/Flight.fxml")) {
            throttle.valueProperty().addListener((observable, oldValue, newValue) -> {
                String[] data={"set /controls/engines/current-engine/throttle "+newValue.doubleValue()};
                if(manual.isSelected())
                cls.Send(data);
            });
            rudder.valueProperty().addListener((observable, oldValue, newValue) -> {
                String[] data={"set /controls/flight/rudder "+newValue.doubleValue()};
                if(manual.isSelected())
                cls.Send(data);
            });
            Joystick.setOnMousePressed(circleOnMousePressedEventHandler);
            Joystick.setOnMouseDragged(circleOnMouseDraggedEventHandler);
            Joystick.setOnMouseReleased(circleOnMouseReleasedEventHandler);

            Who=-1;
        }
    }



}
