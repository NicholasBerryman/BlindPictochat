/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.common.networkcanvas;

import blindpainting.GUI.common.PaintCanvas;
import blindpainting.GUI.common.networkcanvas.NetworkCanvasClient.OtherPlayer;
import blindpainting.GUI.viewing.DrawQueue;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author Nick Berryman
 */
public class NetworkCanvasClient {
    private Socket client;
    ArrayList<OtherPlayer> otherPlayers = new ArrayList<>(); 
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final PaintCanvas canvas;
    private String role;
    private String name;
    String turnName;
    Integer id;
    
    boolean started;
    boolean myTurn = false;
    String chat = "";
    
    public NetworkCanvasClient(PaintCanvas canvas){
        this.canvas = canvas;
    }
    
    public void connect(String ip, int port, String name, String role) throws IOException{
        this.role = role;
        this.name = name;
        client = new Socket(ip, port);
        in = new ObjectInputStream(client.getInputStream());
        out = new ObjectOutputStream(client.getOutputStream());
        out.flush();
        out.writeObject(role);
        out.writeObject(name);
        Thread t = new Thread(new NetworkCanvasClientListener(this));
        t.setDaemon(true);
        t.start();
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public String getTurnName() {
        return turnName;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
    
    Object read() throws IOException, ClassNotFoundException{
        boolean block = true;
        while (block){
            try{
                return in.readObject();
            }
            catch(EOFException e){}
            catch(SocketException e2){
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Lost");
                    alert.setHeaderText(null);
                    alert.setContentText("It looks like someone quit!\nPlease restart the game if you want to play again.");
                    alert.showAndWait();
                    
                    System.exit(0);
                });
                while (true) {;}
            }
        }
        return null;
    }
    
    public void sendMessage(String toSend) throws IOException{
        out.writeObject("message");
        out.writeObject(toSend);
        out.flush();
    }
    
    public void sendStroke(DrawQueue toSend) throws IOException{
        System.out.println("Sending: "+toSend.toString());
        out.writeObject("stroke");
        out.writeObject(toSend);
        out.reset();
    }
    public void sendExit() throws IOException{
        out.writeObject("exit");
    }
    
    PaintCanvas getCanvas(){
        return canvas;
    }

    public boolean isStarted() {
        return started;
    }
    
    public ArrayList<OtherPlayer> getPlayers(){
        return new ArrayList<>(otherPlayers);
    }

    public String getChat() {
        return chat;
    }
    
    public static class OtherPlayer implements Serializable{
        private String name;
        private String role;

        public OtherPlayer(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
        
        @Override
        public String toString(){
            return name+" - "+role;
        }
    }
}

class NetworkCanvasClientListener implements Runnable {
    private final NetworkCanvasClient nc;

    public NetworkCanvasClientListener(NetworkCanvasClient nc) {
        this.nc = nc;
    }
    
    @Override
    public void run() {
        while (true){
            try {
                Object input = nc.read();
                switch (input.toString()) {
                    case "message":
                        String name = nc.read().toString();
                        String message = nc.read().toString();
                        nc.chat += "|"+name+"|: "+message +"\n";
                        break;
                    case "stroke":
                        DrawQueue item = (DrawQueue) nc.read();
                        System.out.println("Receiving: "+item.toString());
                        nc.getCanvas().showDrawQueue(item);
                        break;
                    case "connections":
                        nc.otherPlayers.clear();
                        nc.otherPlayers = new ArrayList<>((ArrayList<OtherPlayer>) nc.read());
                        break;
                    case "start":
                        nc.started = true;
                        nc.id = (Integer) nc.read();
                        break;
                    case "turn":
                        Integer turnIndex = (Integer) nc.read();
                        String turnName = (String) nc.read();
                        nc.myTurn = Objects.equals(turnIndex, nc.id);
                        nc.turnName = turnName;
                        break;
                    default:
                        break;
                }
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(NetworkCanvasHostListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}




