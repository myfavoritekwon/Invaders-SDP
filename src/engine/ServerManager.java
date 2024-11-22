package engine;

import engine.Socket.Client;
import engine.Socket.Server;
import entity.Room;

import java.util.List;


public class ServerManager {
    private Client client;
    private Server server;
    private int difficulty;
    private static List<Room> rooms;
    private String hostIp;

    public ServerManager() {
    }

    public ServerManager(int difficulty, List<Room> rooms, Server server, Client client) {
        this.client = client;
        this.server = server;
        this.difficulty = difficulty;
        this.rooms = rooms;

        if(rooms.isEmpty())
            server.setIp();
        hostIp = server.getHostIp();
        newRoom();
        //서버 정보 뿌림
        Server.startInfoServer();
        //게임 서버
        Server.startMainServer();
    }

    public void startGameServer(){
        client.connectServer(hostIp);
    }

    private void newRoom(){
        Room room = new Room(server.getHostIp(), server.getPort(), difficulty);
        rooms.add(room);
        System.out.println(rooms.size());
    }

    public static List<Room> getRooms(){
        return rooms;
    }

    public static void deleteRoom(boolean check){
        rooms.removeFirst();
    }
}
