package de.htwg.mc.irdroid.model;

import java.util.UUID;

/**
 * This BaseModel (or Base Entity called) define the basis attribute which each
 * Model class has.
 */
public class BaseModel implements Model {
    public static final String ID_DELIMITER = "%";
    private String id;
    private String owner;
    private String type = this.getClass().getSimpleName();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public void generateId() {
        this.id = owner + ID_DELIMITER + type + ID_DELIMITER + UUID.randomUUID().toString();
    }
}
