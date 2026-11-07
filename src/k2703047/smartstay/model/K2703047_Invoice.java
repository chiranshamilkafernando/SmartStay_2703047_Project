package k2703047.smartstay.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Represents a detailed invoice generated for a SmartStay reservation.
 * Implements the 5-step billing calculation algorithm:
 * 1. Base room charge = Daily Rate * Number of Nights
 * 2. Extra guest charges if guests > base capacity
 * 3. Discount applied (12% on base room charge for stays >= 5 nights)
 * 4. Room-type specific tax added
 * 5. Refundable booking advance (LKR 10,000) deducted
 */
public class K2703047_Invoice {
    public static final double LONG_STAY_DISCOUNT_PERCENTAGE = 12.0;
    public static final long LONG_STAY_MIN_NIGHTS = 5;

    private final String invoiceNumber;
    private final LocalDateTime invoiceDateTime;
    private final K2703047_Reservation reservation;

    // Billing calculation breakdown attributes
    private final double dailyRate;
    private final long numberOfNights;
    private final double baseRoomCharge;
    private final int numberOfGuests;
    private final int roomCapacity;
    private final int extraGuestCount;
    private final double extraGuestRate;
    private final double extraGuestCharges;
    private final boolean discountApplied;
    private final double discountRate;
    private final double discountAmount;
    private final double netRoomCharge;
    private final double taxRate;
    private final double taxAmount;
    private final double totalGrossCharge;
    private final double bookingAdvanceDeducted;
    private final double finalPayableAmount;

    /**
     * Constructs and computes the full invoice for a given reservation.
     */
    public K2703047_Invoice(K2703047_Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        this.reservation = reservation;
        this.invoiceNumber = "INV-" + reservation.getBookingId();
        this.invoiceDateTime = LocalDateTime.now();

        K2703047_Room room = reservation.getRoom();

        // 1. Base room charge
        this.dailyRate = room.getDailyRate();
        this.numberOfNights = reservation.getNumberOfNights();
        this.baseRoomCharge = this.dailyRate * this.numberOfNights;

        // 2. Extra guest charges
        this.numberOfGuests = reservation.getNumberOfGuests();
        this.roomCapacity = room.getBaseCapacity();
        this.extraGuestCount = Math.max(0, this.numberOfGuests - this.roomCapacity);
        this.extraGuestRate = room.getExtraGuestRate();
        this.extraGuestCharges = room.calculateExtraGuestCharge(this.numberOfGuests, this.numberOfNights);

        // 3. Discount calculation (12% on base room charge if nights >= 5)
        if (this.numberOfNights >= LONG_STAY_MIN_NIGHTS) {
            this.discountApplied = true;
            this.discountRate = LONG_STAY_DISCOUNT_PERCENTAGE;
            this.discountAmount = (this.baseRoomCharge * LONG_STAY_DISCOUNT_PERCENTAGE) / 100.0;
        } else {
            this.discountApplied = false;
            this.discountRate = 0.0;
            this.discountAmount = 0.0;
        }

        // Net charges before tax
        this.netRoomCharge = (this.baseRoomCharge - this.discountAmount) + this.extraGuestCharges;

        // 4. Tax calculation (polymorphic room tax rate)
        this.taxRate = room.getTaxRate();
        this.taxAmount = room.calculateTax(this.netRoomCharge);
        this.totalGrossCharge = this.netRoomCharge + this.taxAmount;

        // 5. Booking advance deduction
        this.bookingAdvanceDeducted = reservation.getAdvancePaid();
        this.finalPayableAmount = Math.max(0.0, this.totalGrossCharge - this.bookingAdvanceDeducted);
    }

    /**
     * Generates a beautifully formatted text invoice conforming to all brief criteria.
     */
    public String generateInvoiceText() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        K2703047_Room room = reservation.getRoom();
        K2703047_Guest guest = reservation.getGuest();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                               SMARTSTAY HOTEL CHAIN                                    \n");
        sb.append("                          OFFICIAL RESERVATION INVOICE                                  \n");
        sb.append("========================================================================================\n");
        sb.append(String.format(" Invoice No      : %-30s Date/Time : %s\n", invoiceNumber, invoiceDateTime.format(dtf)));
        sb.append(String.format(" Booking ID      : %-30s Status    : %s\n", reservation.getBookingId(), reservation.getStatus()));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" GUEST DETAILS\n");
        sb.append(String.format(" Full Name       : %-30s NIC/Passport : %s\n", guest.getFullName(), guest.getNicOrPassport()));
        sb.append(String.format(" Contact Number  : %-30s Email        : %s\n", guest.getContactNumber(), guest.getEmailAddress()));
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" ROOM & STAY DETAILS\n");
        sb.append(String.format(" Room ID         : %-30s Room Type    : %s\n", room.getRoomId(), room.getRoomType().getDisplayName()));
        sb.append(String.format(" Bed Type        : %-30s Daily Rate   : LKR %,.2f\n", room.getBedType().getDisplayName(), room.getDailyRate()));
        sb.append(String.format(" Check-In Date   : %-30s Check-Out    : %s\n", reservation.getCheckInDate().format(df), reservation.getCheckOutDate().format(df)));
        sb.append(String.format(" Duration        : %d Night(s)                   Guests       : %d (Base Capacity: %d)\n",
                numberOfNights, numberOfGuests, roomCapacity));
        sb.append(String.format(" Breakfast       : %s\n", room.hasComplimentaryBreakfast() ? "Complimentary Included" : "Not Included"));
        sb.append("========================================================================================\n");
        sb.append(" BILLING BREAKDOWN (LKR)\n");
        sb.append("========================================================================================\n");
        sb.append(String.format(" 1. Base Room Charge (%d night(s) @ LKR %,.2f/night)         : LKR %,14.2f\n",
                numberOfNights, dailyRate, baseRoomCharge));

        if (extraGuestCount > 0) {
            sb.append(String.format(" 2. Extra Guest Charge (%d extra @ LKR %,.2f x %d nights)     : LKR %,14.2f\n",
                    extraGuestCount, extraGuestRate, numberOfNights, extraGuestCharges));
        } else {
            sb.append(" 2. Extra Guest Charge (0 extra guests)                             : LKR           0.00\n");
        }

        if (discountApplied) {
            sb.append(String.format(" 3. Stay Discount (12%% discount for %d nights >= 5)                : -LKR %,13.2f\n",
                    numberOfNights, discountAmount));
        } else {
            sb.append(" 3. Stay Discount (< 5 nights, 0% discount)                         : LKR           0.00\n");
        }

        sb.append(String.format("    --------------------------------------------------------------------\n"));
        sb.append(String.format("    Net Charge Subject to Tax                                       : LKR %,14.2f\n", netRoomCharge));
        sb.append(String.format(" 4. Tax Added (%s - %.0f%%)                                         : +LKR %,13.2f\n",
                room.getRoomType().getDisplayName(), taxRate * 100, taxAmount));
        sb.append(String.format("    --------------------------------------------------------------------\n"));
        sb.append(String.format("    Total Gross Booking Cost                                        : LKR %,14.2f\n", totalGrossCharge));
        sb.append(String.format(" 5. Booking Advance Deducted (Paid at Reservation)                 : -LKR %,13.2f\n", bookingAdvanceDeducted));
        sb.append("========================================================================================\n");
        sb.append(String.format(" FINAL PAYABLE AMOUNT AT CHECK-IN                                   : LKR %,14.2f\n", finalPayableAmount));
        sb.append("========================================================================================\n");
        sb.append(" Thank you for choosing SmartStay Hotels Sri Lanka!\n");
        sb.append(" Note: Booking advance is refundable if cancelled 24+ hours prior to check-in.\n");
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    /**
     * Exports the generated invoice to a text file.
     */
    public boolean exportToFile(String targetPath) {
        try {
            File file = new File(targetPath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(generateInvoiceText());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving invoice to file: " + e.getMessage());
            return false;
        }
    }

    // Getters for all billing parameters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDateTime getInvoiceDateTime() {
        return invoiceDateTime;
    }

    public K2703047_Reservation getReservation() {
        return reservation;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public double getBaseRoomCharge() {
        return baseRoomCharge;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public int getExtraGuestCount() {
        return extraGuestCount;
    }

    public double getExtraGuestRate() {
        return extraGuestRate;
    }

    public double getExtraGuestCharges() {
        return extraGuestCharges;
    }

    public boolean isDiscountApplied() {
        return discountApplied;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getNetRoomCharge() {
        return netRoomCharge;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getTotalGrossCharge() {
        return totalGrossCharge;
    }

    public double getBookingAdvanceDeducted() {
        return bookingAdvanceDeducted;
    }

    public double getFinalPayableAmount() {
        return finalPayableAmount;
    }
}
