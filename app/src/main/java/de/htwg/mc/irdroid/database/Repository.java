package de.htwg.mc.irdroid.database;

import java.util.List;

import de.htwg.mc.irdroid.model.Model;

/**
 * The Repository is the abstraction for accessing the persistence Layer. The client,
 * which uses the Repository, does only know the Repository and not the concrete persistence.
 */
public interface Repository<T extends Model> {
    /**
     * Create a document
     * @param document The document which was created.
     * @return The created document will be returned, because the Repository may change
     * some Attributes like the id of the document.
     */
    T create(T document);

    /**
     * Update a document
     * @param document The document which to update.
     * @return The document which was updated will be returned.
     */
    T update(T document);

    /**
     * Delete a document by it's id
     * @param id unique id of document
     */
    void delete(String id);

    /**
     * Query the Repository with a specification.
     * @param spec The specification which tell us how the data will be queried.
     * @return List of specified Model types
     */
    List<T> read(Specification<T> spec);
}
