package exceptions;

/**
 * Exception thrown when an invalid or illegal game operation is attempted.
 * <p>
 * Examples include attacking a village with an empty army, attempting to train
 * units when the army is at maximum capacity, or performing an action that is
 * not allowed in the current game state.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class InvalidOperationException extends Exception {

    /** The name of the operation that failed. */
    private final String operationName;

    /**
     * Constructs an InvalidOperationException with a detail message.
     *
     * @param message the detail message describing what went wrong
     */
    public InvalidOperationException(String message) {
        super(message);
        this.operationName = "Unknown";
    }

    /**
     * Constructs an InvalidOperationException with an operation name and reason.
     *
     * @param operationName the name of the invalid operation
     * @param reason        the reason the operation is invalid
     */
    public InvalidOperationException(String operationName, String reason) {
        super(String.format("Invalid operation '%s': %s", operationName, reason));
        this.operationName = operationName;
    }

    /**
     * Returns the name of the operation that was invalid.
     *
     * @return the operation name
     */
    public String getOperationName() {
        return operationName;
    }
}
