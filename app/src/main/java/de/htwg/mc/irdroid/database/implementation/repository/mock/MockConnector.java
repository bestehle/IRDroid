package de.htwg.mc.irdroid.database.implementation.repository.mock;

import android.content.Context;

import de.htwg.mc.irdroid.database.Connector;


/**
 * Implementation of the Connector as a Mock. The Mock has some pre-defined users.
 */
public class MockConnector implements Connector {
    private final Context context;
    private String email = null;

    public MockConnector(Context context) {
        this.context = context;
    }

    @Override
    public boolean login(String email, String password) {
        this.email = email;
        return true;
    }

    @Override
    public boolean logout() {
        email = null;
        return true;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
