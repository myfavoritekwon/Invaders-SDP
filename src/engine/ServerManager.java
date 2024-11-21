package engine;

import engine.Socket.Server;
import entity.Room;

import java.util.List;


public class ServerManager {
    private Server server;
    private int difficulty;
    private static List<Room> rooms;

    public ServerManager() {
    }

    public ServerManager(int difficulty, List<Room> rooms) {
        this.server = new Server();
        this.difficulty = difficulty;
        this.rooms = rooms;

        server.setIp();
        server.startServer();
    }

    public void startGameServer(){
        newRoom();
        server.startGameServer();
    }

    public void joinGameRoom(Room room){
        server.connectToServer(room);
    }

    private void newRoom(){
        Room room = new Room(server.getHostIp(), server.getPort(), difficulty);
        rooms.add(room);
        System.out.println(rooms.size());
    }

    public static List<Room> getRooms(){
        return rooms;
    }
}
