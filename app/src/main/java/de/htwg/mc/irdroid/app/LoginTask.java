package de.htwg.mc.irdroid.app;

/**
 * Created by Benjamin on 12.01.2015.
 */

import android.content.Intent;
import android.os.AsyncTask;

import de.htwg.mc.irdroid.config.Provider;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;

    public LoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return Provider.getInstance().getFactory().login(mEmail, mPassword);
    }
}