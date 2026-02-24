import java.util.logging.*;

/**
 * Main class demonstrating deep call-stack exception propagation.
 * An exception thrown in a deeply nested method bubbles up to a
 * high-level handler, preserving the full chain of context.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            highLevelOperation();
        } catch (DataProcessorException e) {
            logger.log(Level.SEVERE, "High-level handler caught exception", e);
            System.err.println("Caught at top level: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause());
            }
        }
    }

    // High-level method that delegates work down the call stack
    private static void highLevelOperation() throws DataProcessorException {
        DataProcessor processor = new DataProcessor();
        midLevelOperation(processor);
    }

    // Mid-level method adds valid data then triggers an error scenario
    private static void midLevelOperation(DataProcessor processor) throws DataProcessorException {
        try {
            processor.addValue(10);
            processor.addValue(20);
            // Trigger a deeply nested failure
            deeplyNestedOperation(processor);
        } catch (DataProcessorException e) {
            // Re-throw with additional context via exception chaining
            throw new DataProcessorException("Mid-level operation failed", e);
        }
    }

    // Deeply nested method that deliberately triggers a validation error
    private static void deeplyNestedOperation(DataProcessor processor) throws DataProcessorException {
        // Attempt to add an invalid (negative) value — fail-fast validation fires here
        processor.addValue(-1);
    }
}
