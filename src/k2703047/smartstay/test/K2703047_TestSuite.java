package k2703047.smartstay.test;

import k2703047.smartstay.model.K2703047_BedType;
import k2703047.smartstay.model.K2703047_FamilySuiteRoom;
import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_ReservationStatus;
import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomFactory;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;
import k2703047.smartstay.service.K2703047_HotelSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Comprehensive Automated Test Suite validating Normal, Boundary, and Invalid Test Cases.
 * Aligned with Module Learning Outcomes (LO2, LO5, LO6).
 */
public class K2703047_TestSuite {

    public static class TestResult {
        public final String testId;
        public final String category;
        public final String description;
        public final boolean passed;
        public final String details;

        public TestResult(String testId, String category, String description, boolean passed, String details) {
            this.testId = testId;
            this.category = category;
            this.description = description;
            this.passed = passed;
            this.details = details;
        }
    }

    public static void main(String[] args) {
        runAllTests();
    }

    public static List<TestResult> runAllTests() {
        List<TestResult> results = new ArrayList<>();
        int passedCount = 0;

        System.out.println("==========================================================================================");
        System.out.println("            SMARTSTAY HOTEL BOOKING SYSTEM - COMPREHENSIVE TEST SUITE                     ");
        System.out.println("       Kingston University CI6115 | Student ID: 2703047 | Module Learning Outcome LO5     ");
        System.out.println("==========================================================================================");

        // ==========================================
        // SECTION 1: NORMAL TEST CASES
        // ==========================================
        // TC01: Customer Registration
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            boolean ok = hs.registerGuest("987654321V", "Amara Silva", "+94712345678", "amara.silva@gmail.com");
            results.add(new TestResult("TC01", "Normal", "Guest registration with valid details", ok, "Guest stored and retrievable"));
        } catch (Exception e) {
            results.add(new TestResult("TC01", "Normal", "Guest registration with valid details", false, e.getMessage()));
        }

        // TC02: Make Reservation for Standard Room
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-101", today.plusDays(3), today.plusDays(6), 2);
            boolean ok = res != null && res.getBookingId().startsWith("BK-") && res.getNumberOfNights() == 3;
            results.add(new TestResult("TC02", "Normal", "Create valid 3-night reservation", ok, "Booking created with ID " + res.getBookingId()));
        } catch (Exception e) {
            results.add(new TestResult("TC02", "Normal", "Create valid 3-night reservation", false, e.getMessage()));
        }

        // TC03: 5-Step Billing Calculation (Deluxe Room, 3 nights, no discount)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            // Room D-202 (Deluxe, 9000/night, capacity 2, tax 12%)
            K2703047_Reservation res = hs.makeReservation("199512345678", "D-202", today.plusDays(4), today.plusDays(7), 2);
            K2703047_Invoice inv = hs.generateInvoice(res.getBookingId());

            // Expected calculations:
            // Base: 9,000 * 3 = 27,000
            // Extra: 0
            // Discount: 0 (< 5 nights)
            // Net: 27,000
            // Tax: 12% of 27,000 = 3,240
            // Total Gross: 30,240
            // Advance: 10,000
            // Final Payable: 20,240
            boolean ok = Math.abs(inv.getBaseRoomCharge() - 27000.0) < 0.01 &&
                         Math.abs(inv.getTaxAmount() - 3240.0) < 0.01 &&
                         Math.abs(inv.getFinalPayableAmount() - 20240.0) < 0.01;
            results.add(new TestResult("TC03", "Normal", "5-Step billing calculation for Deluxe Room", ok,
                    String.format("Base: LKR %,.2f | Tax: LKR %,.2f | Final: LKR %,.2f",
                            inv.getBaseRoomCharge(), inv.getTaxAmount(), inv.getFinalPayableAmount())));
        } catch (Exception e) {
            results.add(new TestResult("TC03", "Normal", "5-Step billing calculation for Deluxe Room", false, e.getMessage()));
        }

        // TC04: Long-Stay Discount (>= 5 nights => 12% discount)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            // Standard Room R-102 (6000/night, tax 10%), 6 nights
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-102", today.plusDays(3), today.plusDays(9), 2);
            K2703047_Invoice inv = hs.generateInvoice(res.getBookingId());

            // Base = 6,000 * 6 = 36,000
            // Discount = 12% of 36,000 = 4,320
            // Net = 36,000 - 4,320 = 31,680
            // Tax = 10% of 31,680 = 3,168
            // Total = 34,848 - 10,000 = 24,848
            boolean ok = inv.isDiscountApplied() &&
                         Math.abs(inv.getDiscountAmount() - 4320.0) < 0.01 &&
                         Math.abs(inv.getFinalPayableAmount() - 24848.0) < 0.01;
            results.add(new TestResult("TC04", "Normal", "12% long-stay discount applied for 6 nights", ok,
                    String.format("Base: 36,000 | Discount: LKR %,.2f | Payable: LKR %,.2f", inv.getDiscountAmount(), inv.getFinalPayableAmount())));
        } catch (Exception e) {
            results.add(new TestResult("TC04", "Normal", "12% long-stay discount applied for 6 nights", false, e.getMessage()));
        }

        // TC05: Extra Guest Charge Calculation
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            // Suite Room S-301 (14,000/night, capacity 3, extra guest 2,000/night, tax 15%), 2 nights, 5 guests (2 extra)
            K2703047_Reservation res = hs.makeReservation("199512345678", "S-301", today.plusDays(3), today.plusDays(5), 5);
            K2703047_Invoice inv = hs.generateInvoice(res.getBookingId());

            // Base = 14,000 * 2 = 28,000
            // Extra = 2 extra guests * 2,000 * 2 nights = 8,000
            // Net = 28,000 + 8,000 = 36,000
            // Tax = 15% of 36,000 = 5,400
            // Total = 41,400 - 10,000 = 31,400
            boolean ok = inv.getExtraGuestCount() == 2 &&
                         Math.abs(inv.getExtraGuestCharges() - 8000.0) < 0.01 &&
                         Math.abs(inv.getFinalPayableAmount() - 31400.0) < 0.01;
            results.add(new TestResult("TC05", "Normal", "Extra guest charge calculation (2 extra guests)", ok,
                    String.format("Extra guests: %d | Extra charge: LKR %,.2f", inv.getExtraGuestCount(), inv.getExtraGuestCharges())));
        } catch (Exception e) {
            results.add(new TestResult("TC05", "Normal", "Extra guest charge calculation (2 extra guests)", false, e.getMessage()));
        }

        // TC06: Search Available Rooms by Room Type and Dates
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            List<K2703047_Room> suites = hs.searchAvailableRooms(K2703047_RoomType.SUITE, today.plusDays(3), today.plusDays(5));
            boolean ok = !suites.isEmpty() && suites.get(0).getRoomType() == K2703047_RoomType.SUITE;
            results.add(new TestResult("TC06", "Normal", "Search available rooms by type and date", ok, "Found " + suites.size() + " suite(s)"));
        } catch (Exception e) {
            results.add(new TestResult("TC06", "Normal", "Search available rooms by type and date", false, e.getMessage()));
        }

        // TC07: TreeMap Red-Black Tree Date Index Retrieval
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            LocalDate checkIn = today.plusDays(10);
            hs.makeReservation("199512345678", "R-101", checkIn, checkIn.plusDays(2), 2);
            List<K2703047_Reservation> onDate = hs.getReservationsByCheckInDate(checkIn);
            boolean ok = !onDate.isEmpty() && onDate.get(0).getCheckInDate().equals(checkIn);
            results.add(new TestResult("TC07", "Normal", "View reservations by check-in date (TreeMap)", ok, "Indexed via TreeMap in O(log N)"));
        } catch (Exception e) {
            results.add(new TestResult("TC07", "Normal", "View reservations by check-in date (TreeMap)", false, e.getMessage()));
        }

        // TC08: Binary Search Algorithm by Booking ID
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            K2703047_Reservation res = hs.makeReservation("199512345678", "FS-401", today.plusDays(7), today.plusDays(10), 4);
            K2703047_Reservation found = hs.searchReservationBinary(res.getBookingId());
            boolean ok = found != null && found.getBookingId().equals(res.getBookingId());
            results.add(new TestResult("TC08", "Normal", "Binary Search on sorted reservations by ID", ok, "Located " + res.getBookingId() + " in O(log N) time"));
        } catch (Exception e) {
            results.add(new TestResult("TC08", "Normal", "Binary Search on sorted reservations by ID", false, e.getMessage()));
        }

        // ==========================================
        // SECTION 2: BOUNDARY TEST CASES
        // ==========================================
        // TC09: Advance Booking Boundary - Exactly 2 days in advance (Must SUCCEED)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-101", today.plusDays(2), today.plusDays(4), 2);
            boolean ok = res != null;
            results.add(new TestResult("TC09", "Boundary", "Advance booking boundary: exactly 2 days ahead", ok, "Accepted (boundary = 2 days)"));
        } catch (Exception e) {
            results.add(new TestResult("TC09", "Boundary", "Advance booking boundary: exactly 2 days ahead", false, e.getMessage()));
        }

        // TC10: Advance Booking Boundary - 1 day in advance (Must FAIL)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            hs.makeReservation("199512345678", "R-101", today.plusDays(1), today.plusDays(3), 2);
            results.add(new TestResult("TC10", "Boundary", "Advance booking boundary: 1 day ahead (insufficient)", false, "Should have failed"));
        } catch (IllegalArgumentException e) {
            results.add(new TestResult("TC10", "Boundary", "Advance booking boundary: 1 day ahead (insufficient)", true, "Correctly rejected: " + e.getMessage()));
        } catch (Exception e) {
            results.add(new TestResult("TC10", "Boundary", "Advance booking boundary: 1 day ahead (insufficient)", false, e.getMessage()));
        }

        // TC11: Discount Boundary - Exactly 5 nights stay (12% discount MUST apply)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-102", today.plusDays(3), today.plusDays(8), 2); // 5 nights
            K2703047_Invoice inv = hs.generateInvoice(res.getBookingId());
            boolean ok = inv.getNumberOfNights() == 5 && inv.isDiscountApplied() && inv.getDiscountRate() == 12.0;
            results.add(new TestResult("TC11", "Boundary", "Discount boundary: exactly 5 nights stay", ok, "12% discount correctly triggered"));
        } catch (Exception e) {
            results.add(new TestResult("TC11", "Boundary", "Discount boundary: exactly 5 nights stay", false, e.getMessage()));
        }

        // TC12: Discount Boundary - 4 nights stay (12% discount MUST NOT apply)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-102", today.plusDays(3), today.plusDays(7), 2); // 4 nights
            K2703047_Invoice inv = hs.generateInvoice(res.getBookingId());
            boolean ok = inv.getNumberOfNights() == 4 && !inv.isDiscountApplied() && inv.getDiscountAmount() == 0.0;
            results.add(new TestResult("TC12", "Boundary", "Discount boundary: 4 nights stay (no discount)", ok, "0% discount correctly assigned"));
        } catch (Exception e) {
            results.add(new TestResult("TC12", "Boundary", "Discount boundary: 4 nights stay (no discount)", false, e.getMessage()));
        }

        // TC13: Cancellation Boundary - Exactly 25 hours before check-in date (Must SUCCEED)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate checkIn = LocalDate.now().plusDays(5);
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-102", checkIn, checkIn.plusDays(2), 2);

            // Simulation: Cancellation requested 25 hours before check-in date start
            LocalDateTime simTime = checkIn.atStartOfDay().minusHours(25);
            boolean cancelled = hs.cancelReservationWithCustomTime(res.getBookingId(), simTime);
            boolean ok = cancelled && res.getStatus() == K2703047_ReservationStatus.CANCELLED && res.isAdvanceRefunded();
            results.add(new TestResult("TC13", "Boundary", "Cancellation boundary: 25 hours before check-in", ok, "Cancellation accepted and advance refunded"));
        } catch (Exception e) {
            results.add(new TestResult("TC13", "Boundary", "Cancellation boundary: 25 hours before check-in", false, e.getMessage()));
        }

        // TC14: Cancellation Boundary - 23 hours before check-in date (Must FAIL)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate checkIn = LocalDate.now().plusDays(5);
            K2703047_Reservation res = hs.makeReservation("199512345678", "R-102", checkIn, checkIn.plusDays(2), 2);

            // Simulation: Cancellation requested 23 hours before check-in date start (within 24 hours!)
            LocalDateTime simTime = checkIn.atStartOfDay().minusHours(23);
            boolean cancelled = hs.cancelReservationWithCustomTime(res.getBookingId(), simTime);
            boolean ok = !cancelled && res.getStatus() == K2703047_ReservationStatus.CONFIRMED;
            results.add(new TestResult("TC14", "Boundary", "Cancellation boundary: 23 hours before check-in (within 24h)", ok, "Cancellation correctly rejected"));
        } catch (Exception e) {
            results.add(new TestResult("TC14", "Boundary", "Cancellation boundary: 23 hours before check-in (within 24h)", false, e.getMessage()));
        }

        // TC15: Capacity Boundary - Guest count equal to base capacity (0 extra guest charge)
        try {
            K2703047_Room room = K2703047_RoomFactory.createRoom("R-TEST", K2703047_RoomType.STANDARD, K2703047_BedType.DOUBLE);
            double extra = room.calculateExtraGuestCharge(room.getBaseCapacity(), 3);
            boolean ok = (extra == 0.0);
            results.add(new TestResult("TC15", "Boundary", "Capacity boundary: guests = capacity (2 guests in Standard)", ok, "Extra charge = LKR 0.00"));
        } catch (Exception e) {
            results.add(new TestResult("TC15", "Boundary", "Capacity boundary: guests = capacity", false, e.getMessage()));
        }

        // TC16: Capacity Boundary - Guest count = base capacity + 1 (Exactly 1 extra guest charged)
        try {
            K2703047_Room room = K2703047_RoomFactory.createRoom("R-TEST", K2703047_RoomType.STANDARD, K2703047_BedType.DOUBLE);
            // capacity is 2, 3 guests for 2 nights => 1 extra guest * 1000 * 2 = 2000
            double extra = room.calculateExtraGuestCharge(room.getBaseCapacity() + 1, 2);
            boolean ok = (extra == 2000.0);
            results.add(new TestResult("TC16", "Boundary", "Capacity boundary: guests = capacity + 1 (3 guests, 2 nights)", ok, "Extra charge = LKR 2,000.00"));
        } catch (Exception e) {
            results.add(new TestResult("TC16", "Boundary", "Capacity boundary: guests = capacity + 1", false, e.getMessage()));
        }

        // ==========================================
        // SECTION 3: INVALID / ERROR TEST CASES
        // ==========================================
        // TC17: Booking room under maintenance (Must FAIL)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            // Room R-103 is seeded as UNDER_MAINTENANCE
            hs.makeReservation("199512345678", "R-103", today.plusDays(3), today.plusDays(5), 2);
            results.add(new TestResult("TC17", "Invalid", "Book room with status UNDER_MAINTENANCE", false, "Should have been rejected"));
        } catch (IllegalStateException e) {
            results.add(new TestResult("TC17", "Invalid", "Book room with status UNDER_MAINTENANCE", true, "Correctly rejected: " + e.getMessage()));
        } catch (Exception e) {
            results.add(new TestResult("TC17", "Invalid", "Book room with status UNDER_MAINTENANCE", false, e.getMessage()));
        }

        // TC18: Double booking on overlapping dates (Must FAIL via Conflict Algorithm)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            LocalDate in1 = today.plusDays(5);
            LocalDate out1 = in1.plusDays(4); // Day 5 to Day 9
            hs.makeReservation("199512345678", "R-101", in1, out1, 2);

            // Attempt overlapping booking: Day 6 to Day 8
            hs.makeReservation("N87654321", "R-101", in1.plusDays(1), in1.plusDays(3), 2);
            results.add(new TestResult("TC18", "Invalid", "Double-booking overlapping date range", false, "Should have rejected overlapping interval"));
        } catch (IllegalStateException e) {
            results.add(new TestResult("TC18", "Invalid", "Double-booking overlapping date range", true, "Conflict detected and prevented: " + e.getMessage()));
        } catch (Exception e) {
            results.add(new TestResult("TC18", "Invalid", "Double-booking overlapping date range", false, e.getMessage()));
        }

        // TC19: Check-out date before or equal to check-in date (Must FAIL)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            hs.makeReservation("199512345678", "R-101", today.plusDays(5), today.plusDays(4), 2);
            results.add(new TestResult("TC19", "Invalid", "Check-out date before check-in date", false, "Should have been rejected"));
        } catch (IllegalArgumentException e) {
            results.add(new TestResult("TC19", "Invalid", "Check-out date before check-in date", true, "Correctly rejected: " + e.getMessage()));
        } catch (Exception e) {
            results.add(new TestResult("TC19", "Invalid", "Check-out date before check-in date", false, e.getMessage()));
        }

        // TC20: Invalid Customer Email format (Must FAIL)
        try {
            new K2703047_Guest("199512345678", "Test Guest", "+94771234567", "invalid-email-address");
            results.add(new TestResult("TC20", "Invalid", "Guest registration with malformed email", false, "Should have failed email regex"));
        } catch (IllegalArgumentException e) {
            results.add(new TestResult("TC20", "Invalid", "Guest registration with malformed email", true, "Regex caught invalid email format"));
        } catch (Exception e) {
            results.add(new TestResult("TC20", "Invalid", "Guest registration with malformed email", false, e.getMessage()));
        }

        // TC21: Invalid Customer Phone format (Must FAIL)
        try {
            new K2703047_Guest("199512345678", "Test Guest", "123", "test@domain.com");
            results.add(new TestResult("TC21", "Invalid", "Guest registration with malformed phone (<9 digits)", false, "Should have failed phone validation"));
        } catch (IllegalArgumentException e) {
            results.add(new TestResult("TC21", "Invalid", "Guest registration with malformed phone (<9 digits)", true, "Phone validation caught invalid length"));
        } catch (Exception e) {
            results.add(new TestResult("TC21", "Invalid", "Guest registration with malformed phone (<9 digits)", false, e.getMessage()));
        }

        // TC22: Removing room with active reservations (Must FAIL)
        try {
            K2703047_HotelSystem hs = new K2703047_HotelSystem();
            LocalDate today = LocalDate.now();
            hs.makeReservation("199512345678", "R-102", today.plusDays(4), today.plusDays(7), 2);
            hs.removeRoom("R-102"); // Should fail because active reservation exists
            results.add(new TestResult("TC22", "Invalid", "Remove room with active reservation", false, "Should have thrown IllegalStateException"));
        } catch (IllegalStateException e) {
            results.add(new TestResult("TC22", "Invalid", "Remove room with active reservation", true, "Safeguard triggered: " + e.getMessage()));
        } catch (Exception e) {
            results.add(new TestResult("TC22", "Invalid", "Remove room with active reservation", false, e.getMessage()));
        }

        // Print Test Results Summary Table
        System.out.println(String.format("%-6s | %-9s | %-45s | %-8s | %s", "ID", "Category", "Description", "Result", "Notes/Output"));
        System.out.println("-".repeat(110));
        for (TestResult tr : results) {
            if (tr.passed) passedCount++;
            String status = tr.passed ? "[PASS]" : "[FAIL]";
            System.out.println(String.format("%-6s | %-9s | %-45s | %-8s | %s",
                    tr.testId, tr.category, tr.description, status, tr.details));
        }
        System.out.println("=".repeat(110));
        System.out.println(String.format("TOTAL TESTS: %d | PASSED: %d | FAILED: %d | SUCCESS RATE: %.1f%%",
                results.size(), passedCount, (results.size() - passedCount), (passedCount * 100.0 / results.size())));
        System.out.println("==========================================================================================\n");

        return results;
    }
}
