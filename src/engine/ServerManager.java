package engine;

import engine.Socket.Client;
import engine.Socket.Server;
import entity.Room;

import java.util.ArrayList;
import java.util.List;


public class ServerManager {
    private Client client;
    private Server server;
    private int difficulty;
    private static List<Room> rooms;
    private String hostIp;

    public ServerManager(int difficulty, List<Room> rooms) {
        this.difficulty = difficulty;
        this.rooms = rooms;

    }

    public void setServer(Server server) {
        this.server = server;
        if(rooms.isEmpty())
            server.setIp();
        hostIp = server.getHostIp();
        newRoom();
        //서버 정보 뿌림
        Server.startInfoServer();
        //게임 서버
        Server.startMainServer();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void startGameServer(){
        client.connectServer(hostIp);
    }

    private void newRoom(){
        Room room = new Room(server.getHostIp(), server.getPort(), difficulty);
        rooms.add(room);
        System.out.println(rooms.size());
    }


    private String ServerMessage;
    private String ClientMessage;
    private List<String> giveShooter;
    private String Cooldown;

    public void setServerButton(String takeButton){
        this.ServerMessage = takeButton;
    }

    public void setClientButton(String takeButton){
        this.ClientMessage = takeButton;
    }

    public String getServerButton() {
        return ClientMessage;
    }

    public String getClientButton() {
        return ServerMessage;
    }

    public List<String> getGiveShooter() {
        return giveShooter;
    }

    public void setGiveShooter(List<String> giveShooter) {
        this.giveShooter = giveShooter;
    }

    public void setCooldown(String Cooldown) {
        this.Cooldown = Cooldown;
    }

    public String getCooldown() {
        return Cooldown;
    }
}
