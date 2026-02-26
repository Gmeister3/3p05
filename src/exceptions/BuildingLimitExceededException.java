package exceptions;

/**
 * Exception thrown when adding a building to a village that has already reached its building limit.
 * <p>
 * A village is constrained to a maximum of {@code GameConstants.MAX_BUILDINGS} buildings.
 * Attempting to exceed this limit raises this exception.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class BuildingLimitExceededException extends Exception {

    /** The current number of buildings in the village. */
    private final int currentCount;

    /** The maximum number of buildings allowed. */
    private final int maxAllowed;

    /**
     * Constructs a BuildingLimitExceededException with a detail message.
     *
     * @param message the detail message
     */
    public BuildingLimitExceededException(String message) {
        super(message);
        this.currentCount = 0;
        this.maxAllowed = 0;
    }

    /**
     * Constructs a BuildingLimitExceededException with count details.
     *
     * @param currentCount the current number of buildings
     * @param maxAllowed   the maximum number of buildings permitted
     */
    public BuildingLimitExceededException(int currentCount, int maxAllowed) {
        super(String.format(
                "Building limit exceeded: village already has %d buildings (max allowed: %d).",
                currentCount, maxAllowed));
        this.currentCount = currentCount;
        this.maxAllowed = maxAllowed;
    }

    /**
     * Returns the current number of buildings in the village.
     *
     * @return the current building count
     */
    public int getCurrentCount() {
        return currentCount;
    }

    /**
     * Returns the maximum number of buildings allowed.
     *
     * @return the maximum allowed buildings
     */
    public int getMaxAllowed() {
        return maxAllowed;
    }
}
