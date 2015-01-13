package de.htwg.mc.irdroid.database;

import android.content.Context;

/**
 * This is the specification of the Connector, which stand for the connection to a Database.
 * The Connector basically specify some basic methods, which should be supported by the Database
 * connection.
 */
public interface Connector {
    /**
     * The login method should return true if the login was successful. Otherwise it should return false.
     * @param email of the user
     * @param password of the user
     * @return true on success, false on failure.
     */
    boolean login(String email, String password);

    /**
     * Logout the active user.
     * @return true on success, false on failure.
     */
    boolean logout();

    /**
     * Get the logged in user.
     * @return The email of the logged in user.
     */
    String getEmail();

    /**
     * Get the context at which the Connector was created.
     * @return the Android context
     */
    Context getContext();
}
