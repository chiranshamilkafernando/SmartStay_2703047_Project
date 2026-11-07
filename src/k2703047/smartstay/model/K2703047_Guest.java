package k2703047.smartstay.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Represents a registered guest customer in the SmartStay system.
 * Enforces business rules and validation for guest registration.
 */
public class K2703047_Guest {
    private String nicOrPassport;
    private String fullName;
    private String contactNumber;
    private String emailAddress;
    private LocalDateTime registrationTimestamp;

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{9,15}$");
    private static final Pattern NIC_PASSPORT_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,15}$");

    /**
     * Constructs and validates a Guest instance.
     */
    public K2703047_Guest(String nicOrPassport, String fullName, String contactNumber, String emailAddress) {
        setNicOrPassport(nicOrPassport);
        setFullName(fullName);
        setContactNumber(contactNumber);
        setEmailAddress(emailAddress);
        this.registrationTimestamp = LocalDateTime.now();
    }

    public static boolean isValidNicOrPassport(String value) {
        return value != null && NIC_PASSPORT_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidPhone(String value) {
        return value != null && PHONE_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isValidEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    // Getters and Setters with Validation
    public String getNicOrPassport() {
        return nicOrPassport;
    }

    public void setNicOrPassport(String nicOrPassport) {
        if (!isValidNicOrPassport(nicOrPassport)) {
            throw new IllegalArgumentException("Invalid NIC/Passport number: must be 6-15 alphanumeric characters.");
        }
        this.nicOrPassport = nicOrPassport.trim().toUpperCase();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Full name must contain at least 2 characters.");
        }
        this.fullName = fullName.trim();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        if (!isValidPhone(contactNumber)) {
            throw new IllegalArgumentException("Invalid contact number format: must be 9-15 digits.");
        }
        this.contactNumber = contactNumber.trim();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        if (!isValidEmail(emailAddress)) {
            throw new IllegalArgumentException("Invalid email address format.");
        }
        this.emailAddress = emailAddress.trim().toLowerCase();
    }

    public LocalDateTime getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(LocalDateTime registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof K2703047_Guest)) return false;
        K2703047_Guest that = (K2703047_Guest) o;
        return Objects.equals(nicOrPassport, that.nicOrPassport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nicOrPassport);
    }

    @Override
    public String toString() {
        return String.format("%s (NIC/Passport: %s | Phone: %s | Email: %s)",
                fullName, nicOrPassport, contactNumber, emailAddress);
    }
}
