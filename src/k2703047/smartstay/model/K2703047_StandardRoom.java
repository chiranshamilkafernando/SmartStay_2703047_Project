package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Standard Room Package implementation.
 * Specification:
 * - Daily Rate: LKR 6,000
 * - Complimentary Breakfast: No
 * - Extra Guest Charge: LKR 1,000 per guest per night
 * - Tax Rate: 10%
 * - Base Capacity: 2
 */
public class K2703047_StandardRoom extends K2703047_Room {

    public static final double DEFAULT_DAILY_RATE = 6000.0;
    public static final boolean COMPLIMENTARY_BREAKFAST = false;
    public static final double EXTRA_GUEST_CHARGE = 1000.0;
    public static final double TAX_RATE = 0.10;
    public static final int BASE_CAPACITY = 2;

    public K2703047_StandardRoom(String roomId, K2703047_BedType bedType) {
        super(roomId, K2703047_RoomType.STANDARD, bedType, DEFAULT_DAILY_RATE, K2703047_RoomStatus.AVAILABLE, BASE_CAPACITY);
    }

    public K2703047_StandardRoom(String roomId, K2703047_BedType bedType, double dailyRate,
                                 K2703047_RoomStatus status, int baseCapacity) {
        super(roomId, K2703047_RoomType.STANDARD, bedType, dailyRate, status, baseCapacity);
    }

    @Override
    public double calculateTax(double netRoomCharge) {
        return netRoomCharge * TAX_RATE;
    }

    @Override
    public double getExtraGuestRate() {
        return EXTRA_GUEST_CHARGE;
    }

    @Override
    public boolean hasComplimentaryBreakfast() {
        return COMPLIMENTARY_BREAKFAST;
    }

    @Override
    public double getTaxRate() {
        return TAX_RATE;
    }

    @Override
    public String getRoomPackageDescription() {
        return "Standard Package: LKR 6,000/night | No Breakfast | Extra: LKR 1,000/guest | Tax: 10%";
    }
}
