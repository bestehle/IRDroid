package de.htwg.mc.irdroid.database.implementation.repository.couchbase;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import de.htwg.mc.irdroid.database.Connector;
import de.htwg.mc.irdroid.model.BaseModel;

/**
 * This class is the connection to a Cb database instance. The class will be initialised
 * and passed to any CbRepository, so that each CbRepository operate on the same
 * couchbase lite database instance.
 */
public class CbConnector implements Connector {
    // the number of parts, and id is build of
    static final int ID_PARTS_LENGTH = 3;
    // the index of the type part
    static final int ID_PART_TYPE = 1;
    static final int ID_PART_EMAIL = 0;
    static final int ID_PART_ID = 2;
    // timeout for calling the authentication server on login
    static final int AUTH_CONNECTION_TIMEOUT = 5000;
    // timeout for receiving data from the authentication server on login
    static final int AUTH_SOCKET_TIMEOUT = 5000;
    private final String databaseName;
    private final URL syncUrl;
    private Replication pull = null;
    private Replication push = null;
    private Database database = null;
    private Manager manager = null;
    private String email = null;
    private Context context;

    public CbConnector(Context context, String databaseName, String syncUrl) {
        // store variables
        this.databaseName = databaseName;
        this.context = context;
        // create a manager and open the database
        Log.v(Log.TAG_DATABASE, "Try to Crate Cb Database");
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(databaseName);
            database.delete();
            database = manager.getDatabase(databaseName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        // create the url to which we are able to sync
        // if we pass an invalid url we throw an RuntimeException, because it
        // is a configuration error
        try {
            this.syncUrl = new URL(syncUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Create a view and register its map function:
        View viewByType = database.getView(CbQuery.VIEW_BY_TYPE);
        viewByType.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                String id = (String) document.get("_id");
                // split the id
                String[] parts = id.split(BaseModel.ID_DELIMITER);
                // is it a valid id?
                if (ID_PARTS_LENGTH != parts.length) {
                    return;
                }
                String[] key = {parts[ID_PART_TYPE], parts[ID_PART_ID]};
                Log.e(Log.TAG_VIEW, key[0]+","+key[1]);
                // emit compound key
                emitter.emit(key, null);
            }
        }, "1");

        // Create a view and register its map function:
        View viewBySpecificExercise = database.getView(CbQuery.VIEW_SPECIFIC_EXERCISE);
        viewBySpecificExercise.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                String expectedType = "SpecificExercise";
                String type = (String) document.get("type");
                String therapyplan = (String) document.get("therapyplan");

                if (!expectedType.equals(type)) {
                    return;
                }

                // emit compound key
                emitter.emit(therapyplan, null);
            }
        }, "1");
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public boolean login(String email, String password) {
        // replication already enabled?
        if (pull != null || push != null) {
            Log.e(Log.TAG_DATABASE, "logout before you like to login again!");
            return false;
        }

        // Set the logged in user.
        this.email = email;

        // check if the username + password are valid.
        // Note : If we use another authentication mechanism, this test should be obsolete
        Log.e(Log.TAG_DATABASE, "Check credentials for user '" + this.email + "' on url "+syncUrl.toString());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(this.email, password));

        // set http parameters.
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, AUTH_CONNECTION_TIMEOUT);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, AUTH_SOCKET_TIMEOUT);

        DefaultHttpClient http = new DefaultHttpClient(httpParameters);
        http.setCredentialsProvider(credentialsProvider);

        try {
            HttpResponse response = http.execute(new HttpHead(syncUrl.toString()));
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() != HttpStatus.SC_OK){
                Log.e(Log.TAG_DATABASE, "Http status is "+statusLine.getStatusCode());
                return false;
            }
        } catch (ClientProtocolException e) {
            Log.e(Log.TAG_DATABASE, e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(Log.TAG_DATABASE, e.getMessage());
            return false;
        }

        // setup pull Replication handler
        pull = database.createPullReplication(syncUrl);
        pull.setContinuous(true);

        // setup push Replication handler
        push = database.createPushReplication(syncUrl);
        push.setContinuous(true);

        // Use basic authentication
        // Warning : The basic authentication is not very secure, because the username + password
        // will be added to any sent header. We should use OAuth or Cookie Auth which are more secure.
        Authenticator basicAuthenticator = AuthenticatorFactory.createBasicAuthenticator(this.email, password);

        // set authentication to Replication handlers and enable replication.
        pull.setAuthenticator(basicAuthenticator);
        push.setAuthenticator(basicAuthenticator);

        push.addChangeListener(new Replication.ChangeListener() {
            @Override
            public void changed(Replication.ChangeEvent event) {
                Log.i(Log.TAG_DATABASE, event.toString());
            }
        });
        pull.addChangeListener(new Replication.ChangeListener() {
            @Override
            public void changed(Replication.ChangeEvent event) {
                Log.i(Log.TAG_DATABASE, event.toString());
            }
        });

        pull.start();
        push.start();

        return true;
    }

    @Override
    public boolean logout() {
        // replication already enabled?
        if (pull == null || push == null) {
            Log.e(Log.TAG_DATABASE, "login before you like to logout!");
            return false;
        }
        pull.stop();
        push.stop();
        pull = null;
        push = null;
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
