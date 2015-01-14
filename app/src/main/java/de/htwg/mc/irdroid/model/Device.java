package de.htwg.mc.irdroid.model;

import java.util.HashMap;
import java.util.Map;

public class Device extends BaseModel {
	private String name;
    private Map<CommandType, Command> commandMap;

    public Device() {
        // intentionally empty
    }

	public Device(String name) {
		this.name = name;
        this.commandMap = new HashMap<>();
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public void addCommand(CommandType commandType, Command command) {
        commandMap.put(commandType, command);
    }

    public Command getCommand(CommandType commandType) {
        return commandMap.get(commandType);
    }

    public Map<CommandType, Command> getCommandMap() {
        return commandMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (!commandMap.equals(device.commandMap)) return false;
        if (!name.equals(device.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + commandMap.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * type of specific command
     */
    public enum CommandType {
        power, volumeUp, volumeDown, channelUp, channelDown, digits, other
    }
}
