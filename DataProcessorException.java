import java.io.Serializable;

/**
 * Custom exception for domain-specific errors in DataProcessor.
 * Implements Serializable for potential transmission/persistence of exceptions.
 */
public class DataProcessorException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    // Basic constructor with a message
    public DataProcessorException(String message) {
        super(message);
    }

    // Constructor for exception chaining: wraps a lower-level cause
    public DataProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that carries only a cause (useful for re-wrapping)
    public DataProcessorException(Throwable cause) {
        super(cause);
    }
}
