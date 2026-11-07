package k2703047.smartstay.model;

import java.util.Objects;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Abstract base class representing a hotel room at SmartStay.
 * Demonstrates Abstraction and Encapsulation. Subclasses implement
 * specific room package business policies and calculations (Polymorphism).
 */
public abstract class K2703047_Room {
    private String roomId;
    private K2703047_RoomType roomType;
    private K2703047_BedType bedType;
    private double dailyRate;
    private K2703047_RoomStatus status;
    private int baseCapacity;

    /**
     * Parameterized constructor.
     *
     * @param roomId       Unique room identifier
     * @param roomType     Category of room
     * @param bedType      Type of bed
     * @param dailyRate    Room-type specific daily rate (LKR)
     * @param status       Availability status
     * @param baseCapacity Base guest capacity before extra guest charges apply
     */
    public K2703047_Room(String roomId, K2703047_RoomType roomType, K2703047_BedType bedType,
                         double dailyRate, K2703047_RoomStatus status, int baseCapacity) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty.");
        }
        if (dailyRate <= 0) {
            throw new IllegalArgumentException("Daily room rate must be positive.");
        }
        if (baseCapacity <= 0) {
            throw new IllegalArgumentException("Base capacity must be at least 1.");
        }
        this.roomId = roomId.trim().toUpperCase();
        this.roomType = Objects.requireNonNull(roomType, "Room type is required.");
        this.bedType = Objects.requireNonNull(bedType, "Bed type is required.");
        this.dailyRate = dailyRate;
        this.status = (status != null) ? status : K2703047_RoomStatus.AVAILABLE;
        this.baseCapacity = baseCapacity;
    }

    // Abstract methods implementing Polymorphism across Room packages
    public abstract double calculateTax(double netRoomCharge);
    public abstract double getExtraGuestRate();
    public abstract boolean hasComplimentaryBreakfast();
    public abstract double getTaxRate();
    public abstract String getRoomPackageDescription();

    /**
     * Calculates the extra guest charge based on room-specific policies.
     * Formula: (Number of Guests - Base Capacity) * Extra Guest Rate * Number of Nights
     *
     * @param guestCount     Total number of staying guests
     * @param numberOfNights Total duration in nights
     * @return Extra guest charge in LKR
     */
    public double calculateExtraGuestCharge(int guestCount, long numberOfNights) {
        if (guestCount <= baseCapacity || numberOfNights <= 0) {
            return 0.0;
        }
        int extraGuests = guestCount - baseCapacity;
        return extraGuests * getExtraGuestRate() * numberOfNights;
    }

    public boolean isAvailable() {
        return this.status == K2703047_RoomStatus.AVAILABLE;
    }

    // Getters and Setters with Encapsulation
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty.");
        }
        this.roomId = roomId.trim().toUpperCase();
    }

    public K2703047_RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(K2703047_RoomType roomType) {
        this.roomType = Objects.requireNonNull(roomType, "Room type cannot be null.");
    }

    public K2703047_BedType getBedType() {
        return bedType;
    }

    public void setBedType(K2703047_BedType bedType) {
        this.bedType = Objects.requireNonNull(bedType, "Bed type cannot be null.");
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        if (dailyRate <= 0) {
            throw new IllegalArgumentException("Daily rate must be positive.");
        }
        this.dailyRate = dailyRate;
    }

    public K2703047_RoomStatus getStatus() {
        return status;
    }

    public void setStatus(K2703047_RoomStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
    }

    public int getBaseCapacity() {
        return baseCapacity;
    }

    public void setBaseCapacity(int baseCapacity) {
        if (baseCapacity <= 0) {
            throw new IllegalArgumentException("Base capacity must be positive.");
        }
        this.baseCapacity = baseCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof K2703047_Room)) return false;
        K2703047_Room that = (K2703047_Room) o;
        return Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return String.format("Room [%s] - %s | Bed: %s | Rate: LKR %,.2f/night | Status: %s | Capacity: %d",
                roomId, roomType.getDisplayName(), bedType.getDisplayName(), dailyRate, status.getDisplayName(), baseCapacity);
    }
}
