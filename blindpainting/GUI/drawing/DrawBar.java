/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.drawing;

import blindpainting.Words.Words;
import blindpainting.GUI.PaintCanvas;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Nick Berryman
 */
public class DrawBar {
    private final ToggleButton pathButton = new ToggleButton("Draw");
    private final ToggleButton eraseButton = new ToggleButton("Erase");
    private final ToggleButton lineButton = new ToggleButton("Line");
    private final ToggleButton rectButton = new ToggleButton("Rectangle");
    private final ToggleButton circleButton = new ToggleButton("Circle");
    private final ToggleButton[] toolsArr = {pathButton, eraseButton, lineButton, rectButton, circleButton};
    private final Slider widthSlider = new Slider(1, 50, 3);
    private final Label lineColourLabel = new Label("Line Color");
    private final Label fillColourLabel = new Label("Fill Color");
    private final ColorPicker linePicker = new ColorPicker(Color.BLACK);
    private final ColorPicker fillPicker = new ColorPicker(Color.TRANSPARENT);
    private final Label widthLabel = new Label("3.0");
    private final VBox box = new VBox(10);
    private final TextFlow chat = new TextFlow();
    private final TextField messageBar = new TextField();
    private final ScrollPane messagePane = new ScrollPane();
    private final Label turnLabel = new Label("Painter: ");
    private final Button promptButton = new Button("(Press for prompt)");
    
    private final PaintCanvas canvas;
    private String lastChat = "";
    
    
    public DrawBar(PaintCanvas canvas){
        this.canvas = canvas;
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        ToggleGroup tools = new ToggleGroup();
        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setCursor(Cursor.HAND);
            tool.setToggleGroup(tools);
        }
        box.getChildren().addAll(turnLabel, pathButton, eraseButton, lineButton, rectButton, circleButton, lineColourLabel, linePicker, fillColourLabel, fillPicker, widthLabel, widthSlider);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-background-color: #999");
        box.setPrefWidth(100);
        messagePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        messagePane.setContent(chat);
        initialiseActions();
        widthSlider.setValue(3);
        canvas.setStroke(3);
        box.getChildren().addAll(messagePane, messageBar);
        linePicker.setMinHeight(25);
        fillPicker.setMinHeight(25);
        messagePane.setPrefHeight(Integer.MAX_VALUE);
        if ("Instructor".equals(this.canvas.getNetCanvas().getRole()))
            box.getChildren().add(promptButton);
    }
    
    private void initialiseActions(){
        pathButton.setOnAction(value -> {canvas.setShape(PaintCanvas.Shape.PATH);});
        eraseButton.setOnAction(value -> {canvas.setShape(PaintCanvas.Shape.ERASE);});
        lineButton.setOnAction(value -> {canvas.setShape(PaintCanvas.Shape.LINE);});
        rectButton.setOnAction(value -> {canvas.setShape(PaintCanvas.Shape.RECT);});
        circleButton.setOnAction(value -> {canvas.setShape(PaintCanvas.Shape.CIRCLE);});
        linePicker.setOnAction(value -> {canvas.setLineColour(linePicker.getValue());});
        fillPicker.setOnAction(value -> {canvas.setFillColour(fillPicker.getValue());});
        
        widthSlider.valueProperty().addListener(value -> {
            widthLabel.setText(String.format("%.1f", widthSlider.getValue()));
            canvas.setStroke(widthSlider.getValue());
        });
        
        messageBar.setOnAction(value -> {
            try {
                canvas.getNetCanvas().sendMessage(messageBar.getText());
                messageBar.setText("");
            } catch (IOException ex) {
                Logger.getLogger(DrawBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        Thread t = new Thread(() -> {
            while (true){
                String chatString = canvas.getNetCanvas().getChat();
                String[] chatList = chatString.split("\\|");
                ArrayList<Text> texts = new ArrayList<>();
                int i = 0;
                for (String c : chatList){
                    Text text = new Text(c);
                    if (i%2 == 0) text.setFont(Font.font("Arial", FontWeight.LIGHT, FontPosture.REGULAR, 12));
                    else text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 12));
                    texts.add(text);
                    i++;
                }
                Platform.runLater(() -> {
                    if (!chatString.equals(lastChat)){
                        chat.getChildren().clear();
                        chat.getChildren().addAll(texts);
                        messagePane.setVvalue(1);
                        lastChat = chatString;
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DrawBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.setDaemon(true);
        t.start();
        
        Thread t2 = new Thread(() -> {
            while (true){
                boolean myTurn = canvas.getNetCanvas().isMyTurn();
                String turnName = canvas.getNetCanvas().getTurnName();
                Platform.runLater(() -> {
                    turnLabel.setText("Painter: "+turnName);
                    if ("Painter".equals(canvas.getNetCanvas().getRole())){
                        if (myTurn) box.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                        else box.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DrawBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t2.setDaemon(true);
        t2.start();
        
        promptButton.setOnAction(value -> {
            promptButton.setText(Words.generatePrompt()+" (Press for new)");
        });
    }
    
    public VBox get(){
        return box;
    }
    
}


