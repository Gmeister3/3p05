package exceptions;

/**
 * Exception thrown when attempting to upgrade a building or unit beyond its maximum allowed level.
 * <p>
 * Each building is capped at level 5 by default, and the VillageHall may impose
 * additional constraints on the maximum level of dependent buildings.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class MaxLevelReachedException extends Exception {

    /** The name of the entity that has reached its maximum level. */
    private final String entityName;

    /** The maximum level that was reached. */
    private final int maxLevel;

    /**
     * Constructs a MaxLevelReachedException with a detail message.
     *
     * @param message the detail message
     */
    public MaxLevelReachedException(String message) {
        super(message);
        this.entityName = "Unknown";
        this.maxLevel = 0;
    }

    /**
     * Constructs a MaxLevelReachedException with entity and level details.
     *
     * @param entityName the name of the building or unit at max level
     * @param maxLevel   the maximum level that has been reached
     */
    public MaxLevelReachedException(String entityName, int maxLevel) {
        super(String.format("%s has already reached its maximum level of %d.", entityName, maxLevel));
        this.entityName = entityName;
        this.maxLevel = maxLevel;
    }

    /**
     * Returns the name of the entity that reached the maximum level.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Returns the maximum level that was reached.
     *
     * @return the maximum level
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
