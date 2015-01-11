package de.htwg.mc.irdroid.model;

import java.util.HashMap;
import java.util.Map;

public class Device extends BaseModel {
	private String name;
    private Map<String, int[]> codes;

    public Device() {
        // intentionally empty
    }

	public Device(String name) {
		this.name = name;
        this.codes = new HashMap<String, int[]>();
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public void setCode(String name, int[] pattern) {
        codes.put(name, pattern);
    }

    public int[] getCode(String name) {
        return codes.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        if (!codes.equals(device.codes)) return false;
        if (!name.equals(device.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + codes.hashCode();
        return result;
    }
}
