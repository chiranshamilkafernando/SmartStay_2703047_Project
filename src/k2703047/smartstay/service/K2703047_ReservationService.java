package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_ReservationStatus;
import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Implementation of Reservation Management, Search, Cancellation, and Billing.
 * Demonstrates:
 * - Data Structures: LinkedHashMap (O(1) lookup), ArrayList, and TreeMap (Red-Black Tree for date indexing).
 * - Algorithms: Interval Conflict Detection, Advance Validation, 24-hr Cancellation Threshold,
 *               Binary Search, and 5-step Billing Calculation.
 */
public class K2703047_ReservationService implements K2703047_IReservationService {

    // Primary data store: Map for O(1) Booking ID lookups
    private final Map<String, K2703047_Reservation> reservationMap;

    // Ordered list for binary search and sorting operations
    private final List<K2703047_Reservation> reservationList;

    // Self-balancing search tree index: Check-in Date -> List of Reservations
    private final TreeMap<LocalDate, List<K2703047_Reservation>> checkInDateIndex;

    // Generator for unique sequential Booking IDs (e.g., BK-1001)
    private final AtomicInteger bookingSequence;

    public K2703047_ReservationService() {
        this.reservationMap = new LinkedHashMap<>();
        this.reservationList = new ArrayList<>();
        this.checkInDateIndex = new TreeMap<>();
        this.bookingSequence = new AtomicInteger(1001);
    }

    /**
     * Generates a unique sequential booking ID.
     */
    private synchronized String generateNextBookingId() {
        return "BK-" + bookingSequence.getAndIncrement();
    }

    @Override
    public synchronized K2703047_Reservation makeReservation(K2703047_Guest guest, K2703047_Room room,
                                                            LocalDate checkInDate, LocalDate checkOutDate,
                                                            int numberOfGuests) {
        if (guest == null) {
            throw new IllegalArgumentException("Cannot create reservation without a registered guest.");
        }
        if (room == null) {
            throw new IllegalArgumentException("Cannot create reservation without an assigned room.");
        }

        // Business rule 1: Room status must be AVAILABLE
        if (room.getStatus() != K2703047_RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room " + room.getRoomId() + " is currently " +
                    room.getStatus().getDisplayName() + " and cannot be reserved.");
        }

        // Business rule 2: Check-in must be at least 2 days in advance, check-out after check-in
        LocalDate today = LocalDate.now();
        K2703047_Reservation.validateDates(checkInDate, checkOutDate, today);

        // Business rule 3: Interval Conflict Detection Algorithm
        if (hasRoomDateConflict(room.getRoomId(), checkInDate, checkOutDate, null)) {
            throw new IllegalStateException("Room " + room.getRoomId() +
                    " already has a confirmed reservation overlapping with dates: " + checkInDate + " to " + checkOutDate);
        }

        String bookingId = generateNextBookingId();
        K2703047_Reservation reservation = new K2703047_Reservation(
                bookingId, guest, room, checkInDate, checkOutDate, numberOfGuests, LocalDateTime.now());

        // Store into data structures
        reservationMap.put(bookingId, reservation);
        reservationList.add(reservation);

        // Update TreeMap date index
        checkInDateIndex.computeIfAbsent(checkInDate, k -> new ArrayList<>()).add(reservation);

        return reservation;
    }

    /**
     * Algorithm 1: Date Range Interval Conflict Detection Algorithm.
     * Evaluates half-open intervals [checkIn, checkOut) for active reservations of the same room.
     */
    @Override
    public boolean hasRoomDateConflict(String roomId, LocalDate checkIn, LocalDate checkOut, String excludeBookingId) {
        if (roomId == null || checkIn == null || checkOut == null) {
            return false;
        }
        for (K2703047_Reservation r : reservationMap.values()) {
            if (r.getStatus() != K2703047_ReservationStatus.CANCELLED &&
                r.getRoom().getRoomId().equalsIgnoreCase(roomId)) {
                
                // Exclude current booking if modifying/checking
                if (excludeBookingId != null && r.getBookingId().equalsIgnoreCase(excludeBookingId)) {
                    continue;
                }

                // Conflict test: checkIn < existingCheckOut AND checkOut > existingCheckIn
                if (r.overlapsWith(checkIn, checkOut)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Algorithm 2: 24-Hour Cancellation Eligibility Algorithm.
     * Reservations cannot be modified or cancelled within 24 hours of check-in date.
     * Refunds the LKR 10,000 booking advance upon successful cancellation.
     */
    @Override
    public synchronized boolean cancelReservation(String bookingId, LocalDateTime cancellationTime) {
        if (bookingId == null) {
            return false;
        }
        K2703047_Reservation reservation = getReservationById(bookingId);
        if (reservation == null) {
            return false;
        }
        LocalDateTime effectiveTime = (cancellationTime != null) ? cancellationTime : LocalDateTime.now();

        boolean cancelled = reservation.cancelReservation(effectiveTime);
        if (cancelled) {
            // Remove from check-in date index list if present
            List<K2703047_Reservation> list = checkInDateIndex.get(reservation.getCheckInDate());
            if (list != null) {
                list.remove(reservation);
            }
        }
        return cancelled;
    }

    @Override
    public K2703047_Reservation getReservationById(String bookingId) {
        if (bookingId == null) return null;
        return reservationMap.get(bookingId.trim().toUpperCase());
    }

    @Override
    public List<K2703047_Reservation> getAllReservations() {
        return new ArrayList<>(reservationMap.values());
    }

    /**
     * Search Algorithm: Search reservations by guest name (case-insensitive substring).
     */
    @Override
    public List<K2703047_Reservation> searchReservationsByGuestName(String guestNameKeyword) {
        List<K2703047_Reservation> results = new ArrayList<>();
        if (guestNameKeyword == null || guestNameKeyword.trim().isEmpty()) {
            return getAllReservations();
        }
        String lowerKeyword = guestNameKeyword.trim().toLowerCase();
        for (K2703047_Reservation r : reservationMap.values()) {
            if (r.getGuest().getFullName().toLowerCase().contains(lowerKeyword)) {
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Fast O(log N) retrieval using TreeMap self-balancing index.
     */
    @Override
    public List<K2703047_Reservation> getReservationsByCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            return Collections.emptyList();
        }
        List<K2703047_Reservation> list = checkInDateIndex.get(checkInDate);
        return (list != null) ? new ArrayList<>(list) : Collections.emptyList();
    }

    @Override
    public List<K2703047_Reservation> getActiveReservationsForRoom(String roomId) {
        List<K2703047_Reservation> results = new ArrayList<>();
        if (roomId == null) return results;
        for (K2703047_Reservation r : reservationMap.values()) {
            if (r.getStatus() == K2703047_ReservationStatus.CONFIRMED &&
                r.getRoom().getRoomId().equalsIgnoreCase(roomId)) {
                results.add(r);
            }
        }
        return results;
    }

    /**
     * Algorithm 3: Binary Search on sorted reservation list.
     * Time Complexity: O(log N)
     * Demonstrates knowledge of fundamental search algorithms.
     */
    @Override
    public K2703047_Reservation binarySearchByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }
        String target = bookingId.trim().toUpperCase();

        // Ensure list is sorted by Booking ID for binary search
        List<K2703047_Reservation> sortedList = new ArrayList<>(reservationList);
        sortedList.sort(Comparator.comparing(K2703047_Reservation::getBookingId));

        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            K2703047_Reservation midVal = sortedList.get(mid);
            int cmp = midVal.getBookingId().compareTo(target);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return midVal; // Found
            }
        }
        return null; // Not found
    }

    /**
     * Generates a detailed invoice for the given booking ID.
     */
    @Override
    public K2703047_Invoice generateInvoice(String bookingId) {
        K2703047_Reservation reservation = getReservationById(bookingId);
        if (reservation == null) {
            throw new IllegalArgumentException("No reservation found for Booking ID: " + bookingId);
        }
        return new K2703047_Invoice(reservation);
    }
}
