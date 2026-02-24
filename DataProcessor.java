import java.io.*;
import java.util.*;

public class DataProcessor {
    private List<Integer> data = new ArrayList<>();

    // Exercise 1 & 2: Lack of validation or custom errors
    public void addValue(int value) {
        data.add(value); 
    }

    public int getValue(int index) {
        return data.get(index); // Potential IndexOutOfBoundsException
    }

    // Exercise 3 & 4: Manual resource management (old style)
    public void saveToFile(String filename) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            for (Integer i : data) {
                writer.write(i.toString());
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                writer.close(); // Manual closing is error-prone
            }
        }
    }
}