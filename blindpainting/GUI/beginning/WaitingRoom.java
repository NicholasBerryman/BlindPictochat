/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.beginning;

import blindpainting.Network.NetworkCanvas.NetworkCanvasClient;
import blindpainting.Network.NetworkCanvas.NetworkCanvasClient.OtherPlayer;
import blindpainting.Network.NetworkCanvas.NetworkCanvasHost;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Nick Berryman
 */
public class WaitingRoom {
    private final NetworkCanvasClient networkClient;
    private NetworkCanvasHost networkHost;
    private final boolean isHost;
    private final Stage dialog = new Stage();
    private final VBox box = new VBox();
    

    public WaitingRoom(NetworkCanvasClient networkClient) {
        this.networkClient = networkClient;
        this.isHost = false;
        
        initialise();
    }
    
    public WaitingRoom(NetworkCanvasClient networkClient, NetworkCanvasHost networkHost) {
        this.networkClient = networkClient;
        this.networkHost = networkHost;
        this.isHost = true;
        
        initialise();
    }
    
    
    private void initialise(){
        ProgressBar prog = new ProgressBar();
        prog.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        prog.prefWidthProperty().bind(box.widthProperty());
        
        ListView players = new ListView();
        box.getChildren().add(prog);
        box.getChildren().add(players);
        
        if (isHost){
            Button start = new Button("Start Game");
            Button cancel = new Button("Cancel");
            
            start.setOnAction(value -> {
                try {
                    networkHost.sendStart();
                } catch (IOException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            cancel.setOnAction(value -> {
                try {
                    networkClient.sendExit();
                } catch (IOException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            });
            
            HBox buttons = new HBox();
            buttons.setAlignment(Pos.CENTER);
            buttons.setSpacing(10);
            buttons.getChildren().add(start);
            buttons.getChildren().add(cancel);
            box.getChildren().add(buttons);
        }
        
        dialog.setOnCloseRequest(value->{
            value.consume();
            try {
                networkClient.sendExit();
            } catch (IOException ex) {
                Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        });
        
        Thread t = new Thread(() -> {
            while (true){
                if (networkClient.isStarted()){
                    Platform.runLater(() -> {dialog.close();});
                    break;
                }
                if (players.getItems().size() != networkClient.getPlayers().size()){
                    Platform.runLater(() -> {
                        players.getItems().clear();
                        for (OtherPlayer o : networkClient.getPlayers())
                            players.getItems().add(o.toString());
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    public void showAndWait(){
        dialog.setOnCloseRequest(value->{
            value.consume();
            System.exit(0);
        });
        
        dialog.setTitle("Blind Pictochat - Waiting for game to start");
        dialog.initStyle(StageStyle.UTILITY);
        
        Scene scene;
        scene = new Scene(box, 320, 240);
        
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}

