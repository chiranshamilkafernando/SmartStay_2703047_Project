package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Implementation of Room Management functionalities for administrators.
 * Data Structure: LinkedHashMap provides O(1) search/insertion while preserving deterministic ordering.
 */
public class K2703047_RoomService implements K2703047_IRoomService {

    // Primary data structure for room records: Key = Room ID, Value = Room Object
    private final Map<String, K2703047_Room> roomRegistry;

    public K2703047_RoomService() {
        this.roomRegistry = new LinkedHashMap<>();
    }

    @Override
    public synchronized boolean addRoom(K2703047_Room room) {
        if (room == null || roomRegistry.containsKey(room.getRoomId())) {
            return false;
        }
        roomRegistry.put(room.getRoomId(), room);
        return true;
    }

    @Override
    public synchronized boolean updateRoom(String roomId, K2703047_Room updatedRoom) {
        if (roomId == null || updatedRoom == null) {
            return false;
        }
        String normalizedId = roomId.trim().toUpperCase();
        if (!roomRegistry.containsKey(normalizedId)) {
            return false;
        }
        roomRegistry.put(normalizedId, updatedRoom);
        return true;
    }

    @Override
    public synchronized boolean removeRoom(String roomId) {
        if (roomId == null) {
            return false;
        }
        String normalizedId = roomId.trim().toUpperCase();
        return roomRegistry.remove(normalizedId) != null;
    }

    @Override
    public synchronized boolean updateRoomStatus(String roomId, K2703047_RoomStatus newStatus) {
        if (roomId == null || newStatus == null) {
            return false;
        }
        String normalizedId = roomId.trim().toUpperCase();
        K2703047_Room room = roomRegistry.get(normalizedId);
        if (room != null) {
            room.setStatus(newStatus);
            return true;
        }
        return false;
    }

    @Override
    public K2703047_Room getRoomById(String roomId) {
        if (roomId == null) return null;
        return roomRegistry.get(roomId.trim().toUpperCase());
    }

    @Override
    public List<K2703047_Room> getAllRooms() {
        return new ArrayList<>(roomRegistry.values());
    }

    @Override
    public List<K2703047_Room> getRoomsByType(K2703047_RoomType roomType) {
        if (roomType == null) {
            return getAllRooms();
        }
        List<K2703047_Room> matchingRooms = new ArrayList<>();
        for (K2703047_Room room : roomRegistry.values()) {
            if (room.getRoomType() == roomType) {
                matchingRooms.add(room);
            }
        }
        return matchingRooms;
    }

    /**
     * Search Algorithm: Filters available rooms based on Room Type, current status,
     * and checks for date conflicts across existing reservations.
     */
    @Override
    public List<K2703047_Room> searchAvailableRooms(K2703047_RoomType roomType, LocalDate checkIn, LocalDate checkOut,
                                                    K2703047_IReservationService reservationService) {
        List<K2703047_Room> candidateRooms = (roomType != null) ? getRoomsByType(roomType) : getAllRooms();
        List<K2703047_Room> availableRooms = new ArrayList<>();

        for (K2703047_Room room : candidateRooms) {
            // Business rule: Room status must be AVAILABLE (not UNDER_MAINTENANCE)
            if (room.getStatus() != K2703047_RoomStatus.AVAILABLE) {
                continue;
            }
            // Check if there is an overlapping reservation during the requested interval
            if (reservationService != null && checkIn != null && checkOut != null) {
                boolean hasConflict = reservationService.hasRoomDateConflict(room.getRoomId(), checkIn, checkOut, null);
                if (hasConflict) {
                    continue;
                }
            }
            availableRooms.add(room);
        }
        return availableRooms;
    }

    @Override
    public boolean roomExists(String roomId) {
        if (roomId == null) return false;
        return roomRegistry.containsKey(roomId.trim().toUpperCase());
    }
}
