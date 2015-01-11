package de.htwg.mc.irdroid.config.module;

import android.content.Context;
import de.htwg.mc.irdroid.config.Factory;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbConnector;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbRepository;
import de.htwg.mc.irdroid.model.Device;

/**
 * Provide access to the Repository cb implementations.
 */
public class CbFactory implements Factory {
    private final String syncUrl = "http://10.0.2.2:4984/sync_gateway";
    private final String databaseName = "irdroid";
    private final CbConnector connector;
    private final Repository<Device> deviceRepository;

    public CbFactory(Context context) {
        connector = new CbConnector(context, databaseName, syncUrl);
        deviceRepository = new CbRepository<>(connector, Device.class);
    }

    @Override
    public boolean login(String email, String password) {
        return connector.login(email, password);
    }

    @Override
    public boolean logout() {
        return connector.logout();
    }

    @Override
    public Repository<Device> provideExercise() {
        return deviceRepository;
    }
}
