import java.util.ArrayList;
import java.util.List;

public class DesignManager {
    private static Design currentDesign;
    private static List<Design> savedDesigns = new ArrayList<>();

    public static void setCurrentDesign(Design design) {
        currentDesign = design;
    }

    public static Design getCurrentDesign() {
        return currentDesign;
    }

    public static void resetCurrentDesign() {
        currentDesign = null;
    }

    public static void saveDesign(Design design) {
        // Check if design already exists
        for (int i = 0; i < savedDesigns.size(); i++) {
            if (savedDesigns.get(i).getName().equals(design.getName())) {
                // Replace existing design
                savedDesigns.set(i, design);
                return;
            }
        }

        // Add new design
        savedDesigns.add(design);
    }

    public static List<Design> getSavedDesigns() {
        return savedDesigns;
    }

    public static void deleteDesign(String designName) {
        savedDesigns.removeIf(design -> design.getName().equals(designName));
    }
}