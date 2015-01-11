package de.htwg.mc.irdroid.database.implementation.specification;

import java.util.ArrayList;
import java.util.List;

import de.htwg.mc.irdroid.database.Specification;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbQuery;
import de.htwg.mc.irdroid.model.Device;

/**
 * Specification to read Device documents with a specified id.
 */
public class DeviceByIdSpecification implements Specification<Device> {
    private String id;

    public DeviceByIdSpecification(String id) {
        this.id = id;
    }

    @Override
    public boolean satisfyCriteria(int index, Device document) {
        return (document.getId().equals(id));
    }

    @Override
    public CbQuery getCbQuery() {
        CbQuery query = new CbQuery();

        query.setView(CbQuery.VIEW_ALL_DOCS);
        List<Object> keys = new ArrayList<Object>();
        keys.add(id);
        query.setKeys(keys);

        return query;
    }
}