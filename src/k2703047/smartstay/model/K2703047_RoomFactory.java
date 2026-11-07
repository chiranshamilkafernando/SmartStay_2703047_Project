package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Factory Method Pattern implementation for creating Room instances.
 * Promotes loose coupling, single responsibility, and extensible room creation.
 */
public class K2703047_RoomFactory {

    /**
     * Creates a room instance based on the specified room type using default rates.
     */
    public static K2703047_Room createRoom(String roomId, K2703047_RoomType roomType, K2703047_BedType bedType) {
        return createRoom(roomId, roomType, bedType, roomType.getDailyRate(), K2703047_RoomStatus.AVAILABLE, roomType.getStandardCapacity());
    }

    /**
     * Creates a room instance with full custom attributes.
     */
    public static K2703047_Room createRoom(String roomId, K2703047_RoomType roomType, K2703047_BedType bedType,
                                          double dailyRate, K2703047_RoomStatus status, int baseCapacity) {
        switch (roomType) {
            case STANDARD:
                return new K2703047_StandardRoom(roomId, bedType, dailyRate, status, baseCapacity);
            case DELUXE:
                return new K2703047_DeluxeRoom(roomId, bedType, dailyRate, status, baseCapacity);
            case SUITE:
                return new K2703047_SuiteRoom(roomId, bedType, dailyRate, status, baseCapacity);
            case FAMILY_SUITE:
                return new K2703047_FamilySuiteRoom(roomId, bedType, dailyRate, status, baseCapacity);
            default:
                throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
    }
}
