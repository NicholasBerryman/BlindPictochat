/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.drawing;

import blindpainting.GUI.PaintCanvas;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Nick Berryman
 */
public class DrawWindow {
    PaintCanvas canvas = new PaintCanvas();
    
    public void start(Stage primaryStage){
        VBox box = new VBox();
        canvas.initialise();
        DrawBar drawBar = new DrawBar(canvas);
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.HORIZONTAL);
        pane.getItems().addAll(drawBar.get(), canvas.get());
        pane.setDividerPosition(0, 0.15);
        
        MenuBar menu = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        menu.getMenus().add(fileMenu);
        menu.getMenus().add(editMenu);
        MenuItem clearItem = new MenuItem("Clear");
        KeyCombination clearKey = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN);
        clearItem.acceleratorProperty().set(clearKey);
        editMenu.getItems().add(clearItem);
        box.getChildren().add(menu);
        box.getChildren().add(pane);
        
        
        
        clearItem.setOnAction(value -> {
            try {
                canvas.getNetCanvas().sendClear();
            } catch (IOException ex) {
                Logger.getLogger(DrawWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        
        
        
        
        
        
        Scene scene = new Scene(box, 1200, 800);
        
        primaryStage.setTitle("Blind Pictochat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public PaintCanvas getCanvas(){
        return canvas;
    }
}

