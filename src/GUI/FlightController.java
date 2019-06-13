package GUI;



import Model.Interpter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class FlightController implements Initializable, Observer {


    @FXML
    private Canvas airplane;
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
    private MapDisplayer map;
    @FXML
    private RadioButton manual;
    @FXML
    private Circle border;
    @FXML
    private Circle Joystick;
    //private Interpter interpter;
    private static int Who;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    private ViewModel viewModel;
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    public DoubleProperty startX;
    public DoubleProperty startY;
    public DoubleProperty offset;
    public DoubleProperty heading;
    public double lastX;
    public double lastY;
    public int mapData[][];
    private Image plane[];


    public void LoadDate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./"));
        int v = fileChooser.showOpenDialog(null);
        ArrayList<String> list = new ArrayList<>();
        if (v == JFileChooser.APPROVE_OPTION) {
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            ArrayList<String[]> numbers = new ArrayList<>();
            try {

                br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String[] start=br.readLine().split(cvsSplitBy);
                startX.setValue(Double.parseDouble(start[0]));
                startY.setValue(Double.parseDouble(start[1]));
                start=br.readLine().split(cvsSplitBy);
                offset.setValue(Double.parseDouble(start[0]));
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
                this.viewModel.setData(mapData);
                this.drawAirplane();
                map.setMapData(mapData);

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
                    //list.add(s.nextLine());
                    TextArea.appendText(s.nextLine());
                    TextArea.appendText("\n");

                }
                /*
                String[] stringlist=new String[list.size()];
                for(int i=0;i<list.size();i++)
                {
                    stringlist[i]=list.get(i);
                    TextArea.appendText(list.get(i));
                    TextArea.appendText("\n");
                }

                 */
                viewModel.parse();
                //interpter.interpet(stringlist);
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
            FlightController fc=fxmlLoader.getController();
            fc.viewModel=this.viewModel;
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
        this.viewModel.ip.bindBidirectional(ip.textProperty());
        this.viewModel.port.bindBidirectional(port.textProperty());
        if(this.Who==0) {
            viewModel.connect();

            Stage stage = (Stage) submit.getScene().getWindow();

            stage.close();
        }
        if(this.Who==1)
        {

        }
        ip.clear();
        port.clear();
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
                //manual.fire();
                manual.setSelected(false);
                auto.setSelected(true);

            }
            viewModel.execute();
           // interpter.execute();
        }
        else if(s.equals("manual"))
        {
            if(auto.isSelected())
            {
                //auto.fire();
                auto.setSelected(false);
                manual.setSelected(true);
                //interpter.stop();
                viewModel.stopAutoPilot();
            }

        }
    }

    public void drawAirplane(){
        if(airplaneX.getValue()!=null&&airplaneY.getValue()!=null)
        {

            double H = airplane.getHeight();
            double W = airplane.getWidth();
            double h = H / mapData.length;
            double w = W / mapData[0].length;
            GraphicsContext gc = airplane.getGraphicsContext2D();
            lastX=airplaneX.getValue();
            lastY=airplaneY.getValue()*-1;
            gc.clearRect(0,0,W,H);

            if(heading.getValue()>=0&&heading.getValue()<45)
                gc.drawImage(plane[0], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=45&&heading.getValue()<90)
                gc.drawImage(plane[1], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=90&&heading.getValue()<135)
                gc.drawImage(plane[2], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=135&&heading.getValue()<180)
                gc.drawImage(plane[3], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=180&&heading.getValue()<225)
                gc.drawImage(plane[4], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=225&&heading.getValue()<270)
                gc.drawImage(plane[5], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=270&&heading.getValue()<315)
                gc.drawImage(plane[6], w*lastX, lastY*h, 25, 25);
            if(heading.getValue()>=315)
                gc.drawImage(plane[7], w*lastX, lastY*h, 25, 25);


            /*
            new Thread(()->{
                while(true) {
                    double H = airplane.getHeight();
                    double W = airplane.getWidth();
                    double h = H / mapData.length;
                    double w = W / mapData[0].length;
                    GraphicsContext gc = airplane.getGraphicsContext2D();
                    double tmpX=airplaneX.getValue();
                    double tmpY=airplaneY.getValue()*-1;
                    gc.drawImage(plane, w*tmpX, tmpY*h, 25, 25);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gc.clearRect(0,0,W,H);
                }
        }).start();

             */
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
                            aileron.setValue(nirmulX(newTranslateX));
                            elevator.setValue(nirmulY(newTranslateY));
                            viewModel.setJoystick();
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
    public void setViewModel(ViewModel viewModel){
        this.viewModel=viewModel;
        throttle.valueProperty().bindBidirectional(viewModel.throttle);
        rudder.valueProperty().bindBidirectional(viewModel.rudder);
        aileron=new SimpleDoubleProperty();
        elevator=new SimpleDoubleProperty();
        aileron.bindBidirectional(viewModel.aileron);
        elevator.bindBidirectional(viewModel.elevator);
        airplaneX=new SimpleDoubleProperty();
        airplaneY=new SimpleDoubleProperty();
        startX=new SimpleDoubleProperty();
        startY=new SimpleDoubleProperty();
        airplaneX.bindBidirectional(viewModel.airplaneX);
        airplaneY.bindBidirectional(viewModel.airplaneY);
        startX.bindBidirectional(viewModel.startX);
        startY.bindBidirectional(viewModel.startY);
        offset=new SimpleDoubleProperty();
        offset.bindBidirectional(viewModel.offset);
        viewModel.script.bindBidirectional(TextArea.textProperty());
        heading=new SimpleDoubleProperty();
        heading.bindBidirectional(viewModel.heading);
        plane=new Image[8];
        try {
            plane[0]=new Image(new FileInputStream("./PTM1/resources/plane0.png"));
            plane[1]=new Image(new FileInputStream("./PTM1/resources/plane45.png"));
            plane[2]=new Image(new FileInputStream("./PTM1/resources/plane90.png"));
            plane[3]=new Image(new FileInputStream("./PTM1/resources/plane135.png"));
            plane[4]=new Image(new FileInputStream("./PTM1/resources/plane180.png"));
            plane[5]=new Image(new FileInputStream("./PTM1/resources/plane225.png"));
            plane[6]=new Image(new FileInputStream("./PTM1/resources/plane270.png"));
            plane[7]=new Image(new FileInputStream("./PTM1/resources/plane315.png"));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.getPath().equals("/C:/Users/ororl/GitHub/PTM1/production/GitHub/GUI/Flight.fxml")) {
            throttle.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(manual.isSelected())
                    viewModel.setThrottle();
            });

            rudder.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(manual.isSelected())
                    viewModel.setRudder();
            });
            Joystick.setOnMousePressed(circleOnMousePressedEventHandler);
            Joystick.setOnMouseDragged(circleOnMouseDraggedEventHandler);
            Joystick.setOnMouseReleased(circleOnMouseReleasedEventHandler);

            Who=-1;
            //this.viewModel=new ViewModel();



        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o==viewModel)
        {
            drawAirplane();
        }
    }
}
