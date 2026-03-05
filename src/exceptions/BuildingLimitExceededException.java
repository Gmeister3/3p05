package exceptions;

// Thrown when adding a building exceeds the village building limit.
public class BuildingLimitExceededException extends Exception {

    private final int currentCount;
    private final int maxAllowed;

    public BuildingLimitExceededException(String message) {
        super(message);
        this.currentCount = 0;
        this.maxAllowed = 0;
    }

    public BuildingLimitExceededException(int currentCount, int maxAllowed) {
        super(String.format(
                "Building limit exceeded: village already has %d buildings (max allowed: %d).",
                currentCount, maxAllowed));
        this.currentCount = currentCount;
        this.maxAllowed = maxAllowed;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}
