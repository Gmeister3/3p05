package exceptions;

// Thrown when an invalid or illegal game operation is attempted.
public class InvalidOperationException extends Exception {

    private final String operationName;

    public InvalidOperationException(String message) {
        super(message);
        this.operationName = "Unknown";
    }

    public InvalidOperationException(String operationName, String reason) {
        super(String.format("Invalid operation '%s': %s", operationName, reason));
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }
}
