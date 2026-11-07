package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Family Suite Room Package implementation.
 * Specification:
 * - Daily Rate: LKR 18,000
 * - Complimentary Breakfast: Yes
 * - Extra Guest Charge: LKR 2,500 per guest per night
 * - Tax Rate: 15%
 * - Base Capacity: 4
 */
public class K2703047_FamilySuiteRoom extends K2703047_Room {

    public static final double DEFAULT_DAILY_RATE = 18000.0;
    public static final boolean COMPLIMENTARY_BREAKFAST = true;
    public static final double EXTRA_GUEST_CHARGE = 2500.0;
    public static final double TAX_RATE = 0.15;
    public static final int BASE_CAPACITY = 4;

    public K2703047_FamilySuiteRoom(String roomId, K2703047_BedType bedType) {
        super(roomId, K2703047_RoomType.FAMILY_SUITE, bedType, DEFAULT_DAILY_RATE, K2703047_RoomStatus.AVAILABLE, BASE_CAPACITY);
    }

    public K2703047_FamilySuiteRoom(String roomId, K2703047_BedType bedType, double dailyRate,
                                    K2703047_RoomStatus status, int baseCapacity) {
        super(roomId, K2703047_RoomType.FAMILY_SUITE, bedType, dailyRate, status, baseCapacity);
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
        return "Family Suite Package: LKR 18,000/night | Complimentary Breakfast Included | Extra: LKR 2,500/guest | Tax: 15%";
    }
}
