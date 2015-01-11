package de.htwg.mc.irdroid.config.module;

import android.content.Context;

import de.htwg.mc.irdroid.config.Factory;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.repository.mock.MockConnector;
import de.htwg.mc.irdroid.database.implementation.repository.mock.MockRepository;
import de.htwg.mc.irdroid.model.Device;

/**
 * Provide access to the Repository mock implementations with dummy data.
 */
public class MockWithDataFactory implements Factory {
    private final MockConnector connector;
    private final Repository<Device> deviceRepository;

    public MockWithDataFactory(Context context) {
        connector = new MockConnector(context);
        deviceRepository = new MockRepository<>(connector, Device.class);

        /**
         * Paste any dummy data here.
         */

        // login as the default user, so that the user is the owner of these documents.
        connector.login("user", "pw");

        deviceRepository.create(new Device("Sony TV"));
        deviceRepository.create(new Device("LG TV"));
        deviceRepository.create(new Device("Samsung TV"));
        deviceRepository.create(new Device("Beamer"));

        // logout here
        connector.logout();
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
    public Repository<Device> provideDevice() {
        return deviceRepository;
    }

}
