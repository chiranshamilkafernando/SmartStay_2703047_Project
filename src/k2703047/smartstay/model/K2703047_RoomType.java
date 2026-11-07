package k2703047.smartstay.model;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Enum representing available room types and their package specifications.
 * Based on Table 1 of the coursework brief:
 * - Standard: LKR 6,000 | Breakfast: No  | Extra Guest: LKR 1,000 | Tax: 10% | Base Capacity: 2
 * - Deluxe:   LKR 9,000 | Breakfast: Yes | Extra Guest: LKR 1,500 | Tax: 12% | Base Capacity: 2
 * - Suite:    LKR 14,000| Breakfast: Yes | Extra Guest: LKR 2,000 | Tax: 15% | Base Capacity: 3
 * - Family:   LKR 18,000| Breakfast: Yes | Extra Guest: LKR 2,500 | Tax: 15% | Base Capacity: 4
 */
public enum K2703047_RoomType {
    STANDARD("Standard", 6000.0, false, 1000.0, 0.10, 2),
    DELUXE("Deluxe", 9000.0, true, 1500.0, 0.12, 2),
    SUITE("Suite", 14000.0, true, 2000.0, 0.15, 3),
    FAMILY_SUITE("Family Suite", 18000.0, true, 2500.0, 0.15, 4);

    private final String displayName;
    private final double dailyRate;
    private final boolean complimentaryBreakfast;
    private final double extraGuestCharge;
    private final double taxRate;
    private final int standardCapacity;

    K2703047_RoomType(String displayName, double dailyRate, boolean complimentaryBreakfast,
                      double extraGuestCharge, double taxRate, int standardCapacity) {
        this.displayName = displayName;
        this.dailyRate = dailyRate;
        this.complimentaryBreakfast = complimentaryBreakfast;
        this.extraGuestCharge = extraGuestCharge;
        this.taxRate = taxRate;
        this.standardCapacity = standardCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public boolean hasComplimentaryBreakfast() {
        return complimentaryBreakfast;
    }

    public double getExtraGuestCharge() {
        return extraGuestCharge;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public int getStandardCapacity() {
        return standardCapacity;
    }

    @Override
    public String toString() {
        return displayName + " (LKR " + String.format("%,.0f", dailyRate) + "/night)";
    }
}
