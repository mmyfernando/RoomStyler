public enum FurnitureType {
    CHAIR("Chair"),
    DINING_TABLE("Dining Table"),
    SIDE_TABLE("Side Table"),
    ;

    private final String displayName;

    FurnitureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}