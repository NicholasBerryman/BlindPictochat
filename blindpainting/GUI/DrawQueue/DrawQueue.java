/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.DrawQueue;

import blindpainting.GUI.PaintCanvas;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Nick Berryman
 */
public class DrawQueue implements Serializable{
    private final LinkedList<DrawQueueItem> queue = new LinkedList<>();
    
    public int size(){
        return queue.size();
    }
    
    public void push(DrawQueueItem toAdd){
        queue.offer(toAdd);
    }
    
    public DrawQueueItem pop(){
        return queue.pop();
    }
    
    public void clear(){
        queue.clear();
    }
    
    private void run(DrawQueueItem item, PaintCanvas canvas){
        switch(item.getShape()){
            case LINE:
                canvas.addLine(
                        item.get("startX"),
                        item.get("startY"),
                        item.get("endX"),
                        item.get("endY"),
                        item.get("strokeWidth"), 
                        item.getColor("line"));
                break;
            case RECT:
                canvas.addRect(
                        item.get("startX"),
                        item.get("startY"),
                        item.get("width"),
                        item.get("height"),
                        item.get("strokeWidth"), 
                        item.getColor("line"),
                        item.getColor("fill"));
                break;
            case CIRCLE:
                canvas.addCirc(
                        item.get("startX"),
                        item.get("startY"),
                        item.get("radius"),
                        item.get("strokeWidth"), 
                        item.getColor("line"),
                        item.getColor("fill"));
                break;
            case PATH:
                break;
            case ERASE:
                canvas.addErase(
                        item.get("startX"),
                        item.get("startY"),
                        item.get("width"));
                break;
            default:
                break;
        }
    }
    
    public void peek(PaintCanvas canvas){
        run(queue.peek(), canvas);
    }
    
    public void pop(PaintCanvas canvas){
        run(queue.pop(), canvas);
    }
    
    @Override
    public String toString(){
        return queue.toString();
    }
}

