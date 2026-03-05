package exceptions;

// Thrown when an operation cannot be completed due to insufficient resources.
public class InsufficientResourcesException extends Exception {

    private final String resourceType;
    private final double required;
    private final double available;

    public InsufficientResourcesException(String message) {
        super(message);
        this.resourceType = "Unknown";
        this.required = 0;
        this.available = 0;
    }

    public InsufficientResourcesException(String resourceType, double required, double available) {
        super(String.format("Insufficient %s: required %.1f but only %.1f available.",
                resourceType, required, available));
        this.resourceType = resourceType;
        this.required = required;
        this.available = available;
    }

    public String getResourceType() {
        return resourceType;
    }

    public double getRequired() {
        return required;
    }

    public double getAvailable() {
        return available;
    }
}
