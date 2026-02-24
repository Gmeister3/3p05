import java.io.*;
import java.util.*;
import java.util.logging.*;

public class DataProcessor {

    private static final Logger logger = Logger.getLogger(DataProcessor.class.getName());

    private List<Integer> data = new ArrayList<>();

    // Exercise 1 & 2: Fail-fast validation with custom exception
    public void addValue(int value) throws DataProcessorException {
        if (value < 0) {
            throw new DataProcessorException("Value must be non-negative, got: " + value);
        }
        assert data.size() >= 0 : "Internal invariant violated: data list is in an inconsistent state";
        data.add(value);
    }

    public int getValue(int index) throws DataProcessorException {
        if (index < 0 || index >= data.size()) {
            throw new DataProcessorException(
                "Index " + index + " is out of bounds for size " + data.size());
        }
        return data.get(index);
    }

    // Exercise 3 & 4: try-with-resources and exception chaining with logging
    public void saveToFile(String filename) throws DataProcessorException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Integer i : data) {
                writer.write(i.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save data to file: " + filename, e);
            throw new DataProcessorException("Could not save data to file: " + filename, e);
        }
    }
}