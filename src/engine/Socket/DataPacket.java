package engine.Socket;

import engine.ItemManager;

import java.io.Serializable;
import java.util.List;

public class DataPacket implements Serializable {
    private String command;
    private List<String> data;
    private String Cooldown;
    private ItemManager.ItemType itemType;
    private boolean itemCheck;

    public DataPacket(String command, List<String> data, String Cooldown, ItemManager.ItemType itemType, boolean itemCheck) {
        this.command = command;
        this.data = data;
        this.Cooldown = Cooldown;
        this.itemType = itemType;
        this.itemCheck = itemCheck;
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
    public ItemManager.ItemType getItemType() { return itemType; }
    public boolean getItemCheck() { return itemCheck; }
}