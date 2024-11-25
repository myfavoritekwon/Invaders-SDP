package engine.Socket;

import java.io.Serializable;
import java.util.List;

public class DataPacket implements Serializable {
    private String command;
    private List<String> data;
    private String Cooldown;

    public DataPacket(String command, List<String> data, String Cooldown) {
        this.command = command;
        this.data = data;
        this.Cooldown = Cooldown;
    }

    public String getCommand() {
        return command;
    }
    public List<String> getData() {
        return data;
    }
    public String getCooldown() {
        return Cooldown;
    }
}