package exceptions;

// Thrown when attempting to upgrade a building or unit beyond its maximum level.
public class MaxLevelReachedException extends Exception {

    private final String entityName;
    private final int maxLevel;

    public MaxLevelReachedException(String message) {
        super(message);
        this.entityName = "Unknown";
        this.maxLevel = 0;
    }

    public MaxLevelReachedException(String entityName, int maxLevel) {
        super(String.format("%s has already reached its maximum level of %d.", entityName, maxLevel));
        this.entityName = entityName;
        this.maxLevel = maxLevel;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
