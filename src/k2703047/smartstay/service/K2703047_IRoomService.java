package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;

import java.time.LocalDate;
import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Interface defining room management operations (Demonstrating Abstraction).
 */
public interface K2703047_IRoomService {
    boolean addRoom(K2703047_Room room);
    boolean updateRoom(String roomId, K2703047_Room updatedRoom);
    boolean removeRoom(String roomId);
    boolean updateRoomStatus(String roomId, K2703047_RoomStatus newStatus);
    K2703047_Room getRoomById(String roomId);
    List<K2703047_Room> getAllRooms();
    List<K2703047_Room> getRoomsByType(K2703047_RoomType roomType);
    List<K2703047_Room> searchAvailableRooms(K2703047_RoomType roomType, LocalDate checkIn, LocalDate checkOut,
                                           K2703047_IReservationService reservationService);
    boolean roomExists(String roomId);
}
