package k2703047.smartstay.ui;

import k2703047.smartstay.model.K2703047_BedType;
import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomFactory;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;
import k2703047.smartstay.service.K2703047_HotelSystem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Interactive Command Line Interface (CLI) for SmartStay Hotel Booking System.
 * Usability features:
 * - Colorized ANSI output headers and status messages
 * - Robust input validation and error feedback
 * - Clear table formats for rooms, guests, reservations, and invoices
 */
public class K2703047_CLIView {

    private final K2703047_HotelSystem hotelSystem;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public K2703047_CLIView(K2703047_HotelSystem hotelSystem) {
        this.hotelSystem = hotelSystem;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader("SMARTSTAY HOTEL BOOKING SYSTEM - MAIN PORTAL");
            System.out.println(" 1. Administrator Portal (Manage Rooms & Status)");
            System.out.println(" 2. Guest Portal (Register, Search, Book, Cancel, Invoices)");
            System.out.println(" 3. View All Reservations & Invoices");
            System.out.println(" 4. Launch Desktop GUI Interface");
            System.out.println(" 5. Exit System");
            System.out.print("\n Select an option [1-5]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleAdminMenu();
                    break;
                case "2":
                    handleGuestMenu();
                    break;
                case "3":
                    handleViewReservationsAndInvoices();
                    break;
                case "4":
                    launchGui();
                    break;
                case "5":
                    System.out.println("\nExiting SmartStay Hotel Booking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option from 1 to 5.");
            }
        }
    }

    // ==========================================
    // ADMINISTRATOR SUB-MENU
    // ==========================================
    private void handleAdminMenu() {
        boolean back = false;
        while (!back) {
            printHeader("ADMINISTRATOR PORTAL");
            System.out.println(" 1. View All Room Records");
            System.out.println(" 2. Add New Room Record");
            System.out.println(" 3. Update Room Record");
            System.out.println(" 4. Manage Room Availability Status");
            System.out.println(" 5. Remove Room Record");
            System.out.println(" 6. Return to Main Menu");
            System.out.print("\n Select admin operation [1-6]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    displayAllRooms();
                    break;
                case "2":
                    handleAddNewRoom();
                    break;
                case "3":
                    handleUpdateRoom();
                    break;
                case "4":
                    handleManageRoomStatus();
                    break;
                case "5":
                    handleRemoveRoom();
                    break;
                case "6":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void displayAllRooms() {
        printHeader("ALL ROOM RECORDS");
        List<K2703047_Room> rooms = hotelSystem.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms registered in the system.");
            return;
        }
        printRoomTable(rooms);
    }

    private void handleAddNewRoom() {
        printHeader("ADD NEW ROOM RECORD");
        try {
            System.out.print("Enter Room ID (e.g. R-105, D-205, S-305): ");
            String roomId = scanner.nextLine().trim().toUpperCase();
            if (hotelSystem.getRoomById(roomId) != null) {
                System.out.println("[Error] A room with ID " + roomId + " already exists.");
                return;
            }

            System.out.println("Select Room Type:");
            System.out.println("1. Standard (LKR 6,000/night, No Breakfast, Extra: LKR 1,000, Tax: 10%)");
            System.out.println("2. Deluxe   (LKR 9,000/night, Breakfast Yes, Extra: LKR 1,500, Tax: 12%)");
            System.out.println("3. Suite    (LKR 14,000/night, Breakfast Yes, Extra: LKR 2,000, Tax: 15%)");
            System.out.println("4. Family Suite (LKR 18,000/night, Breakfast Yes, Extra: LKR 2,500, Tax: 15%)");
            System.out.print("Choice [1-4]: ");
            String tChoice = scanner.nextLine().trim();
            K2703047_RoomType type;
            switch (tChoice) {
                case "1": type = K2703047_RoomType.STANDARD; break;
                case "2": type = K2703047_RoomType.DELUXE; break;
                case "3": type = K2703047_RoomType.SUITE; break;
                case "4": type = K2703047_RoomType.FAMILY_SUITE; break;
                default:
                    System.out.println("[Error] Invalid room type selected.");
                    return;
            }

            System.out.println("Select Bed Type:");
            System.out.println("1. Single Bed\n2. Double Bed\n3. Queen Bed\n4. King Bed");
            System.out.print("Choice [1-4]: ");
            String bChoice = scanner.nextLine().trim();
            K2703047_BedType bedType;
            switch (bChoice) {
                case "1": bedType = K2703047_BedType.SINGLE; break;
                case "2": bedType = K2703047_BedType.DOUBLE; break;
                case "3": bedType = K2703047_BedType.QUEEN; break;
                case "4": bedType = K2703047_BedType.KING; break;
                default:
                    System.out.println("[Error] Invalid bed type selected.");
                    return;
            }

            System.out.print("Use default daily rate of LKR " + String.format("%,.0f", type.getDailyRate()) + "? (Y/N): ");
            String useDef = scanner.nextLine().trim().toUpperCase();
            double rate = type.getDailyRate();
            if (useDef.equals("N")) {
                System.out.print("Enter custom daily rate (LKR): ");
                rate = Double.parseDouble(scanner.nextLine().trim());
            }

            K2703047_Room newRoom = K2703047_RoomFactory.createRoom(
                    roomId, type, bedType, rate, K2703047_RoomStatus.AVAILABLE, type.getStandardCapacity());
            boolean added = hotelSystem.addRoom(newRoom);
            if (added) {
                System.out.println("[Success] Room " + roomId + " successfully added to the system!");
            } else {
                System.out.println("[Error] Failed to add room.");
            }
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void handleUpdateRoom() {
        printHeader("UPDATE ROOM RECORD");
        displayAllRooms();
        System.out.print("Enter Room ID to update: ");
        String roomId = scanner.nextLine().trim().toUpperCase();
        K2703047_Room existing = hotelSystem.getRoomById(roomId);
        if (existing == null) {
            System.out.println("[Error] Room not found.");
            return;
        }

        try {
            System.out.print("Enter new Daily Rate (Current: LKR " + existing.getDailyRate() + ") or press Enter to skip: ");
            String rInput = scanner.nextLine().trim();
            if (!rInput.isEmpty()) {
                existing.setDailyRate(Double.parseDouble(rInput));
            }

            System.out.println("Select new Bed Type (Current: " + existing.getBedType() + "):");
            System.out.println("1. Single Bed | 2. Double Bed | 3. Queen Bed | 4. King Bed | Enter to skip");
            String bInput = scanner.nextLine().trim();
            if (!bInput.isEmpty()) {
                switch (bInput) {
                    case "1": existing.setBedType(K2703047_BedType.SINGLE); break;
                    case "2": existing.setBedType(K2703047_BedType.DOUBLE); break;
                    case "3": existing.setBedType(K2703047_BedType.QUEEN); break;
                    case "4": existing.setBedType(K2703047_BedType.KING); break;
                }
            }

            System.out.println("[Success] Room " + roomId + " updated successfully!");
        } catch (Exception e) {
            System.out.println("[Error] Could not update room: " + e.getMessage());
        }
    }

    private void handleManageRoomStatus() {
        printHeader("MANAGE ROOM AVAILABILITY STATUS");
        displayAllRooms();
        System.out.print("Enter Room ID to update status: ");
        String roomId = scanner.nextLine().trim().toUpperCase();
        K2703047_Room room = hotelSystem.getRoomById(roomId);
        if (room == null) {
            System.out.println("[Error] Room not found.");
            return;
        }

        System.out.println("Current Status: " + room.getStatus().getDisplayName());
        System.out.println("Select New Status:");
        System.out.println(" 1. Available");
        System.out.println(" 2. Reserved");
        System.out.println(" 3. Under Maintenance");
        System.out.print("Choice [1-3]: ");
        String choice = scanner.nextLine().trim();

        K2703047_RoomStatus newStatus = null;
        switch (choice) {
            case "1": newStatus = K2703047_RoomStatus.AVAILABLE; break;
            case "2": newStatus = K2703047_RoomStatus.RESERVED; break;
            case "3": newStatus = K2703047_RoomStatus.UNDER_MAINTENANCE; break;
            default:
                System.out.println("[Error] Invalid status option.");
                return;
        }

        hotelSystem.updateRoomStatus(roomId, newStatus);
        System.out.println("[Success] Room " + roomId + " status updated to " + newStatus.getDisplayName() + "!");
    }

    private void handleRemoveRoom() {
        printHeader("REMOVE ROOM RECORD");
        displayAllRooms();
        System.out.print("Enter Room ID to remove: ");
        String roomId = scanner.nextLine().trim().toUpperCase();
        try {
            boolean removed = hotelSystem.removeRoom(roomId);
            if (removed) {
                System.out.println("[Success] Room " + roomId + " removed successfully!");
            } else {
                System.out.println("[Error] Room not found.");
            }
        } catch (IllegalStateException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // ==========================================
    // GUEST SUB-MENU
    // ==========================================
    private void handleGuestMenu() {
        boolean back = false;
        while (!back) {
            printHeader("GUEST / CUSTOMER PORTAL");
            System.out.println(" 1. Register as Customer");
            System.out.println(" 2. Search Available Rooms by Room Type and Date");
            System.out.println(" 3. Make Room Reservation");
            System.out.println(" 4. Search Reservations by Guest Name or Booking ID");
            System.out.println(" 5. View Reservations by Check-In Date");
            System.out.println(" 6. Cancel Eligible Reservation");
            System.out.println(" 7. Return to Main Menu");
            System.out.print("\n Select guest operation [1-7]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleRegisterGuest();
                    break;
                case "2":
                    handleSearchRooms();
                    break;
                case "3":
                    handleMakeReservation();
                    break;
                case "4":
                    handleSearchReservations();
                    break;
                case "5":
                    handleViewByCheckInDate();
                    break;
                case "6":
                    handleCancelReservation();
                    break;
                case "7":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
            }
        }
    }

    private void handleRegisterGuest() {
        printHeader("CUSTOMER REGISTRATION");
        try {
            System.out.print("Enter NIC or Passport Number: ");
            String nic = scanner.nextLine().trim().toUpperCase();
            if (hotelSystem.getGuest(nic) != null) {
                System.out.println("[Info] Customer is already registered: " + hotelSystem.getGuest(nic));
                return;
            }

            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Contact Number (e.g. +94771234567): ");
            String phone = scanner.nextLine().trim();

            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();

            boolean registered = hotelSystem.registerGuest(nic, name, phone, email);
            if (registered) {
                System.out.println("[Success] Customer registered successfully!");
            }
        } catch (Exception e) {
            System.out.println("[Error] Registration failed: " + e.getMessage());
        }
    }

    private void handleSearchRooms() {
        printHeader("SEARCH AVAILABLE ROOMS");
        try {
            System.out.println("Filter by Room Type:");
            System.out.println("0. All Types");
            System.out.println("1. Standard | 2. Deluxe | 3. Suite | 4. Family Suite");
            System.out.print("Choice [0-4]: ");
            String tc = scanner.nextLine().trim();
            K2703047_RoomType type = null;
            if (tc.equals("1")) type = K2703047_RoomType.STANDARD;
            else if (tc.equals("2")) type = K2703047_RoomType.DELUXE;
            else if (tc.equals("3")) type = K2703047_RoomType.SUITE;
            else if (tc.equals("4")) type = K2703047_RoomType.FAMILY_SUITE;

            LocalDate today = LocalDate.now();
            System.out.printf("Enter Check-In Date (YYYY-MM-DD, minimum %s): ", today.plusDays(2));
            String inStr = scanner.nextLine().trim();
            LocalDate checkIn = LocalDate.parse(inStr, dateFormatter);

            System.out.print("Enter Check-Out Date (YYYY-MM-DD): ");
            String outStr = scanner.nextLine().trim();
            LocalDate checkOut = LocalDate.parse(outStr, dateFormatter);

            List<K2703047_Room> available = hotelSystem.searchAvailableRooms(type, checkIn, checkOut);
            if (available.isEmpty()) {
                System.out.println("\n[Notice] No rooms are available for the selected dates and criteria.");
            } else {
                System.out.println("\n[Success] Found " + available.size() + " available room(s):");
                printRoomTable(available);
            }
        } catch (DateTimeParseException e) {
            System.out.println("[Error] Invalid date format. Please use YYYY-MM-DD.");
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void handleMakeReservation() {
        printHeader("MAKE ROOM RESERVATION");
        try {
            System.out.print("Enter Guest NIC/Passport: ");
            String nic = scanner.nextLine().trim().toUpperCase();
            K2703047_Guest guest = hotelSystem.getGuest(nic);
            if (guest == null) {
                System.out.println("[Error] Customer not registered. Please register first (Option 1).");
                return;
            }
            System.out.println("Guest: " + guest.getFullName() + " (" + guest.getEmailAddress() + ")");

            LocalDate today = LocalDate.now();
            System.out.printf("Enter Check-In Date (YYYY-MM-DD, at least 2 days ahead, min: %s): ", today.plusDays(2));
            LocalDate checkIn = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);

            System.out.print("Enter Check-Out Date (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);

            System.out.print("Enter Number of Guests: ");
            int guests = Integer.parseInt(scanner.nextLine().trim());

            // Show available rooms for these dates
            List<K2703047_Room> available = hotelSystem.searchAvailableRooms(null, checkIn, checkOut);
            if (available.isEmpty()) {
                System.out.println("[Error] No rooms are available for the selected dates.");
                return;
            }
            System.out.println("\nAvailable Rooms for selected dates:");
            printRoomTable(available);

            System.out.print("Enter Room ID to reserve: ");
            String roomId = scanner.nextLine().trim().toUpperCase();

            // Confirmation of LKR 10,000 advance
            System.out.println("\n[Business Policy] A refundable booking advance of LKR 10,000 is charged now.");
            System.out.print("Confirm reservation and charge advance? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!confirm.equals("Y")) {
                System.out.println("Reservation cancelled by user.");
                return;
            }

            K2703047_Reservation reservation = hotelSystem.makeReservation(nic, roomId, checkIn, checkOut, guests);
            System.out.println("\n[SUCCESS] Reservation Confirmed!");
            System.out.println("Booking ID: " + reservation.getBookingId());

            // Generate invoice immediately
            K2703047_Invoice invoice = hotelSystem.generateInvoice(reservation.getBookingId());
            System.out.println(invoice.generateInvoiceText());

            // Auto-export invoice
            String invoicePath = "invoices/" + reservation.getBookingId() + ".txt";
            invoice.exportToFile(invoicePath);
            System.out.println("[Notice] Invoice automatically saved to: " + invoicePath);

        } catch (DateTimeParseException e) {
            System.out.println("[Error] Invalid date format. Please use YYYY-MM-DD.");
        } catch (Exception e) {
            System.out.println("[Error] Reservation failed: " + e.getMessage());
        }
    }

    private void handleSearchReservations() {
        printHeader("SEARCH RESERVATIONS");
        System.out.println(" 1. Search by Booking ID (Demonstrates O(log N) Binary Search)");
        System.out.println(" 2. Search by Guest Name");
        System.out.print("Select search type [1-2]: ");
        String opt = scanner.nextLine().trim();

        if (opt.equals("1")) {
            System.out.print("Enter Booking ID (e.g. BK-1001): ");
            String bid = scanner.nextLine().trim().toUpperCase();
            K2703047_Reservation r = hotelSystem.searchReservationBinary(bid);
            if (r != null) {
                System.out.println("\n[Found via Binary Search Algorithm]:");
                System.out.println(r);
                showInvoiceOption(r.getBookingId());
            } else {
                System.out.println("[Not Found] No reservation exists with Booking ID: " + bid);
            }
        } else if (opt.equals("2")) {
            System.out.print("Enter Guest Name or keyword: ");
            String name = scanner.nextLine().trim();
            List<K2703047_Reservation> results = hotelSystem.searchReservationsByGuestName(name);
            if (results.isEmpty()) {
                System.out.println("No reservations found for guest matching '" + name + "'.");
            } else {
                printReservationTable(results);
                System.out.print("Enter Booking ID to view invoice (or Enter to skip): ");
                String bid = scanner.nextLine().trim().toUpperCase();
                if (!bid.isEmpty()) {
                    showInvoiceOption(bid);
                }
            }
        }
    }

    private void handleViewByCheckInDate() {
        printHeader("VIEW RESERVATIONS BY CHECK-IN DATE");
        try {
            System.out.print("Enter Check-In Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
            List<K2703047_Reservation> list = hotelSystem.getReservationsByCheckInDate(date);
            if (list.isEmpty()) {
                System.out.println("No reservations scheduled to check in on " + date);
            } else {
                System.out.println("[Indexed via TreeMap Red-Black Tree] Found " + list.size() + " reservation(s):");
                printReservationTable(list);
            }
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void handleCancelReservation() {
        printHeader("CANCEL RESERVATION");
        System.out.print("Enter Booking ID to cancel: ");
        String bid = scanner.nextLine().trim().toUpperCase();
        K2703047_Reservation r = hotelSystem.getReservation(bid);
        if (r == null) {
            System.out.println("[Error] Reservation not found.");
            return;
        }

        System.out.println("Reservation Details:");
        System.out.println(r);
        System.out.println("\n[Business Policy Reminder] Reservations cannot be cancelled within 24 hours of check-in.");
        System.out.print("Are you sure you want to cancel this booking? (Y/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        boolean cancelled = hotelSystem.cancelReservation(bid);
        if (cancelled) {
            System.out.println("\n[Success] Reservation " + bid + " cancelled successfully!");
            System.out.println("[Notice] Refund of LKR 10,000 booking advance processed to " + r.getGuest().getFullName());
        } else {
            System.out.println("\n[Error] Cancellation Rejected: Reservation cannot be cancelled within 24 hours of check-in date or is already cancelled.");
        }
    }

    private void handleViewReservationsAndInvoices() {
        printHeader("ALL SYSTEM RESERVATIONS");
        List<K2703047_Reservation> all = hotelSystem.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("No reservations recorded yet.");
            return;
        }
        printReservationTable(all);

        System.out.print("\nEnter Booking ID to view detailed invoice (or Enter to return): ");
        String bid = scanner.nextLine().trim().toUpperCase();
        if (!bid.isEmpty()) {
            showInvoiceOption(bid);
        }
    }

    private void showInvoiceOption(String bookingId) {
        try {
            K2703047_Invoice invoice = hotelSystem.generateInvoice(bookingId);
            System.out.println(invoice.generateInvoiceText());
            System.out.print("Export invoice to file? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                String path = "invoices/" + bookingId + ".txt";
                invoice.exportToFile(path);
                System.out.println("Invoice saved to: " + path);
            }
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void launchGui() {
        System.out.println("\nLaunching SmartStay Desktop GUI...");
        new Thread(() -> {
            try {
                K2703047_GUIView.launch(hotelSystem);
            } catch (Exception e) {
                System.out.println("GUI error: " + e.getMessage());
            }
        }).start();
    }

    // Table printing helper utilities
    private void printRoomTable(List<K2703047_Room> rooms) {
        System.out.println("+----------+---------------+------------+-----------------+-------------------+---------------+");
        System.out.println("| Room ID  | Room Type     | Bed Type   | Daily Rate(LKR) | Breakfast Included| Status        |");
        System.out.println("+----------+---------------+------------+-----------------+-------------------+---------------+");
        for (K2703047_Room r : rooms) {
            System.out.printf("| %-8s | %-13s | %-10s | LKR %11.2f | %-17s | %-13s |\n",
                    r.getRoomId(),
                    r.getRoomType().getDisplayName(),
                    r.getBedType().getDisplayName(),
                    r.getDailyRate(),
                    r.hasComplimentaryBreakfast() ? "Yes" : "No",
                    r.getStatus().getDisplayName());
        }
        System.out.println("+----------+---------------+------------+-----------------+-------------------+---------------+");
    }

    private void printReservationTable(List<K2703047_Reservation> list) {
        System.out.println("+------------+--------------------+---------+--------------+--------------+--------+-------------+");
        System.out.println("| Booking ID | Guest Name         | Room ID | Check-In     | Check-Out    | Guests | Status      |");
        System.out.println("+------------+--------------------+---------+--------------+--------------+--------+-------------+");
        for (K2703047_Reservation r : list) {
            System.out.printf("| %-10s | %-18s | %-7s | %-12s | %-12s | %-6d | %-11s |\n",
                    r.getBookingId(),
                    r.getGuest().getFullName(),
                    r.getRoom().getRoomId(),
                    r.getCheckInDate(),
                    r.getCheckOutDate(),
                    r.getNumberOfGuests(),
                    r.getStatus().getDisplayName());
        }
        System.out.println("+------------+--------------------+---------+--------------+--------------+--------+-------------+");
    }

    private void printHeader(String title) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(" " + title);
        System.out.println("=".repeat(80));
    }
}
