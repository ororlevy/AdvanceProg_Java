package GUI;


import Commands.Command;
import Commands.ConnectCommand;
import Model.ClientSim;
import Model.Interpter;
import flight_sim.ParsherAutoPilot;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
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
    private ClientSim cls=new ClientSim();
    private Interpter interpter=new Interpter();



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
            if(!stage.isShowing())
                stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void Submit(){
        cls.Connect(ip.getText(),Integer.parseInt(port.getText()));
        ip.clear();
        port.clear();
        Stage stage = (Stage) submit.getScene().getWindow();
        stage.close();
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
                manual.setSelected(false);
            }
            interpter.execute();
        }
        else if(s.equals("manual"))
        {
            if(auto.isSelected())
            {
                auto.setSelected(false);
                ParsherAutoPilot.stop=true;
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.getPath().equals("/C:/Users/ororl/GitHub/PTM1/production/GitHub/GUI/Flight.fxml")) {
            throttle.valueProperty().addListener((observable, oldValue, newValue) -> {
                String[] data={"set /controls/engines/current-engine/throttle "+newValue.doubleValue()};
                cls.Send(data);
            });
            rudder.valueProperty().addListener((observable, oldValue, newValue) -> {
                String[] data={"set /controls/flight/rudder "+newValue.doubleValue()};
                cls.Send(data);
            });
        }
    }
}
