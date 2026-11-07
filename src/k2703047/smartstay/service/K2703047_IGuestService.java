package k2703047.smartstay.service;

import k2703047.smartstay.model.K2703047_Guest;

import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Interface defining customer registration and lookup operations.
 */
public interface K2703047_IGuestService {
    boolean registerGuest(K2703047_Guest guest);
    K2703047_Guest getGuestByNicOrPassport(String nicOrPassport);
    List<K2703047_Guest> getAllGuests();
    List<K2703047_Guest> searchGuestsByName(String nameKeyword);
    boolean guestExists(String nicOrPassport);
}
