package de.htwg.mc.irdroid.database.implementation.broadcast;

import android.content.IntentFilter;

import de.htwg.mc.irdroid.model.Model;


/**
 * Definition for the naming of broadcast's for a specific Repository.
 * The naming should be used to send one of the broadcasts or to create
 * a broadcast receiver with this intent filter.
 *
 * Note :
 * - The Broadcast sender should set the Action to one of the below defined
 * values.
 * - The Broadcast sender should also set the id of the Repository document
 */
public class RepositoryBroadcast<T extends Model> {
    private final Class<T> type;

    public RepositoryBroadcast(Class<T> typeClass) {
        this.type = typeClass;
    }

    /**
     * Get the Intent Filter to receive all actions (create/update/delete)
     * @return an intent filter.
     */
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getActionCreate());
        filter.addAction(getActionDelete());
        filter.addAction(getActionUpdate());
        return filter;
    }

    /**
     * The name which will be set for the Intent by the Broadcast Sender.
     * This name should also be used by the Receiver as the Intent filter.
     * @return the name of the Intent
     */
    private String getName() {
        return this.getClass().getName() + "." + type.getSimpleName() + ".";
    }

    /**
     * The action of the Broadcast will be set with intent.setAction(..) to this value
     * if a document was created.
     * @return the action name for creating a document
     */
    public String getActionCreate() {
        return getName() + "create";
    }

    /**
     * The action of the Broadcast will be set with intent.setAction(..) to this value
     * if a document was updated.
     * @return the action name for updating a document
     */
    public String getActionUpdate() {
        return getName() + "update";
    }

    /**
     * The action of the Broadcast will be set with intent.setAction(..) to this value
     * if a document was deleted.
     * @return the action name for deleting a document
     */
    public String getActionDelete() {
        return getName() + "delete";
    }

    /**
     * The naming of the extra for the id, will be set to this value.
     * @return the name of the id extra.
     */
    public String getExtraId() {
        return getName() + "id";
    }
}