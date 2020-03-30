/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blindpainting.GUI.common.networkcanvas;

import blindpainting.GUI.common.networkcanvas.NetworkCanvasClient.OtherPlayer;
import blindpainting.GUI.viewing.DrawQueue;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author Nick Berryman
 */
public class NetworkCanvasHost {
    ServerSocket host;
    ArrayList<GameClient> clients = new ArrayList<>();
    int activePainterIndex;
    
    public void startHost(int port) throws IOException{
        host = new ServerSocket(port);
        Thread t = new Thread(() -> {
            while (true)
            {
                try {
                    GameClient c = GameClient.accept(host);
                    if (c != null && !clients.contains(c)){
                        clients.add(c);
                        c.start(this);
                        sendClients();
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(NetworkCanvasHost.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    void removeClient(GameClient c){
        clients.remove(c);
        sendClients();
    }
    
    private void sendToAll(Object data) throws IOException{
        for (GameClient gc : clients){
            gc.send(data);
        }
    }
    
    private void sendToRole(GameClient from, String role, Object data) throws IOException{
        for (GameClient gc : clients){
            if (gc.getRole().equals(role)) gc.send(data);
        }
    }
    
    private void sendToRoles(GameClient from, Object data, String... roles) throws IOException{
        for (String role : roles)
            sendToRole(from, role, data);
    }
    
    private void sendToRolesNotSelf(GameClient from, Object data, String... roles) throws IOException{
        for (String role : roles)
            sendToRoleNotSelf(from, role, data);
    }
    
    private void sendToRoleNotSelf(GameClient from, String role, Object data) throws IOException{
        for (GameClient gc : clients){
            if (gc.getRole().equals(role) && from != gc) gc.send(data);
        }
    }
    
    void sendStroke(GameClient from, DrawQueue stroke){
        try {
            if (from.getRole().equals("Painter")){
                sendToRoles(from, "stroke", "Instructor", "Appreciator");
                sendToRoles(from, stroke, "Instructor", "Appreciator");
            }
            else if (from.getRole().equals("Instructor")){
                sendToRoles(from, "stroke", "Painter");
                sendToRoles(from, stroke, "Painter");
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkCanvasHost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void sendMessage(GameClient from, String message){
        try {
            switch (from.getRole()){
                case "Painter":
                    sendToRoles(from, "message", "Instructor", "Painter");
                    sendToRoles(from, from.getName(), "Instructor", "Painter");
                    sendToRoles(from, message, "Instructor", "Painter");
                    break;
                case "Instructor":
                    sendToRoles(from, "message", "Instructor", "Painter");
                    sendToRoles(from, from.getName(), "Instructor", "Painter");
                    sendToRoles(from, message, "Instructor", "Painter");
                    break;
                case "Appreciator":
                    sendToRoles(from, "message", "Instructor", "Appreciator", "Painter");
                    sendToRoles(from, from.getName(), "Instructor", "Appreciator", "Painter");
                    sendToRoles(from, message, "Instructor", "Appreciator", "Painter");
                    break;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NetworkCanvasHost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendStart() throws IOException{
        sendToAll("start");
        for (int i = 0; i < clients.size(); i++){
            clients.get(i).send(i);
        }
        
        int i = 0;
        for (GameClient g : clients){
            if ("Painter".equals(g.getRole())){
                activePainterIndex = i;
                break;
            }
            i++;
        }
        sendToAll("turn");
        sendToAll(activePainterIndex);
        sendToAll(clients.get(activePainterIndex).getName());
    }
    
    public void sendNextTurn() throws IOException{
        for (int i = activePainterIndex+1; true; i++){
            if (i >= clients.size()) i = 0;
            if ("Painter".equals(clients.get(i).getRole())){
                activePainterIndex = i;
                break;
            }
        }
        System.out.println(activePainterIndex);
        sendToAll("turn");
        sendToAll(activePainterIndex);
        sendToAll(clients.get(activePainterIndex).getName());
    }
    
    public void sendClients(){
        try {
            ArrayList<OtherPlayer> out = new ArrayList<>();
            for (GameClient a : clients) out.add(new OtherPlayer(a.getName(),a.getRole()));
            sendToAll("connections");
            sendToAll(out);
        } catch (IOException ex) {
            Logger.getLogger(NetworkCanvasHost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class GameClient {
    private Socket sock;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String role;
    private String name;
    
    private GameClient(){};
    
    public static GameClient accept(ServerSocket host) throws IOException, ClassNotFoundException{
        GameClient gc = new GameClient();
        gc.sock = host.accept();
        gc.out = new ObjectOutputStream(gc.sock.getOutputStream());
        gc.out.flush();
        gc.in = new ObjectInputStream(gc.sock.getInputStream());
        String role = gc.in.readObject().toString();
        switch (role){
            case "Instructor":
                gc.role = "Instructor";
                break;
            case "Painter":
                gc.role = "Painter";
                break;
            case "Appreciator":
                gc.role = "Appreciator";
                break;
            default:
                return null;
        }
        gc.name = gc.in.readObject().toString();
        return gc;
    }
    
    public Object read() throws IOException, ClassNotFoundException, SocketException{
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
    
    public void send(Object toSend) throws IOException{
        out.writeObject(toSend);
        out.reset();
    }
    
    public String getRole(){
        return this.role;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void start(NetworkCanvasHost nc){
        Thread t = new Thread(new NetworkCanvasHostListener(nc, this));
        t.setDaemon(true);
        t.start();
    }
    
}
class NetworkCanvasHostListener implements Runnable {
    private final NetworkCanvasHost nc;
    private final GameClient gc;

    public NetworkCanvasHostListener(NetworkCanvasHost nc, GameClient gc) {
        this.nc = nc;
        this.gc = gc;
    }
    
    @Override
    public void run() {
        DrawQueue item;
        boolean running = true;
        while (running){
            try {
                Object input = gc.read();
                if (input == null){
                    nc.removeClient(gc);
                    nc.sendClients();
                    running = false;
                    break;
                }
                switch (input.toString()) {
                    case "message":
                        String message = gc.read().toString();
                        nc.sendMessage(gc, message);
                        break;
                    case "stroke":
                        item = (DrawQueue) gc.read();
                        System.out.println("Relaying: "+item.toString());
                        nc.sendStroke(gc, item);
                        if ("Painter".equals(gc.getRole())) nc.sendNextTurn();
                        break;
                    case "exit":
                        nc.removeClient(gc);
                        nc.sendClients();
                        running = false;
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

//TODO do turn order and stuff like that
