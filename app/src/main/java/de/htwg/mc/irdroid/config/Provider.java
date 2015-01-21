package de.htwg.mc.irdroid.config;

import android.content.Context;

import de.htwg.mc.irdroid.config.module.CbFactory;
import de.htwg.mc.irdroid.config.module.MockWithDataFactory;

/**
 * Created by deparlak on 04.12.2014.
 * <p/>
 * The Provider is a singleton class, which return the instance to the Factory. The
 * Factory should be replaced here.
 */
public class Provider {
    private static Context context = null;
    private static Provider singleton = new Provider();
    private Factory factory = null;

    private Provider() {
    }

    public static Provider getInstance() {
        return singleton;
    }

    public static void setContext(Context c) {
        context = c;
    }

    public Factory getFactory() {
        if (this.factory == null) {
            synchronized (this) {
                if (this.factory == null) {
                    /**
                     * Switch the implementation here.
                     */
                    //this.factory = new MockFactory(context);
                    this.factory = new CbFactory(context);
//                    this.factory = new MockWithDataFactory(context);
                }
            }
        }
        return this.factory;
    }
}