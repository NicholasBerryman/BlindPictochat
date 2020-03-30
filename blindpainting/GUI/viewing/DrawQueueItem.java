/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.viewing;

import blindpainting.GUI.common.PaintCanvas;
import blindpainting.GUI.common.PaintCanvas.Shape;
import java.io.Serializable;
import java.util.HashMap;
import javafx.scene.paint.Color;

/**
 *
 * @author Nick Berryman
 */
public class DrawQueueItem implements Serializable{
    private Shape shape = Shape.NONE;
    private HashMap<String, Double> data = new HashMap<>();
    private HashMap<String, SerializableColor> colours = new HashMap<>();
    
    public void setShape(PaintCanvas.Shape shape){
        this.shape = shape;
    }
    
    public void setShape(String shape){
        this.shape = Shape.valueOf(shape);
    }
    
    public void setColor(String name, Color colour){
        this.colours.put(name, new SerializableColor(colour));
    }
    
    public Color getColor(String name){
        return this.colours.get(name).get();
    }
    
    public void setData(String name, Double data){
        this.data.put(name, data);
    }
    
    public double get(String name){
        return this.data.get(name);
    }
    
    public Shape getShape(){
        return this.shape;
    }
    
    @Override
    public String toString(){
        return shape.toString()+this.data.toString()+this.colours.toString();
    }
    
}

class SerializableColor implements Serializable{
    private double red;
    private double green;
    private double blue;
    private double alpha;
    public SerializableColor(Color color)
    {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getOpacity();
    }
    public SerializableColor(double red, double green, double blue, double alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    public Color get()
    {
        return new Color(red, green, blue, alpha);
    }
}

