package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Enum representing room availability statuses:
 * Available, Reserved, Under Maintenance
 */
public enum K2703047_RoomStatus {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    UNDER_MAINTENANCE("Under Maintenance");

    private final String displayName;

    K2703047_RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
