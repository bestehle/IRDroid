package de.htwg.mc.irdroid.config;

import android.app.Application;

/**
 * The App will be called on startup by the android system. Here we initialise the
 * Provider class with the Context of the application.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Provider.setContext(getApplicationContext());

        // Local Broadcast Receiver for Therapyplan
//        RepositoryBroadcast<SpecificExercise> SpecificExerciseBroadcast = new RepositoryBroadcast<>(SpecificExercise.class);
//        LocalBroadcastManager.getInstance(this).registerReceiver(new SpecificExerciseReceiver(), SpecificExerciseBroadcast.getIntentFilter());

        // Register another Local Broadcast Receiver here....

    }
}