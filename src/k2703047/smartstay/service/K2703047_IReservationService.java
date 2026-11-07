package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Interface defining reservation operations, search, cancellation, and invoice generation.
 */
public interface K2703047_IReservationService {
    K2703047_Reservation makeReservation(K2703047_Guest guest, K2703047_Room room,
                                        LocalDate checkInDate, LocalDate checkOutDate,
                                        int numberOfGuests);
    
    boolean cancelReservation(String bookingId, LocalDateTime cancellationTime);
    K2703047_Reservation getReservationById(String bookingId);
    List<K2703047_Reservation> getAllReservations();
    List<K2703047_Reservation> searchReservationsByGuestName(String guestNameKeyword);
    List<K2703047_Reservation> getReservationsByCheckInDate(LocalDate checkInDate);
    List<K2703047_Reservation> getActiveReservationsForRoom(String roomId);
    boolean hasRoomDateConflict(String roomId, LocalDate checkIn, LocalDate checkOut, String excludeBookingId);
    K2703047_Invoice generateInvoice(String bookingId);
    K2703047_Reservation binarySearchByBookingId(String bookingId);
}
