package de.htwg.mc.irdroid.model;

/**
 * The Model interface should be implemented by every Model class.
 * It defines some basic methods, which will be used for Persistence.
 */
public interface Model {
    /**
     * Get the id of the model document, which should be unique
     * @return The id of the object
     */
    String getId();

    /**
     * Set the unique id of the model document.
     * @param id the id which to set
     */
    void setId(String id);

    /**
     * Generate a id for the document
     */
    void generateId();

    /**
     * Return the type of the Model class. Each model class should return
     * a unique type.
     * @return type of Model class
     */
    String getType();

    /**
     * Get the owner of the document. The owner is a unique id, which
     * will be created by the Repository implementation. Note the
     * difference between id and owner. The id is the unique identifier for
     * the document. The owner is the unique identifier to the user which
     * created the document.
     * This mean that:
     * - The id does exist only once
     * - Multiple documents can have the same owner, which mean that these
     * documents where created by the user.
     * @return owner of document
     */
    String getOwner();

    /**
     * Set the owner id of the model document.
     * Note that this value will be set automatically by the Repository implementation.
     * @param owner the owner which to set
     */
    void setOwner(String owner);
}
