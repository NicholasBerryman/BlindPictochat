/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.common.beginning;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Nick Berryman
 */
public class JoinWindow {
    private final TabPane mainPane = new TabPane();
    private boolean isHost;
    private String name;
    private String role;
    private String ip;
    private final Stage dialog = new Stage();
    
    public JoinWindow(){
        initJoin();
        initHost();
    }
    
    private void initHost(){
        VBox joinBox = new VBox();
        GridPane window = new GridPane();
        Label port = new Label("IP: ");
        TextField ipText = new TextField("127.0.0.1");
        ipText.setEditable(false);
        
        Label username = new Label("Nickname: ");
        TextField userText = new TextField();
        
        Label role = new Label("Role: ");
        ComboBox roleCombo = new ComboBox();
        roleCombo.getItems().addAll(
            "Instructor",
            "Painter",
            "Appreciator"
        );
        roleCombo.getSelectionModel().select(1);
        
        
        Button hostOK = new Button("Host");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().add(hostOK);
        buttons.getChildren().add(cancel);
        
        
        window.setPadding(new Insets(10, 10, 5, 10));
        window.add(port, 0, 0);
        window.add(ipText, 1, 0);
        window.add(username, 0, 1);
        window.add(userText, 1, 1);
        window.add(role, 0, 2);
        window.add(roleCombo, 1, 2);
        joinBox.getChildren().add(window);
        joinBox.getChildren().add(buttons);
        
        cancel.setOnAction(value->{
            System.exit(0);
        });
        hostOK.setOnAction(value->{
            this.name = userText.getText();
            this.role = roleCombo.getSelectionModel().getSelectedItem().toString();
            this.ip = ipText.getText();
            this.isHost = true;
            dialog.close();
        });
        
        Tab hostTab = new Tab("Host a game", joinBox);
        mainPane.getTabs().add(hostTab);
    }
    
    private void initJoin(){
        VBox joinBox = new VBox();
        GridPane window = new GridPane();
        Label ip = new Label("IP: ");
        TextField ipText = new TextField();
        
        Label username = new Label("Nickname: ");
        TextField userText = new TextField();
        
        Label role = new Label("Role: ");
        ComboBox roleCombo = new ComboBox();
        roleCombo.getItems().addAll(
            "Instructor",
            "Painter",
            "Appreciator"
        );
        roleCombo.getSelectionModel().select(1);
        
        
        Button joinOK = new Button("Join");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().add(joinOK);
        buttons.getChildren().add(cancel);
        
        
        window.setPadding(new Insets(10, 10, 5, 10));
        window.add(ip, 0, 0);
        window.add(ipText, 1, 0);
        window.add(username, 0, 1);
        window.add(userText, 1, 1);
        window.add(role, 0, 2);
        window.add(roleCombo, 1, 2);
        joinBox.getChildren().add(window);
        joinBox.getChildren().add(buttons);
        
        cancel.setOnAction(value->{
            System.exit(0);
        });
        joinOK.setOnAction(value->{
            this.name = userText.getText();
            this.role = roleCombo.getSelectionModel().getSelectedItem().toString();
            this.ip = ipText.getText();
            this.isHost = false;
            dialog.close();
        });
        
        Tab joinTab = new Tab("Join a game", joinBox);
        mainPane.getTabs().add(joinTab);
    }
    
    public void showAndWait(){
        dialog.setOnCloseRequest(value->{
            value.consume();
            System.exit(0);
        });
        
        dialog.setTitle("Blind Pictochat - Connect to Game");
        dialog.initStyle(StageStyle.UTILITY);
        
        Scene scene;
        scene = new Scene(mainPane, 320, 165);
        
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public TabPane getMainPane() {
        return mainPane;
    }

    public boolean isHost() {
        return isHost;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getIp() {
        return ip;
    }
}
