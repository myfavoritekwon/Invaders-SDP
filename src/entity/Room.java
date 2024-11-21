package entity;

public class Room {
    private final String ip;
    private final int port;
    private final int difficulty;


    public Room(String ip, int port, int difficulty) {
        this.ip = ip;
        this.port = port;
        this.difficulty = difficulty;
    }

    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public int getDifficulty() {
        return difficulty;
    }
}
