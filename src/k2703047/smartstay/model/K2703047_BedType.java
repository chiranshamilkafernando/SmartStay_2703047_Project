package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Enum representing bed types supported by rooms.
 */
public enum K2703047_BedType {
    SINGLE("Single Bed", 1),
    DOUBLE("Double Bed", 2),
    QUEEN("Queen Bed", 2),
    KING("King Bed", 2);

    private final String displayName;
    private final int bedCapacity;

    K2703047_BedType(String displayName, int bedCapacity) {
        this.displayName = displayName;
        this.bedCapacity = bedCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBedCapacity() {
        return bedCapacity;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
