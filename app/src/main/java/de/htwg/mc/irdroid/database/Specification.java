package de.htwg.mc.irdroid.database;

import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbQuery;
import de.htwg.mc.irdroid.model.Model;

/**
 * The Specification is for the abstraction of the read method of the Repository.
 * All methods should be implemented for a concrete specification, to be able to switch
 * the Repository.
 */
public interface Specification<T extends Model> {
    /**
     * Criteria for the MockRepository implementation.
     * This method will be called for every element of the Repository and return true, if
     * the element satisfy the criteria.
     * @param index of the document in the Collection of the Mock Repository.
     * @param document the document to check if it match the criteria.
     * @return
     */
    boolean satisfyCriteria(int index, T document);

    /**
     * Get the Query parameters for a CB (Couchbase) implementation.
     * @return The query parameters for the CB implementation.
     */
    CbQuery getCbQuery();
}