package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Guest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Implementation of Guest Registration and Directory management.
 * Data Structure: LinkedHashMap indexed by NIC/Passport for O(1) key lookups.
 */
public class K2703047_GuestService implements K2703047_IGuestService {

    private final Map<String, K2703047_Guest> guestDirectory;

    public K2703047_GuestService() {
        this.guestDirectory = new LinkedHashMap<>();
    }

    @Override
    public synchronized boolean registerGuest(K2703047_Guest guest) {
        if (guest == null || guestDirectory.containsKey(guest.getNicOrPassport())) {
            return false;
        }
        guestDirectory.put(guest.getNicOrPassport(), guest);
        return true;
    }

    @Override
    public K2703047_Guest getGuestByNicOrPassport(String nicOrPassport) {
        if (nicOrPassport == null) return null;
        return guestDirectory.get(nicOrPassport.trim().toUpperCase());
    }

    @Override
    public List<K2703047_Guest> getAllGuests() {
        return new ArrayList<>(guestDirectory.values());
    }

    /**
     * Search Algorithm: Linear search with case-insensitive substring matching.
     */
    @Override
    public List<K2703047_Guest> searchGuestsByName(String nameKeyword) {
        List<K2703047_Guest> results = new ArrayList<>();
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return getAllGuests();
        }
        String lowerKeyword = nameKeyword.trim().toLowerCase();
        for (K2703047_Guest guest : guestDirectory.values()) {
            if (guest.getFullName().toLowerCase().contains(lowerKeyword)) {
                results.add(guest);
            }
        }
        return results;
    }

    @Override
    public boolean guestExists(String nicOrPassport) {
        if (nicOrPassport == null) return false;
        return guestDirectory.containsKey(nicOrPassport.trim().toUpperCase());
    }
}
