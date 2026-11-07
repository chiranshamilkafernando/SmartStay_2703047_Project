package k2703047.smartstay.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Represents a Room Reservation in the SmartStay system.
 * Enforces business rules:
 * 1. Advance payment of LKR 10,000 recorded.
 * 2. Check-in must be at least 2 days after booking date.
 * 3. Cannot be cancelled within 24 hours of check-in date.
 * 4. Half-open date interval overlap detection [checkIn, checkOut).
 */
public class K2703047_Reservation implements Comparable<K2703047_Reservation> {
    public static final double BOOKING_ADVANCE_AMOUNT = 10000.0;
    public static final int MIN_ADVANCE_DAYS = 2;
    public static final int CANCELLATION_DEADLINE_HOURS = 24;

    private String bookingId;
    private K2703047_Guest guest;
    private K2703047_Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private LocalDateTime bookingTimestamp;
    private double advancePaid;
    private boolean advanceRefunded;
    private K2703047_ReservationStatus status;

    /**
     * Constructs and initializes a reservation.
     */
    public K2703047_Reservation(String bookingId, K2703047_Guest guest, K2703047_Room room,
                                LocalDate checkInDate, LocalDate checkOutDate,
                                int numberOfGuests, LocalDateTime bookingTimestamp) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty.");
        }
        this.guest = Objects.requireNonNull(guest, "Guest information is required.");
        this.room = Objects.requireNonNull(room, "Room assignment is required.");
        this.bookingTimestamp = (bookingTimestamp != null) ? bookingTimestamp : LocalDateTime.now();

        validateDates(checkInDate, checkOutDate, this.bookingTimestamp.toLocalDate());
        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }

        this.bookingId = bookingId.trim().toUpperCase();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.advancePaid = BOOKING_ADVANCE_AMOUNT;
        this.advanceRefunded = false;
        this.status = K2703047_ReservationStatus.CONFIRMED;
    }

    /**
     * Validates business rules for booking dates:
     * - Check-out must be after check-in.
     * - Guests must book at least 2 days in advance.
     */
    public static void validateDates(LocalDate checkInDate, LocalDate checkOutDate, LocalDate bookingDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be strictly after check-in date.");
        }
        long daysInAdvance = ChronoUnit.DAYS.between(bookingDate, checkInDate);
        if (daysInAdvance < MIN_ADVANCE_DAYS) {
            throw new IllegalArgumentException(String.format(
                    "Advance booking rule violation: Guests must book at least %d days in advance. Selected advance is %d day(s).",
                    MIN_ADVANCE_DAYS, daysInAdvance));
        }
    }

    /**
     * Evaluates if this reservation overlaps with another date range for the same room.
     * Standard half-open interval logic: [checkIn, checkOut)
     */
    public boolean overlapsWith(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        if (this.status == K2703047_ReservationStatus.CANCELLED) {
            return false;
        }
        return this.checkInDate.isBefore(otherCheckOut) && this.checkOutDate.isAfter(otherCheckIn);
    }

    /**
     * Verifies if this reservation can be cancelled at a given timestamp.
     * Rule: Reservations cannot be modified or cancelled within 24 hours of the check-in date.
     *
     * @param currentDateTime timestamp at which cancellation is requested
     * @return true if cancellation is permitted, false if within 24 hours of check-in
     */
    public boolean isEligibleForCancellation(LocalDateTime currentDateTime) {
        if (this.status != K2703047_ReservationStatus.CONFIRMED) {
            return false;
        }
        LocalDateTime checkInStart = this.checkInDate.atStartOfDay();
        LocalDateTime cancellationDeadline = checkInStart.minusHours(CANCELLATION_DEADLINE_HOURS);
        return currentDateTime.isBefore(cancellationDeadline);
    }

    /**
     * Cancels the reservation if eligible and refunds the advance.
     */
    public boolean cancelReservation(LocalDateTime currentDateTime) {
        if (!isEligibleForCancellation(currentDateTime)) {
            return false;
        }
        this.status = K2703047_ReservationStatus.CANCELLED;
        this.advanceRefunded = true;
        return true;
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public K2703047_Guest getGuest() {
        return guest;
    }

    public void setGuest(K2703047_Guest guest) {
        this.guest = Objects.requireNonNull(guest);
    }

    public K2703047_Room getRoom() {
        return room;
    }

    public void setRoom(K2703047_Room room) {
        this.room = Objects.requireNonNull(room);
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be positive.");
        }
        this.numberOfGuests = numberOfGuests;
    }

    public LocalDateTime getBookingTimestamp() {
        return bookingTimestamp;
    }

    public double getAdvancePaid() {
        return advancePaid;
    }

    public boolean isAdvanceRefunded() {
        return advanceRefunded;
    }

    public K2703047_ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(K2703047_ReservationStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    @Override
    public int compareTo(K2703047_Reservation other) {
        // Natural sorting by check-in date, then by booking ID
        int dateCmp = this.checkInDate.compareTo(other.checkInDate);
        if (dateCmp != 0) {
            return dateCmp;
        }
        return this.bookingId.compareTo(other.bookingId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof K2703047_Reservation)) return false;
        K2703047_Reservation that = (K2703047_Reservation) o;
        return Objects.equals(bookingId, that.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return String.format("Reservation [%s] | Guest: %s | Room: %s (%s) | %s to %s (%d nights) | Status: %s",
                bookingId, guest.getFullName(), room.getRoomId(), room.getRoomType().getDisplayName(),
                checkInDate, checkOutDate, getNumberOfNights(), status.getDisplayName());
    }
}
