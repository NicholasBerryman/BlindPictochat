/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.common;

import blindpainting.GUI.common.networkcanvas.NetworkCanvasClient;
import blindpainting.GUI.viewing.DrawQueue;
import blindpainting.GUI.viewing.DrawQueueItem;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Nick Berryman
 */
public class PaintCanvas {
    private final Canvas canvas;
    private final Pane overlay;
    private final Pane canvasPane = new Pane();
    private final GraphicsContext gc;
    private Shape currentShape;
    private Action currentAction;
    
    private Color paintColour;
    private Color fillColour;
    private double paintStroke;
    
    private final Line line = new Line();
    private final Rectangle rect = new Rectangle();
    private final Circle circ = new Circle();
    
    private final DrawQueue queue = new DrawQueue();
    private final DrawQueue lastStroke = new DrawQueue();
    
    private NetworkCanvasClient netCanvas;
    
    
    public PaintCanvas(){
        canvasPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        canvas = new Canvas(1080, 790);
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.widthProperty());
        overlay = new Pane();
        overlay.getChildren().add(line);
        overlay.getChildren().add(rect);
        overlay.getChildren().add(circ);
        canvasPane.getChildren().addAll(overlay, canvas);
        gc = canvas.getGraphicsContext2D();
        
        currentShape = Shape.NONE;
        currentAction = Action.NONE;
        paintColour = Color.BLACK;
        fillColour = Color.TRANSPARENT;
        paintStroke = 1;
    }
    
    public void setNetCanvas(NetworkCanvasClient netCanvas){
        this.netCanvas = netCanvas;
    }

    public NetworkCanvasClient getNetCanvas() {
        return netCanvas;
    }
    
    private void sendLastStroke(){
        if (lastStroke.size() > 0 && netCanvas != null)
        {
            try {
                netCanvas.sendStroke(lastStroke);
            } catch (IOException ex) {
                Logger.getLogger(PaintCanvas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Pane get(){
        return canvasPane;
    }
    
    public DrawQueue getDrawQueue(){
        return queue;
    }
    
    public void showDrawQueue(DrawQueue queue){
        while (queue.size() > 0) queue.pop(this);
    }
    
    public void showDrawQueueItem(DrawQueueItem item){
        DrawQueue d = new DrawQueue();
        d.push(item);
        showDrawQueue(d);
    }
    
    public void setShape(Shape toDraw){
        this.currentShape = toDraw;
    }
    
    public void setStroke(double stroke){
        this.paintStroke = stroke;
    }
    
    public void setLineColour(Color colour){
        this.paintColour = colour;
    }
    
    public void setFillColour(Color colour){
        this.fillColour = colour;
    }
    
    public void initialise(){
        canvas.setOnMousePressed(e->{
            if (netCanvas.isMyTurn() || !"Painter".equals(netCanvas.getRole())){
                lastStroke.clear();
                overlay.toFront();
                currentAction = Action.DRAW;
                switch (currentShape){
                    case PATH:
                        gc.setStroke(paintColour);
                        gc.setLineWidth(paintStroke);
                        gc.beginPath();
                        gc.lineTo(e.getX(), e.getY());
                        DrawQueueItem item = new DrawQueueItem();
                        item.setShape(Shape.PATH);
                        item.setColor("line", paintColour);
                        item.setData("strokeWidth", paintStroke);
                        item.setData("state", 0.0);
                        lastStroke.push(item);
                        queue.push(item);
                        item = new DrawQueueItem();
                        item.setShape(Shape.PATH);
                        item.setData("x", e.getX()/canvas.getWidth());
                        item.setData("y", e.getY()/canvas.getHeight());
                        item.setData("state", 1.0);
                        lastStroke.push(item);
                        queue.push(item);
                        break;

                    case ERASE:
                        gc.setLineWidth(paintStroke);
                        double lineWidth = gc.getLineWidth();
                        gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
                        break;

                    case LINE:
                        gc.setStroke(paintColour);
                        gc.setLineWidth(paintStroke);
                        line.setStroke(paintColour);
                        line.setStrokeWidth(paintStroke);
                        line.setStartX(e.getX());
                        line.setStartY(e.getY());
                        line.setEndX(e.getX());
                        line.setEndY(e.getY());
                        line.setVisible(true);
                        break;

                    case RECT:
                        gc.setStroke(paintColour);
                        gc.setLineWidth(paintStroke);
                        gc.setFill(fillColour);
                        rect.setStroke(paintColour);
                        rect.setFill(fillColour);
                        rect.setStrokeWidth(paintStroke);
                        rect.setX(e.getX());                
                        rect.setY(e.getY());
                        rect.setTranslateX(0);
                        rect.setTranslateY(0);
                        rect.setWidth(0);
                        rect.setHeight(0);
                        rect.setVisible(true);
                        break;

                    case CIRCLE:
                        gc.setStroke(paintColour);
                        gc.setLineWidth(paintStroke);
                        gc.setFill(fillColour);
                        circ.setStroke(paintColour);
                        circ.setFill(fillColour);
                        circ.setStrokeWidth(paintStroke);
                        circ.setCenterX(e.getX());
                        circ.setCenterY(e.getY());
                        circ.setRadius(0);
                        circ.setVisible(true);
                        break;
                }
            }
        });
        
        canvas.setOnMouseDragged(e->{
            if (netCanvas.isMyTurn() || !"Painter".equals(netCanvas.getRole())){
                DrawQueueItem item;
                switch (currentShape){
                    case PATH:
                        gc.lineTo(e.getX(), e.getY());
                        gc.stroke();
                        item = new DrawQueueItem();
                        item.setShape(Shape.PATH);
                        item.setData("x", e.getX()/canvas.getWidth());
                        item.setData("y", e.getY()/canvas.getHeight());
                        item.setData("state", 1.0);
                        queue.push(item);
                        lastStroke.push(item);
                        break;

                    case ERASE:
                        double lineWidth = gc.getLineWidth();
                        gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
                        item = new DrawQueueItem();
                        item.setShape(Shape.ERASE);
                        item.setData("startX", e.getX()/canvas.getWidth());
                        item.setData("startY", e.getY()/canvas.getHeight());
                        item.setData("width", lineWidth);
                        queue.push(item);
                        lastStroke.push(item);
                        break;

                    case LINE:
                        line.setEndX(e.getX());
                        line.setEndY(e.getY());
                        break;

                    case RECT:
                        rect.setWidth(Math.abs((e.getX() - rect.getX())));
                        rect.setHeight(Math.abs((e.getY() - rect.getY())));
                        if (e.getX() - rect.getX() < 0) rect.setTranslateX(e.getX() - rect.getX());
                        if (e.getY() - rect.getY() < 0) rect.setTranslateY(e.getY() - rect.getY());
                        break;

                    case CIRCLE:
                        circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);
                        break;
                }
            }
        });
        
        canvas.setOnMouseReleased(e->{
            if (netCanvas.isMyTurn() || !"Painter".equals(netCanvas.getRole())){
                DrawQueueItem item;
                overlay.toBack();
                currentAction = Action.NONE;
                switch (currentShape){
                    case PATH:
                        gc.lineTo(e.getX(), e.getY());
                        gc.stroke();
                        gc.closePath();
                        item = new DrawQueueItem();
                        item.setShape(Shape.PATH);
                        item.setData("x", e.getX()/canvas.getWidth());
                        item.setData("y", e.getY()/canvas.getHeight());
                        item.setData("state", 1.0);
                        queue.push(item);
                        lastStroke.push(item);
                        item = new DrawQueueItem();
                        item.setShape(Shape.PATH);
                        item.setData("state", 2.0);
                        queue.push(item);
                        lastStroke.push(item);
                        break;

                    case ERASE:
                        double lineWidth = gc.getLineWidth();
                        gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
                        break;

                    case LINE:
                        line.setEndX(e.getX());
                        line.setEndY(e.getY());
                        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                        line.setVisible(false);
                        item = new DrawQueueItem();
                        item.setShape(Shape.LINE);
                        item.setColor("line", paintColour);
                        item.setData("startX", line.getStartX()/canvas.getWidth());
                        item.setData("startY", line.getStartY()/canvas.getHeight());
                        item.setData("endX", line.getEndX()/canvas.getWidth());
                        item.setData("endY", line.getEndY()/canvas.getHeight());
                        item.setData("strokeWidth",paintStroke);
                        queue.push(item);
                        lastStroke.push(item);
                        break;

                    case RECT:
                        rect.setWidth(Math.abs((e.getX() - rect.getX())));
                        rect.setHeight(Math.abs((e.getY() - rect.getY())));
                        if (e.getX() - rect.getX() < 0) rect.setTranslateX(e.getX() - rect.getX());
                        if (e.getY() - rect.getY() < 0) rect.setTranslateY(e.getY() - rect.getY());
                        item = new DrawQueueItem();
                        item.setShape(Shape.RECT);
                        item.setColor("line", paintColour);
                        item.setColor("fill", fillColour);
                        item.setData("startX", (rect.getX()+rect.getTranslateX())/canvas.getWidth());
                        item.setData("startY", (rect.getY()+rect.getTranslateY())/canvas.getHeight());
                        item.setData("width", rect.getWidth()/canvas.getWidth());
                        item.setData("height", rect.getHeight()/canvas.getHeight());
                        item.setData("strokeWidth",paintStroke);
                        queue.push(item);
                        lastStroke.push(item);

                        gc.fillRect(rect.getX()+rect.getTranslateX(), rect.getY()+rect.getTranslateY(), rect.getWidth(), rect.getHeight());
                        gc.strokeRect(rect.getX()+rect.getTranslateX(), rect.getY()+rect.getTranslateY(), rect.getWidth(), rect.getHeight());
                        rect.setVisible(false);
                        break;

                    case CIRCLE:
                        circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                        gc.fillOval(circ.getCenterX()-circ.getRadius(), circ.getCenterY()-circ.getRadius(), circ.getRadius()*2, circ.getRadius()*2);
                        gc.strokeOval(circ.getCenterX()-circ.getRadius(), circ.getCenterY()-circ.getRadius(), circ.getRadius()*2, circ.getRadius()*2);
                        circ.setVisible(false);
                        item = new DrawQueueItem();
                        item.setShape(Shape.CIRCLE);
                        item.setColor("line", paintColour);
                        item.setColor("fill", fillColour);
                        item.setData("startX", (circ.getCenterX()-circ.getRadius())/canvas.getWidth());
                        item.setData("startY", (circ.getCenterY()-circ.getRadius())/canvas.getHeight());
                        item.setData("radius", circ.getRadius()/canvas.getWidth());
                        item.setData("strokeWidth",paintStroke);
                        queue.push(item);
                        lastStroke.push(item);
                        break;
                }
                sendLastStroke();
            }
        });
    }
    
    public void addLine(double startX, double startY, double endX, double endY, double strokeWidth, Color colour){
        startX *= canvas.getWidth();
        endX *= canvas.getWidth();
        startY *= canvas.getHeight();
        endY *= canvas.getHeight();
        gc.setStroke(colour);
        gc.setLineWidth(strokeWidth);
        gc.strokeLine(startX, startY, endX, endY);
    }
    
    public void addRect(double startX, double startY, double width, double height, double strokeWidth, Color lineColour, Color fillColour){
        startX *= canvas.getWidth();
        width *= canvas.getWidth();
        startY *= canvas.getHeight();
        height *= canvas.getHeight();
        gc.setStroke(lineColour);
        gc.setFill(fillColour);
        gc.setLineWidth(strokeWidth);
        gc.fillRect(startX, startY, width, height);
        gc.strokeRect(startX, startY, width, height);
    }
    
    public void addCirc(double startX, double startY, double radius, double strokeWidth, Color lineColour, Color fillColour){
        startX *= canvas.getWidth();
        radius *= canvas.getWidth();
        startY *= canvas.getHeight();
        gc.setStroke(lineColour);
        gc.setFill(fillColour);
        gc.setLineWidth(strokeWidth);
        gc.strokeOval(startX, startY, radius*2, radius*2);
        gc.fillOval(startX, startY, radius*2, radius*2);
    }
    
    public void addErase(double startX, double startY, double width){
        startX *= canvas.getWidth();
        startY *= canvas.getHeight();
        gc.clearRect(startX-width/2.0, startY-width/2.0, width, width);
    }
    
    public void startAddPath(double strokeWidth, Color colour){
        gc.setStroke(colour);
        gc.setLineWidth(strokeWidth);
        gc.beginPath();
    }
    
    public void addPath(double x, double y){
        x *= canvas.getWidth();
        y *= canvas.getHeight();
        gc.lineTo(x, y);
        gc.stroke();
    }
    
    public void finishAddPath(){
        gc.closePath();
    }
    
    public enum Shape{CIRCLE, LINE, NONE, PATH, RECT, ERASE};
    public enum Action{NONE, DRAW}
}
