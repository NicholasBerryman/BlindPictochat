/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.drawing;

import blindpainting.GUI.common.PaintCanvas;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

/**
 *
 * @author Nick Berryman
 */
public class DrawWindow {
    PaintCanvas canvas = new PaintCanvas();
    
    public void start(Stage primaryStage){
        canvas.initialise();
        DrawBar drawBar = new DrawBar(canvas);
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.HORIZONTAL);
        pane.getItems().addAll(drawBar.get(), canvas.get());
        pane.setDividerPosition(0, 0.15);
        
        
        Scene scene = new Scene(pane, 1200, 800);
        
        primaryStage.setTitle("Blind Pictochat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public PaintCanvas getCanvas(){
        return canvas;
    }
}

