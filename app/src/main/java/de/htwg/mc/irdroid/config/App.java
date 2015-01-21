package de.htwg.mc.irdroid.config;

import android.app.Application;

import de.htwg.mc.irdroid.app.LoginTask;

/**
 * The App will be called on startup by the android system. Here we initialise the
 * Provider class with the Context of the application.
 */
public class App extends Application {
    private final static String EMAIL = "user1";
    private final static String PASSWORD = "1234";
    private LoginTask login = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Provider.setContext(getApplicationContext());


        if (login == null) {
            login = new LoginTask(EMAIL, PASSWORD);
            login.execute((Void) null);
        }

        // Local Broadcast Receiver for Therapyplan
//        RepositoryBroadcast<SpecificExercise> SpecificExerciseBroadcast = new RepositoryBroadcast<>(SpecificExercise.class);
//        LocalBroadcastManager.getInstance(this).registerReceiver(new SpecificExerciseReceiver(), SpecificExerciseBroadcast.getIntentFilter());

        // Register another Local Broadcast Receiver here....

    }
}