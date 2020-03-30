/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting;

import blindpainting.GUI.common.beginning.JoinWindow;
import blindpainting.GUI.common.beginning.WaitingRoom;
import blindpainting.GUI.common.networkcanvas.NetworkCanvasClient;
import blindpainting.GUI.common.networkcanvas.NetworkCanvasHost;
import blindpainting.GUI.drawing.DrawWindow;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author Nick Berryman
 */
public class BlindPictochat extends Application {
    
    @Override
    public void start(Stage primaryStage){
        boolean connected = false;
        
        while (!connected){
            JoinWindow join = new JoinWindow();
            join.showAndWait();

            DrawWindow window = new DrawWindow();

            NetworkCanvasClient client = new NetworkCanvasClient(window.getCanvas());
            try {
                NetworkCanvasHost host = null;
                if (join.isHost()){
                    host = new NetworkCanvasHost();
                    host.startHost(32586);
                }
                client.connect(join.getIp(), 32586, join.getName(), join.getRole());
                window.getCanvas().setNetCanvas(client);
                connected = true;
                if (!join.isHost()) new WaitingRoom(client).showAndWait();
                else new WaitingRoom(client, host).showAndWait();
                window.start(primaryStage);
            } catch (IOException ex) {
                primaryStage.close();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(null);
                alert.setContentText("Connection error!\nPlease try again.");
                alert.showAndWait();
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

