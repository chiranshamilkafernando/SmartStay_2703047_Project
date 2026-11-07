package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_BedType;
import k2703047.smartstay.model.K2703047_FamilySuiteRoom;
import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomFactory;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Facade Pattern implementation coordinating Room, Guest, and Reservation subsystems.
 * Acts as the centralized API for UI controllers (Console CLI & Desktop GUI).
 */
public class K2703047_HotelSystem {

    private final K2703047_IRoomService roomService;
    private final K2703047_IGuestService guestService;
    private final K2703047_IReservationService reservationService;

    public K2703047_HotelSystem() {
        this.roomService = new K2703047_RoomService();
        this.guestService = new K2703047_GuestService();
        this.reservationService = new K2703047_ReservationService();
        seedInitialData();
    }

    /**
     * Seeds initial realistic sample rooms, registered guests, and bookings.
     */
    private void seedInitialData() {
        // Seed Rooms across all types and bed options
        roomService.addRoom(K2703047_RoomFactory.createRoom("R-101", K2703047_RoomType.STANDARD, K2703047_BedType.SINGLE));
        roomService.addRoom(K2703047_RoomFactory.createRoom("R-102", K2703047_RoomType.STANDARD, K2703047_BedType.DOUBLE));
        roomService.addRoom(K2703047_RoomFactory.createRoom("D-201", K2703047_RoomType.DELUXE, K2703047_BedType.QUEEN));
        roomService.addRoom(K2703047_RoomFactory.createRoom("D-202", K2703047_RoomType.DELUXE, K2703047_BedType.KING));
        roomService.addRoom(K2703047_RoomFactory.createRoom("S-301", K2703047_RoomType.SUITE, K2703047_BedType.KING));
        roomService.addRoom(K2703047_RoomFactory.createRoom("FS-401", K2703047_RoomType.FAMILY_SUITE, K2703047_BedType.KING));

        // One room under maintenance for rule validation
        K2703047_Room maintRoom = K2703047_RoomFactory.createRoom("R-103", K2703047_RoomType.STANDARD, K2703047_BedType.DOUBLE);
        maintRoom.setStatus(K2703047_RoomStatus.UNDER_MAINTENANCE);
        roomService.addRoom(maintRoom);

        // Seed Sample Guests
        K2703047_Guest g1 = new K2703047_Guest("199512345678", "Kasun Perera", "+94771234567", "kasun.p@gmail.com");
        K2703047_Guest g2 = new K2703047_Guest("N87654321", "Sarah Jenkins", "+447911123456", "sarah.j@outlook.com");
        guestService.registerGuest(g1);
        guestService.registerGuest(g2);

        // Seed an active reservation (e.g., checking in 5 days from today for 6 nights -> qualifies for 12% discount)
        LocalDate today = LocalDate.now();
        LocalDate in1 = today.plusDays(5);
        LocalDate out1 = in1.plusDays(6);
        reservationService.makeReservation(g1, roomService.getRoomById("D-201"), in1, out1, 3);
    }

    // Room operations
    public boolean addRoom(K2703047_Room room) {
        return roomService.addRoom(room);
    }

    public boolean updateRoom(String roomId, K2703047_Room updatedRoom) {
        return roomService.updateRoom(roomId, updatedRoom);
    }

    public boolean removeRoom(String roomId) {
        // Validation: cannot remove room if it has active reservations
        List<K2703047_Reservation> active = reservationService.getActiveReservationsForRoom(roomId);
        if (!active.isEmpty()) {
            throw new IllegalStateException("Cannot remove Room " + roomId + ": active reservations exist.");
        }
        return roomService.removeRoom(roomId);
    }

    public boolean updateRoomStatus(String roomId, K2703047_RoomStatus status) {
        return roomService.updateRoomStatus(roomId, status);
    }

    public List<K2703047_Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    public K2703047_Room getRoomById(String roomId) {
        return roomService.getRoomById(roomId);
    }

    public List<K2703047_Room> searchAvailableRooms(K2703047_RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        return roomService.searchAvailableRooms(roomType, checkIn, checkOut, reservationService);
    }

    // Guest operations
    public boolean registerGuest(String nicOrPassport, String fullName, String phone, String email) {
        K2703047_Guest guest = new K2703047_Guest(nicOrPassport, fullName, phone, email);
        return guestService.registerGuest(guest);
    }

    public K2703047_Guest getGuest(String nicOrPassport) {
        return guestService.getGuestByNicOrPassport(nicOrPassport);
    }

    public List<K2703047_Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    public List<K2703047_Guest> searchGuestsByName(String name) {
        return guestService.searchGuestsByName(name);
    }

    // Reservation operations
    public K2703047_Reservation makeReservation(String nicOrPassport, String roomId,
                                                LocalDate checkInDate, LocalDate checkOutDate,
                                                int numberOfGuests) {
        K2703047_Guest guest = guestService.getGuestByNicOrPassport(nicOrPassport);
        if (guest == null) {
            throw new IllegalArgumentException("Guest with NIC/Passport " + nicOrPassport + " is not registered.");
        }
        K2703047_Room room = roomService.getRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomId + " does not exist.");
        }
        return reservationService.makeReservation(guest, room, checkInDate, checkOutDate, numberOfGuests);
    }

    public boolean cancelReservation(String bookingId) {
        return reservationService.cancelReservation(bookingId, LocalDateTime.now());
    }

    public boolean cancelReservationWithCustomTime(String bookingId, LocalDateTime customCancellationTime) {
        return reservationService.cancelReservation(bookingId, customCancellationTime);
    }

    public K2703047_Reservation getReservation(String bookingId) {
        return reservationService.getReservationById(bookingId);
    }

    public List<K2703047_Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    public List<K2703047_Reservation> searchReservationsByGuestName(String guestName) {
        return reservationService.searchReservationsByGuestName(guestName);
    }

    public List<K2703047_Reservation> getReservationsByCheckInDate(LocalDate checkInDate) {
        return reservationService.getReservationsByCheckInDate(checkInDate);
    }

    public K2703047_Reservation searchReservationBinary(String bookingId) {
        return reservationService.binarySearchByBookingId(bookingId);
    }

    public K2703047_Invoice generateInvoice(String bookingId) {
        return reservationService.generateInvoice(bookingId);
    }

    public K2703047_IRoomService getRoomService() {
        return roomService;
    }

    public K2703047_IGuestService getGuestService() {
        return guestService;
    }

    public K2703047_IReservationService getReservationService() {
        return reservationService;
    }
}
