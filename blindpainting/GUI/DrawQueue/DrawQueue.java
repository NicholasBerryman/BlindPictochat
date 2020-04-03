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
        return queue.poll();
    }
    
    public void clear(){
        queue.clear();
    }
    
    public void pop(PaintCanvas canvas){
        DrawQueueItem item = queue.pop();
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
                int state = (int)item.get("state");
                switch (state){
                    case 0:
                        canvas.startAddPath(item.get("strokeWidth"), item.getColor("line"));
                        break;
                        
                    case 1:
                        canvas.addPath(item.get("x"), item.get("y"));
                        break;
                        
                    case 2:
                        canvas.finishAddPath();
                        break;
                }
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
    
    @Override
    public String toString(){
        return queue.toString();
    }
}

