package de.htwg.mc.irdroid.database.implementation.specification;

import java.util.Arrays;
import java.util.HashMap;

import de.htwg.mc.irdroid.database.Specification;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbQuery;
import de.htwg.mc.irdroid.model.Device;

/**
 * Specification to read all Device documents.
 */
public class DeviceAllSpecification implements Specification<Device> {
    @Override
    public boolean satisfyCriteria(int index, Device document) {
        return true;
    }

    @Override
    public CbQuery getCbQuery() {
        String type = Device.class.getSimpleName();
        CbQuery query = new CbQuery();

        query.setView(CbQuery.VIEW_BY_TYPE);
        query.setStartKey(type);
        query.setEndKey(Arrays.asList(type, new HashMap<String, Object>()));

        return query;
    }
}
