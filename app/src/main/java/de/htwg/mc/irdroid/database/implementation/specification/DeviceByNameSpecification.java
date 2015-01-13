package de.htwg.mc.irdroid.database.implementation.specification;

import java.util.LinkedList;
import java.util.List;

import de.htwg.mc.irdroid.database.Specification;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbQuery;
import de.htwg.mc.irdroid.model.Device;

/**
 * Specification to read all Exercise documents with a specified name.
 */
public class DeviceByNameSpecification implements Specification<Device> {
    private List<String> names;

    public DeviceByNameSpecification(String name) {
        names = new LinkedList();
        names.add(name);
    }

    public DeviceByNameSpecification(List<String> names) {
        this.names = names;
    }

    @Override
    public boolean satisfyCriteria(int index, Device document) {
        return names.contains(document.getName());
    }

    @Override
    public CbQuery getCbQuery() {
        return null;
    }
}
