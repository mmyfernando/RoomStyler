import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_FILE = "designs.dat";

    // Save a list of designs to file
    public static void saveDesigns(List<Design> designs) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            out.writeObject(designs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load designs from file
    @SuppressWarnings("unchecked")
    public static List<Design> loadDesigns() {
        List<Design> designs = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            designs = (List<Design>) in.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, return empty list
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return designs;
    }
}