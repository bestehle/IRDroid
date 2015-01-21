package de.htwg.mc.irdroid.config;

import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.model.Device;

/**
 * The Factory interface specify the available Repositories.
 * A Repository is an abstraction between Model and persistence.
 * Any new Repository should be added here.
 * Additional the Factory will also be used to provide the login/logout
 * mechanism.
 */
public interface Factory {
    /**
     * Provide the login to the database.
     * @param email of the user
     * @param password of the user
     * @return true on success, false on failure
     */
    boolean login(String email, String password);

    /**
     * Provide the logout from the database
     * @return true on success, false on failure
     */
    boolean logout();

    /**
     * Provide access to the Exercise Repository
     * @return The Exercise Repository
     */
    Repository<Device> provideDevice();

//    /**
//     * Provide access to the Therapyplan Repository
//     * @return The Therapyplan Repository
//     */
//    Repository<Therapyplan> provideTherapyplan();
//
//    /**
//     * Provide access to the SpecificExercise Repository
//     * @return The Therapyplan Repository
//     */
//    Repository<SpecificExercise> provideSpecificExercise();

    public void generateDummyCommands();

}
