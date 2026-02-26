package gameelements;

/**
 * Represents Iron, a secondary resource used for construction and training.
 * <p>
 * Iron is primarily required for defensive buildings and heavy military units.
 * It is produced by {@link IronMine} buildings and {@link IronMiner} peasants.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Iron extends Resource {

    /**
     * Constructs an Iron resource with the specified initial quantity.
     *
     * @param quantity the starting amount of iron
     */
    public Iron(double quantity) {
        super(quantity);
    }

    /**
     * Returns the resource display name.
     *
     * @return "Iron"
     */
    @Override
    public String getResourceName() {
        return "Iron";
    }
}
