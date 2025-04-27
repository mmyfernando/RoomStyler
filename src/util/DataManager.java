import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_FILE = "designs.dat";

    public static void saveDesigns(List<Design> designs) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(DATA_FILE)))) {
            out.writeObject(designs);
            System.out.println("Designs saved successfully to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving designs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Design> loadDesigns() {
        List<Design> designs = new ArrayList<>();
        File file = new File(DATA_FILE);

        // Return empty list if file doesn't exist yet
        if (!file.exists()) {
            System.out.println("No designs file found. Creating a new one when designs are saved.");
            return designs;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(DATA_FILE)))) {
            designs = (List<Design>) in.readObject();
            System.out.println("Loaded " + designs.size() + " designs from " + DATA_FILE);
        } catch (FileNotFoundException e) {
            System.err.println("Designs file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading designs file: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Error deserializing designs: " + e.getMessage());
            e.printStackTrace();
        }

        // Return empty list if there was an error
        return designs != null ? designs : new ArrayList<>();
    }
}