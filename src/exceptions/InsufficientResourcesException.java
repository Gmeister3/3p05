package exceptions;

/**
 * Exception thrown when an operation cannot be completed due to insufficient resources.
 * <p>
 * This exception is raised when a player attempts to build, train, or upgrade
 * without having the required Gold, Iron, or Lumber.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class InsufficientResourcesException extends Exception {

    /** The type of resource that was insufficient. */
    private final String resourceType;

    /** The amount required for the operation. */
    private final double required;

    /** The amount currently available. */
    private final double available;

    /**
     * Constructs an InsufficientResourcesException with a detail message.
     *
     * @param message the detail message
     */
    public InsufficientResourcesException(String message) {
        super(message);
        this.resourceType = "Unknown";
        this.required = 0;
        this.available = 0;
    }

    /**
     * Constructs an InsufficientResourcesException with resource details.
     *
     * @param resourceType the type of resource that was insufficient (e.g., "Gold")
     * @param required     the amount of resource required
     * @param available    the amount of resource currently available
     */
    public InsufficientResourcesException(String resourceType, double required, double available) {
        super(String.format("Insufficient %s: required %.1f but only %.1f available.",
                resourceType, required, available));
        this.resourceType = resourceType;
        this.required = required;
        this.available = available;
    }

    /**
     * Returns the type of resource that was insufficient.
     *
     * @return the resource type string
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Returns the amount of the resource that was required.
     *
     * @return the required amount
     */
    public double getRequired() {
        return required;
    }

    /**
     * Returns the amount of the resource that was available.
     *
     * @return the available amount
     */
    public double getAvailable() {
        return available;
    }
}
