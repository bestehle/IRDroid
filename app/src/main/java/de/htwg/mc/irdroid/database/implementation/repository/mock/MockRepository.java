package de.htwg.mc.irdroid.database.implementation.repository.mock;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Connector;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.Specification;
import de.htwg.mc.irdroid.database.implementation.broadcast.RepositoryBroadcast;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.Device;
import de.htwg.mc.irdroid.model.Model;


/**
 * Implementation of the Repository as a Mock, which store any data in a Collection.
 */
public class MockRepository<T extends Model> implements Repository<T> {
    private Connector connector;
    private final List<T> list = new LinkedList<>();
    private final RepositoryBroadcast<T> broadcast;
    private final Class<T> type;

    public MockRepository(MockConnector connector, Class<T> typeClass) {
        this.connector = connector;
        this.type = typeClass;
        this.broadcast = new RepositoryBroadcast<>(type);

    }

    @Override
    public T create(T document) {
        // ignore null objects
        if (null == document) {
            return null;
        }
        synchronized (list) {
            document.setId(UUID.randomUUID().toString());
            document.setOwner(connector.getEmail());
            list.add(document);
        }

        Intent intent = new Intent(broadcast.getActionCreate());
        intent.putExtra(broadcast.getExtraId(), document.getId());
        LocalBroadcastManager.getInstance(connector.getContext()).sendBroadcast(intent);

        return document;
    }

    @Override
    public T update(T document) {
        Boolean exist = false;
        // ignore null objects
        if (null == document) {
            return null;
        }
        T doc = null;
        synchronized (list) {
            if (list.contains(document)) {
                document.setOwner(connector.getEmail());
                list.remove(document);
                list.add(document);
                doc = document;
                exist = true;
            }
        }

        if (exist) {
            Intent intent = new Intent(broadcast.getActionUpdate());
            intent.putExtra(broadcast.getExtraId(), document.getId());
            LocalBroadcastManager.getInstance(connector.getContext()).sendBroadcast(intent);
        }

        return doc;
    }

    @Override
    public void delete(String id) {
        Boolean exist = false;
        // ignore null objects
        if (null == id) {
            return;
        }
        synchronized (list) {
            for (T document : list) {
                if (document.getId().equals(id)) {
                    list.remove(document);
                    exist = true;
                    break;
                }
            }
        }

        if (exist) {
            Intent intent = new Intent(broadcast.getActionDelete());
            intent.putExtra(broadcast.getExtraId(), id);
            LocalBroadcastManager.getInstance(connector.getContext()).sendBroadcast(intent);
        }
    }

    @Override
    public List<T> read(Specification<T> specification) {
        int i = 0;
        List<T> result = new LinkedList<>();

        // ignore null objects
        if (null == specification) {
            return result;
        }

        synchronized (list) {
            for (T document : list) {
                if (specification.satisfyCriteria(i, document)) {
                    result.add(document);
                }
                i++;
            }
        }
        return result;
    }
}
