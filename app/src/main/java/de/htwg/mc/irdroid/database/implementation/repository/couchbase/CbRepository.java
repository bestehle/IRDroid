package de.htwg.mc.irdroid.database.implementation.repository.couchbase;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.Specification;
import de.htwg.mc.irdroid.database.implementation.broadcast.RepositoryBroadcast;
import de.htwg.mc.irdroid.model.BaseModel;
import de.htwg.mc.irdroid.model.Model;


/**
 * Implementation of the Repository in Couchbase-lite, which store any data in a a sql-lite database.
 */
public class CbRepository<T extends Model> implements Repository<T> {
    private final Class<T> type;
    private final CbConnector connector;
    private final Gson gson = new Gson();
    private final RepositoryBroadcast<T> broadcast;

    public CbRepository(final CbConnector connector, Class<T> typeClass) {
        this.connector = connector;
        this.type = typeClass;
        this.broadcast = new RepositoryBroadcast<>(type);

        // database change listener.
        // Note that the change listener is globally for all documents, but the Repository
        // is for a specific type. Therefore we check the document type (the type is part
        // of the document id) and ignore other document types.
        connector.getDatabase().addChangeListener(new Database.ChangeListener() {
            public void changed(Database.ChangeEvent event) {
                // run through changes.
                for (DocumentChange change : event.getChanges()) {
                    // handle a conflict. Actually we ignore conflicts.
                    if (change.isConflict()) {
                        continue;
                    }
                    // extract the document type of the id.
                    String[] parts = change.getDocumentId().split(BaseModel.ID_DELIMITER);
                    // is it a valid id?
                    if (CbConnector.ID_PARTS_LENGTH != parts.length) {
                        Log.e(Log.TAG_DATABASE, "Unexpected document id "+change.getDocumentId());
                        continue;
                    }
                    // ignore documents of other type.
                    if (!type.getSimpleName().equals(parts[CbConnector.ID_PART_TYPE])) {
                        continue;
                    }
                    // ok send a broadcast for this document
                    Log.i(Log.TAG_DATABASE, "Broadcast for document "+change.getDocumentId());


                    // create the intent with the document id
                    Intent intent = new Intent();
                    intent.putExtra(broadcast.getExtraId(), change.getDocumentId());

                    // Is the document deleted, updated or created?
                    // and set the action based on the status of the document
                    if (change.getAddedRevision().isDeleted()) {
                        intent.setAction(broadcast.getActionDelete());
                    } else if (1 < change.getAddedRevision().getGeneration()) {
                        intent.setAction(broadcast.getActionUpdate());
                    } else {
                        intent.setAction(broadcast.getActionCreate());
                    }

                    // send the local broadcast
                    LocalBroadcastManager.getInstance(connector.getContext()).sendBroadcast(intent);
                }
            }
        });
    }

    @Override
    public T create(T document) {
        // ignore null objects
        if (null == document) {
            Log.e(Log.TAG_DATABASE, "document is null");
            return null;
        }

        //set the owner
        document.setOwner(connector.getEmail());
        // generate the id for the document
        document.generateId();

        Log.v(Log.TAG_DATABASE, "Try to create " + document.getId());

        // create the document by converting the POJO to a string and
        // convert the created string to a String-object map.
        String json = gson.toJson(document, type);
        Log.v(Log.TAG_DATABASE, "Is Json " + json);

        final HashMap<String, Object> properties = gson.fromJson(json, HashMap.class);
        Log.v(Log.TAG_DATABASE, "Converted to " + properties.toString());
        // get the document with the specified id.
        Document doc = connector.getDatabase().getDocument(document.getId());


        // add the properties to this doc, which get than saved to the database
        try {
            doc.putProperties(properties);
            Log.v(Log.TAG_DATABASE, "create was successful");
            return document;
        } catch (CouchbaseLiteException e) {
            Log.e(Log.TAG_DATABASE, e.toString());
            return null;
        }
    }

    @Override
    public T update(T document) {
        // ignore null objects
        if (null == document) {
            Log.e(Log.TAG_DATABASE, "document is null and will be ignored");
            return null;
        }

        Document doc = connector.getDatabase().getDocument(document.getId());
        // Does the document not exist?
        // The document does not exists if there is no revision.
        if (null == doc || null == doc.getCurrentRevisionId()) {
            Log.i(Log.TAG_DATABASE, "document is null and will be ignored");
            return null;
        }

        //set the owner
        document.setOwner(connector.getEmail());
        Log.v(Log.TAG_DATABASE, "Try to update " + document.getId());

        // create the document by converting the POJO to a string and
        // convert the created string to a String-object map.
        String json = gson.toJson(document, type);
        final HashMap<String, Object> properties = gson.fromJson(json, HashMap.class);
        Log.v(Log.TAG_DATABASE, "Converted to " + properties.toString());

        Log.v(Log.TAG_DATABASE, "Update document to : " + json);
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    newRevision.setUserProperties(properties);
                    return true;
                }
            });
        } catch (CouchbaseLiteException e) {
            Log.e(Log.TAG_DATABASE, e.getMessage());
            return null;
        }

        return document;
    }

    @Override
    public void delete(String id) {
        // ignore null objects
        if (null == id) {
            Log.e(Log.TAG_DATABASE, "id is null and will be ignored");
            return;
        }

        Document doc = connector.getDatabase().getDocument(id);
        // Does the document not exist?
        // The document does not exists if there is no revision.
        if (null == doc || null == doc.getCurrentRevisionId()) {
            Log.i(Log.TAG_DATABASE, "document is null and will be ignored");
            return;
        }

        // the document exist, so try to delete it.
        try {
            doc.delete();
        } catch (CouchbaseLiteException e) {
            Log.e(Log.TAG_DATABASE, e.toString());
        }
    }

    @Override
    public List<T> read(Specification<T> spec) {
        List<T> list = new LinkedList<>();
        // ignore null objects
        if (null == spec) {
            Log.e(Log.TAG_DATABASE, "Specification is null");
            return list;
        }
        CbQuery parameter = spec.getCbQuery();
        Query query;

        // Setup query by the specification
        if(null != spec.getCbQuery().getView()) {
            query = connector.getDatabase().getView(parameter.getView()).createQuery();
        } else {
            query = connector.getDatabase().createAllDocumentsQuery();
        }
        query.setStartKey(parameter.getStartKey());
        query.setEndKey(parameter.getEndKey());
        // Note that this method should be used, but actually it causes an exception.
        //query.setKeys(parameter.getKeys());
        query.setSkip(parameter.getSkip());
        query.setLimit(parameter.getLimit());
        Log.v(Log.TAG_DATABASE, "Query View : "+parameter.getView());

        // Query the view
        try {
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();

                // type information available?
                if (null == row.getDocument().getProperty("type")) {
                    Log.w(Log.TAG_DATABASE, "Read a document without a type : "+row.getDocument().toString());
                    continue;
                }

                Log.v(Log.TAG_DATABASE, "Expected type : "+type.getSimpleName());
                Log.v(Log.TAG_DATABASE, "Available type : "+row.getDocument().getProperty("type"));
                Log.v(Log.TAG_DATABASE, "Raw document : "+row.getDocument().getProperties().toString());

                if (false == type.getSimpleName().equals(row.getDocument().getProperty("type"))) {
                    Log.w(Log.TAG_DATABASE, "Read not expected type. Maybe your Query parameters are not optimal.");
                    continue;
                }

                // parse the document
                JsonElement jsonElement = gson.toJsonTree(row.getDocument().getProperties());
                T object = gson.fromJson(jsonElement, type);

                // Note that getKeys should be used with the setKeys method, which is actually not working.
                if (null == parameter.getKeys() || parameter.getKeys().contains(object.getId())) {
                    list.add(object);
                }
            }
        } catch (CouchbaseLiteException e) {
            Log.e(Log.TAG_DATABASE, e.toString());
        }
        return list;
    }
}
