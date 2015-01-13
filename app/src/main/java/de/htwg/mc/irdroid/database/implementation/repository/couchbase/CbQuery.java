package de.htwg.mc.irdroid.database.implementation.repository.couchbase;

import java.util.List;

/**
 * Query parameters for the read(..) method of a CbRepository.
 */
public class CbQuery {
    /**
     * Default view for all documents.
     */
    public static final String VIEW_ALL_DOCS = null;
    /**
     * The name of a map function which emit the type of each document.
     */
    public static final String VIEW_BY_TYPE = "byType";
    /**
     * The name for a special map function, which emit the therapyplan id of each SpecificExercises.
     * So we are able to find SpecificExercises by their Therapyplan.
     */
    public static final String VIEW_SPECIFIC_EXERCISE = "SpecificExercise";

    private String view = null;
    private Object startKey = null;
    private Object endKey = null;
    private List<Object> keys = null;
    private int skip = 0;
    private int limit = 1000;

    public CbQuery() {
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Object getEndKey() {
        return endKey;
    }

    public void setEndKey(Object endKey) {
        this.endKey = endKey;
    }

    public Object getStartKey() {
        return startKey;
    }

    public void setStartKey(Object startKey) {
        this.startKey = startKey;
    }

    public List<Object> getKeys() {
        return keys;
    }

    public void setKeys(List<Object> keys) {
        this.keys = keys;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }
}
