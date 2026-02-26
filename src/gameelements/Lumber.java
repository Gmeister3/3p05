package gameelements;

/**
 * Represents Lumber, a construction material resource.
 * <p>
 * Lumber is required for building most structures and training certain unit types.
 * It is produced by {@link LumberMill} buildings and {@link Lumberman} peasants.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Lumber extends Resource {

    /**
     * Constructs a Lumber resource with the specified initial quantity.
     *
     * @param quantity the starting amount of lumber
     */
    public Lumber(double quantity) {
        super(quantity);
    }

    /**
     * Returns the resource display name.
     *
     * @return "Lumber"
     */
    @Override
    public String getResourceName() {
        return "Lumber";
    }
}
