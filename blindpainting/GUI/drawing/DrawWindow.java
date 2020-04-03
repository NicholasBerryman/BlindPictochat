/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.drawing;

import blindpainting.GUI.PaintCanvas;
import blindpainting.Words.Words;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
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
        Menu wordSetMenu = new Menu("Word Set");
        menu.getMenus().add(fileMenu);
        menu.getMenus().add(editMenu);
        menu.getMenus().add(wordSetMenu);
        MenuItem clearItem = new MenuItem("Clear");
        KeyCombination clearKey = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN);
        CheckMenuItem gridItem = new CheckMenuItem("Enable Grid");
        gridItem.setSelected(true);
        KeyCombination gridKey = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN);
        clearItem.acceleratorProperty().set(clearKey);
        gridItem.acceleratorProperty().set(gridKey);
        editMenu.getItems().add(clearItem);
        editMenu.getItems().add(gridItem);
        
        RadioMenuItem easyWords = new RadioMenuItem("Easy");
        RadioMenuItem reallyHardWords = new RadioMenuItem("Really Hard");
        RadioMenuItem idiomWords = new RadioMenuItem("Idioms");
        ToggleGroup wordSetGroup = new ToggleGroup();
        wordSetGroup.getToggles().add(easyWords);
        wordSetGroup.getToggles().add(reallyHardWords);
        wordSetGroup.getToggles().add(idiomWords);
        wordSetMenu.getItems().add(easyWords);
        wordSetMenu.getItems().add(reallyHardWords);
        wordSetMenu.getItems().add(idiomWords);
        easyWords.setSelected(true);
        
        box.getChildren().add(menu);
        box.getChildren().add(pane);
        
        clearItem.setOnAction(value -> {
            try {
                canvas.getNetCanvas().sendClear();
            } catch (IOException ex) {
                Logger.getLogger(DrawWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        gridItem.setOnAction(value -> {
            canvas.drawGrid(gridItem.isSelected());
        });
        
        easyWords.setOnAction(value -> {
            drawBar.setWordSet(Words.WordSet.easy);
        });
        
        reallyHardWords.setOnAction(value -> {
            drawBar.setWordSet(Words.WordSet.reallyHard);
        });
        
        idiomWords.setOnAction(value -> {
            drawBar.setWordSet(Words.WordSet.idioms);
        });
        
        
        
        Scene scene = new Scene(box, 1200, 800);
        
        primaryStage.setTitle("Blind Pictochat");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        canvas.drawGrid(true);
    }
    
    public PaintCanvas getCanvas(){
        return canvas;
    }
}

