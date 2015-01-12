package de.htwg.mc.irdroid.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armin on 10/01/15.
 */
public class Device {
    /**
     * name or description of device
     */
    private String description;
    private Map<CommandType, Command> commandMap = new HashMap<>();

    public Device(CommandType commandType, Command command) {
        commandMap.put(commandType, command);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<CommandType, Command> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(Map<CommandType, Command> commandMap) {
        this.commandMap = commandMap;
    }

    /**
     * type of specific command
     */
    public enum CommandType {
        power, volumeUp, volumeDown, channelUp, channelDown, digits, other
    }
}
