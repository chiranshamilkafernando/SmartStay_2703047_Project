package k2703047.smartstay.ui;

import k2703047.smartstay.model.K2703047_BedType;
import k2703047.smartstay.model.K2703047_Guest;
import k2703047.smartstay.model.K2703047_Invoice;
import k2703047.smartstay.model.K2703047_Reservation;
import k2703047.smartstay.model.K2703047_ReservationStatus;
import k2703047.smartstay.model.K2703047_Room;
import k2703047.smartstay.model.K2703047_RoomFactory;
import k2703047.smartstay.model.K2703047_RoomStatus;
import k2703047.smartstay.model.K2703047_RoomType;
import k2703047.smartstay.service.K2703047_HotelSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * 
 * Desktop Graphical User Interface (Java Swing) for SmartStay Hotel Booking System.
 * Evaluates UI/UX design:
 * - Professional layout with tabs, tables, status indicators, and responsive panels
 * - Form validation and clear feedback dialogs
 * - Live billing estimator and printable/exportable invoice preview
 */
public class K2703047_GUIView extends JFrame {

    private final K2703047_HotelSystem hotelSystem;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Table Models
    private DefaultTableModel roomTableModel;
    private DefaultTableModel guestTableModel;
    private DefaultTableModel reservationTableModel;
    private DefaultTableModel searchResultsTableModel;

    // Component references
    private JTable roomTable;
    private JTable guestTable;
    private JTable reservationTable;
    private JTable searchResultsTable;
    private JTextArea invoiceTextArea;
    private JComboBox<String> guestSelectorCombo;
    private JComboBox<String> invoiceSelectorCombo;

    public K2703047_GUIView(K2703047_HotelSystem hotelSystem) {
        super("SmartStay Hotel Chain - Room Reservation System | Student ID: 2703047");
        this.hotelSystem = hotelSystem;
        initializeUI();
        refreshAllData();
    }

    public static void launch(K2703047_HotelSystem hotelSystem) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            K2703047_GUIView frame = new K2703047_GUIView(hotelSystem);
            frame.setVisible(true);
        });
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel("SMARTSTAY HOTEL CHAIN - SRI LANKA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("CI6115 Programming III | Patterns & Algorithms | Student ID: 2703047");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(200, 220, 255));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabbedPane.addTab("  Rooms (Admin)  ", createRoomsPanel());
        tabbedPane.addTab("  Guest Registration  ", createGuestPanel());
        tabbedPane.addTab("  Search & Book  ", createBookingPanel());
        tabbedPane.addTab("  Reservations & Cancellation  ", createReservationManagementPanel());
        tabbedPane.addTab("  Detailed Invoices  ", createInvoicePanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel statusLabel = new JLabel("Ready. System loaded with initial rooms, packages, and registered guests.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);

        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // ==========================================
    // TAB 1: ROOMS MANAGEMENT PANEL (ADMIN)
    // ==========================================
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table
        String[] cols = {"Room ID", "Room Type", "Bed Type", "Daily Rate (LKR)", "Breakfast", "Capacity", "Status"};
        roomTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setRowHeight(26);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        alignTableColumns(roomTable, 0, 1, 2, 4, 5, 6);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JButton btnAdd = new JButton("Add Room");
        JButton btnUpdateRate = new JButton("Update Rate");
        JButton btnStatus = new JButton("Change Status");
        JButton btnRemove = new JButton("Remove Room");
        JButton btnRefresh = new JButton("Refresh");

        toolbar.add(btnAdd);
        toolbar.add(btnUpdateRate);
        toolbar.add(btnStatus);
        toolbar.add(btnRemove);
        toolbar.add(btnRefresh);

        panel.add(toolbar, BorderLayout.SOUTH);

        // Action Listeners
        btnAdd.addActionListener(e -> showAddRoomDialog());
        btnUpdateRate.addActionListener(e -> showUpdateRoomDialog());
        btnStatus.addActionListener(e -> showChangeStatusDialog());
        btnRemove.addActionListener(e -> handleRemoveRoomGUI());
        btnRefresh.addActionListener(e -> refreshRoomsTable());

        return panel;
    }

    private void showAddRoomDialog() {
        JTextField idField = new JTextField(10);
        JComboBox<K2703047_RoomType> typeCombo = new JComboBox<>(K2703047_RoomType.values());
        JComboBox<K2703047_BedType> bedCombo = new JComboBox<>(K2703047_BedType.values());
        JTextField rateField = new JTextField(10);

        typeCombo.addActionListener(e -> {
            K2703047_RoomType t = (K2703047_RoomType) typeCombo.getSelectedItem();
            if (t != null) rateField.setText(String.valueOf(t.getDailyRate()));
        });
        typeCombo.setSelectedIndex(0);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Room ID (e.g. R-105):"));
        form.add(idField);
        form.add(new JLabel("Room Type:"));
        form.add(typeCombo);
        form.add(new JLabel("Bed Type:"));
        form.add(bedCombo);
        form.add(new JLabel("Daily Rate (LKR):"));
        form.add(rateField);

        int res = JOptionPane.showConfirmDialog(this, form, "Add New Room", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim().toUpperCase();
                K2703047_RoomType type = (K2703047_RoomType) typeCombo.getSelectedItem();
                K2703047_BedType bed = (K2703047_BedType) bedCombo.getSelectedItem();
                double rate = Double.parseDouble(rateField.getText().trim());

                if (hotelSystem.getRoomById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Room ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                K2703047_Room room = K2703047_RoomFactory.createRoom(id, type, bed, rate, K2703047_RoomStatus.AVAILABLE, type.getStandardCapacity());
                hotelSystem.addRoom(room);
                refreshRoomsTable();
                JOptionPane.showMessageDialog(this, "Room " + id + " added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateRoomDialog() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomId = (String) roomTableModel.getValueAt(row, 0);
        K2703047_Room room = hotelSystem.getRoomById(roomId);
        if (room == null) return;

        String input = JOptionPane.showInputDialog(this, "Enter new Daily Rate (LKR):", room.getDailyRate());
        if (input != null && !input.trim().isEmpty()) {
            try {
                double newRate = Double.parseDouble(input.trim());
                room.setDailyRate(newRate);
                refreshRoomsTable();
                JOptionPane.showMessageDialog(this, "Rate updated for room " + roomId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid rate: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChangeStatusDialog() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to modify status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomId = (String) roomTableModel.getValueAt(row, 0);
        K2703047_RoomStatus current = (K2703047_RoomStatus) roomTableModel.getValueAt(row, 6);

        K2703047_RoomStatus selected = (K2703047_RoomStatus) JOptionPane.showInputDialog(
                this, "Select new availability status for " + roomId + ":",
                "Change Room Status", JOptionPane.QUESTION_MESSAGE, null,
                K2703047_RoomStatus.values(), current);

        if (selected != null) {
            hotelSystem.updateRoomStatus(roomId, selected);
            refreshRoomsTable();
            JOptionPane.showMessageDialog(this, "Room " + roomId + " status set to " + selected);
        }
    }

    private void handleRemoveRoomGUI() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomId = (String) roomTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove room " + roomId + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                hotelSystem.removeRoom(roomId);
                refreshRoomsTable();
                JOptionPane.showMessageDialog(this, "Room " + roomId + " removed.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Remove", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 2: GUEST MANAGEMENT PANEL
    // ==========================================
    private JPanel createGuestPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Form
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Register New Customer"));

        JTextField nicField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        formPanel.add(new JLabel("NIC / Passport:"));
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(new JLabel("Email Address:"));

        formPanel.add(nicField);
        formPanel.add(nameField);
        formPanel.add(phoneField);
        formPanel.add(emailField);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(formPanel, BorderLayout.CENTER);

        JButton btnRegister = new JButton("Register Customer");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.add(btnRegister);
        formWrapper.add(btnWrapper, BorderLayout.SOUTH);

        panel.add(formWrapper, BorderLayout.NORTH);

        // Guest Table
        String[] cols = {"NIC / Passport", "Full Name", "Contact Number", "Email Address"};
        guestTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        guestTable = new JTable(guestTableModel);
        guestTable.setRowHeight(24);
        panel.add(new JScrollPane(guestTable), BorderLayout.CENTER);

        btnRegister.addActionListener(e -> {
            try {
                String nic = nicField.getText().trim().toUpperCase();
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();

                boolean ok = hotelSystem.registerGuest(nic, name, phone, email);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Guest registered successfully!");
                    nicField.setText("");
                    nameField.setText("");
                    phoneField.setText("");
                    emailField.setText("");
                    refreshGuestsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Guest already registered with this NIC/Passport.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Validation Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ==========================================
    // TAB 3: SEARCH & BOOKING PANEL
    // ==========================================
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search Criteria Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Available Rooms"));

        JComboBox<String> typeFilterCombo = new JComboBox<>(new String[]{"All Room Types", "Standard", "Deluxe", "Suite", "Family Suite"});
        LocalDate minCheckIn = LocalDate.now().plusDays(2);
        JTextField inDateField = new JTextField(minCheckIn.format(dateFormatter), 9);
        JTextField outDateField = new JTextField(minCheckIn.plusDays(3).format(dateFormatter), 9);
        JButton btnSearch = new JButton("Find Available Rooms");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchPanel.add(new JLabel("Room Type:"));
        searchPanel.add(typeFilterCombo);
        searchPanel.add(new JLabel("Check-In (Min 2 days advance):"));
        searchPanel.add(inDateField);
        searchPanel.add(new JLabel("Check-Out:"));
        searchPanel.add(outDateField);
        searchPanel.add(btnSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Search Results Table
        String[] cols = {"Room ID", "Type", "Bed", "Daily Rate (LKR)", "Breakfast Included", "Base Capacity"};
        searchResultsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        searchResultsTable = new JTable(searchResultsTableModel);
        searchResultsTable.setRowHeight(24);
        panel.add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);

        // Booking Action Panel at Bottom
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Complete Room Reservation"));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 6));
        guestSelectorCombo = new JComboBox<>();
        JSpinner guestCountSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        JButton btnBook = new JButton("Confirm Booking & Pay LKR 10,000 Advance");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBook.setBackground(new Color(24, 134, 75));
        btnBook.setForeground(Color.WHITE);

        inputRow.add(new JLabel("Select Registered Guest:"));
        inputRow.add(guestSelectorCombo);
        inputRow.add(new JLabel("Number of Guests:"));
        inputRow.add(guestCountSpinner);
        inputRow.add(btnBook);

        JLabel policyLabel = new JLabel("  * Refundable advance of LKR 10,000 is charged at reservation. Free cancellation until 24h prior to check-in.");
        policyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        policyLabel.setForeground(new Color(80, 80, 80));

        actionPanel.add(inputRow);
        actionPanel.add(policyLabel);

        panel.add(actionPanel, BorderLayout.SOUTH);

        // Action Listener for Search
        btnSearch.addActionListener(e -> {
            try {
                LocalDate inDate = LocalDate.parse(inDateField.getText().trim(), dateFormatter);
                LocalDate outDate = LocalDate.parse(outDateField.getText().trim(), dateFormatter);

                int typeIdx = typeFilterCombo.getSelectedIndex();
                K2703047_RoomType filterType = null;
                if (typeIdx == 1) filterType = K2703047_RoomType.STANDARD;
                else if (typeIdx == 2) filterType = K2703047_RoomType.DELUXE;
                else if (typeIdx == 3) filterType = K2703047_RoomType.SUITE;
                else if (typeIdx == 4) filterType = K2703047_RoomType.FAMILY_SUITE;

                List<K2703047_Room> results = hotelSystem.searchAvailableRooms(filterType, inDate, outDate);
                searchResultsTableModel.setRowCount(0);
                for (K2703047_Room r : results) {
                    searchResultsTableModel.addRow(new Object[]{
                            r.getRoomId(),
                            r.getRoomType().getDisplayName(),
                            r.getBedType().getDisplayName(),
                            String.format("LKR %,.2f", r.getDailyRate()),
                            r.hasComplimentaryBreakfast() ? "Yes" : "No",
                            r.getBaseCapacity()
                    });
                }
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No available rooms for the selected dates.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action Listener for Booking
        btnBook.addActionListener(e -> {
            int row = searchResultsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an available room from the search results table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (guestSelectorCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Please select or register a guest first.", "Guest Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String roomId = (String) searchResultsTableModel.getValueAt(row, 0);
                String selectedGuest = (String) guestSelectorCombo.getSelectedItem();
                String nic = selectedGuest.substring(selectedGuest.indexOf("[") + 1, selectedGuest.indexOf("]"));

                LocalDate inDate = LocalDate.parse(inDateField.getText().trim(), dateFormatter);
                LocalDate outDate = LocalDate.parse(outDateField.getText().trim(), dateFormatter);
                int guestCount = (int) guestCountSpinner.getValue();

                K2703047_Reservation res = hotelSystem.makeReservation(nic, roomId, inDate, outDate, guestCount);
                refreshAllData();

                JOptionPane.showMessageDialog(this,
                        "Reservation confirmed successfully!\n" +
                        "Booking ID: " + res.getBookingId() + "\n" +
                        "Refundable Advance Charged: LKR 10,000\n\n" +
                        "View detailed invoice in the 'Detailed Invoices' tab.",
                        "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);

                // Auto-show invoice
                displayInvoiceForBooking(res.getBookingId());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Booking Failed: " + ex.getMessage(), "Reservation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ==========================================
    // TAB 4: RESERVATIONS & CANCELLATION PANEL
    // ==========================================
    private JPanel createReservationManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Filter / Search Toolbar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search & Filter Reservations"));

        JTextField searchField = new JTextField(12);
        JButton btnSearchName = new JButton("Search Guest Name");
        JButton btnBinarySearchId = new JButton("Binary Search ID");
        JTextField checkInDateField = new JTextField(9);
        JButton btnFilterDate = new JButton("Filter by Check-In Date");
        JButton btnShowAll = new JButton("Show All");

        searchBar.add(new JLabel("Keyword / ID:"));
        searchBar.add(searchField);
        searchBar.add(btnSearchName);
        searchBar.add(btnBinarySearchId);
        searchBar.add(new JLabel("Check-In:"));
        searchBar.add(checkInDateField);
        searchBar.add(btnFilterDate);
        searchBar.add(btnShowAll);

        panel.add(searchBar, BorderLayout.NORTH);

        // Reservations Table
        String[] cols = {"Booking ID", "Guest Name", "Room ID", "Check-In", "Check-Out", "Nights", "Guests", "Status"};
        reservationTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setRowHeight(24);
        panel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        JButton btnCancel = new JButton("Cancel Selected Reservation");
        btnCancel.setForeground(Color.RED);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnViewInvoice = new JButton("View Detailed Invoice");
        btnViewInvoice.setFont(new Font("Segoe UI", Font.BOLD, 12));

        bottomBar.add(btnCancel);
        bottomBar.add(btnViewInvoice);
        panel.add(bottomBar, BorderLayout.SOUTH);

        // Action Listeners
        btnSearchName.addActionListener(e -> {
            String kw = searchField.getText().trim();
            List<K2703047_Reservation> list = hotelSystem.searchReservationsByGuestName(kw);
            populateReservationTable(list);
        });

        btnBinarySearchId.addActionListener(e -> {
            String bid = searchField.getText().trim().toUpperCase();
            K2703047_Reservation r = hotelSystem.searchReservationBinary(bid);
            if (r != null) {
                populateReservationTable(List.of(r));
                JOptionPane.showMessageDialog(this, "Found reservation via O(log N) Binary Search Algorithm!", "Binary Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found with Booking ID: " + bid, "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnFilterDate.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(checkInDateField.getText().trim(), dateFormatter);
                List<K2703047_Reservation> list = hotelSystem.getReservationsByCheckInDate(date);
                populateReservationTable(list);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnShowAll.addActionListener(e -> refreshReservationsTable());

        btnCancel.addActionListener(e -> {
            int row = reservationTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bid = (String) reservationTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel booking " + bid + "?\n" +
                    "Notice: Cancellation is strictly prohibited within 24 hours of check-in.",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = hotelSystem.cancelReservation(bid);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Reservation " + bid + " cancelled successfully.\n" +
                            "Refund of LKR 10,000 booking advance processed.",
                            "Cancellation Approved", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cancellation Rejected!\n" +
                            "Business rule violation: Reservations cannot be modified or cancelled within 24 hours of check-in.",
                            "Cancellation Policy Violation", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnViewInvoice.addActionListener(e -> {
            int row = reservationTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to view its invoice.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bid = (String) reservationTableModel.getValueAt(row, 0);
            displayInvoiceForBooking(bid);
        });

        return panel;
    }

    // ==========================================
    // TAB 5: INVOICE VIEWER PANEL
    // ==========================================
    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Selector Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        invoiceSelectorCombo = new JComboBox<>();
        JButton btnLoad = new JButton("Load Invoice");
        JButton btnSaveFile = new JButton("Export Invoice to Text File");
        JButton btnCopy = new JButton("Copy to Clipboard");

        topBar.add(new JLabel("Select Booking:"));
        topBar.add(invoiceSelectorCombo);
        topBar.add(btnLoad);
        topBar.add(btnSaveFile);
        topBar.add(btnCopy);

        panel.add(topBar, BorderLayout.NORTH);

        // Monospace Text Area for Invoice
        invoiceTextArea = new JTextArea();
        invoiceTextArea.setEditable(false);
        invoiceTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        invoiceTextArea.setBackground(new Color(250, 252, 255));
        invoiceTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(invoiceTextArea), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> {
            String selected = (String) invoiceSelectorCombo.getSelectedItem();
            if (selected != null && selected.contains(" ")) {
                String bid = selected.split(" ")[0];
                displayInvoiceForBooking(bid);
            }
        });

        btnSaveFile.addActionListener(e -> {
            String selected = (String) invoiceSelectorCombo.getSelectedItem();
            if (selected != null && selected.contains(" ")) {
                String bid = selected.split(" ")[0];
                try {
                    K2703047_Invoice inv = hotelSystem.generateInvoice(bid);
                    String path = "invoices/" + bid + ".txt";
                    inv.exportToFile(path);
                    JOptionPane.showMessageDialog(this, "Invoice exported successfully to:\n" + path, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCopy.addActionListener(e -> {
            String text = invoiceTextArea.getText();
            if (!text.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "Invoice text copied to clipboard!");
            }
        });

        return panel;
    }

    private void displayInvoiceForBooking(String bookingId) {
        try {
            K2703047_Invoice inv = hotelSystem.generateInvoice(bookingId);
            invoiceTextArea.setText(inv.generateInvoiceText());
            invoiceTextArea.setCaretPosition(0);
        } catch (Exception e) {
            invoiceTextArea.setText("Error generating invoice: " + e.getMessage());
        }
    }

    // Refresh Data Helpers
    public void refreshAllData() {
        refreshRoomsTable();
        refreshGuestsTable();
        refreshReservationsTable();
        refreshCombos();
    }

    private void refreshRoomsTable() {
        roomTableModel.setRowCount(0);
        for (K2703047_Room r : hotelSystem.getAllRooms()) {
            roomTableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomType().getDisplayName(),
                    r.getBedType().getDisplayName(),
                    String.format("%,.2f", r.getDailyRate()),
                    r.hasComplimentaryBreakfast() ? "Yes" : "No",
                    r.getBaseCapacity(),
                    r.getStatus()
            });
        }
    }

    private void refreshGuestsTable() {
        guestTableModel.setRowCount(0);
        for (K2703047_Guest g : hotelSystem.getAllGuests()) {
            guestTableModel.addRow(new Object[]{
                    g.getNicOrPassport(),
                    g.getFullName(),
                    g.getContactNumber(),
                    g.getEmailAddress()
            });
        }
    }

    private void refreshReservationsTable() {
        populateReservationTable(hotelSystem.getAllReservations());
    }

    private void populateReservationTable(List<K2703047_Reservation> list) {
        reservationTableModel.setRowCount(0);
        for (K2703047_Reservation r : list) {
            reservationTableModel.addRow(new Object[]{
                    r.getBookingId(),
                    r.getGuest().getFullName(),
                    r.getRoom().getRoomId(),
                    r.getCheckInDate(),
                    r.getCheckOutDate(),
                    r.getNumberOfNights(),
                    r.getNumberOfGuests(),
                    r.getStatus().getDisplayName()
            });
        }
    }

    private void refreshCombos() {
        if (guestSelectorCombo != null) {
            guestSelectorCombo.removeAllItems();
            for (K2703047_Guest g : hotelSystem.getAllGuests()) {
                guestSelectorCombo.addItem(g.getFullName() + " [" + g.getNicOrPassport() + "]");
            }
        }
        if (invoiceSelectorCombo != null) {
            invoiceSelectorCombo.removeAllItems();
            for (K2703047_Reservation r : hotelSystem.getAllReservations()) {
                invoiceSelectorCombo.addItem(r.getBookingId() + " - " + r.getGuest().getFullName() + " (" + r.getRoom().getRoomId() + ")");
            }
            if (invoiceSelectorCombo.getItemCount() > 0 && invoiceTextArea.getText().isEmpty()) {
                String first = invoiceSelectorCombo.getItemAt(0);
                displayInvoiceForBooking(first.split(" ")[0]);
            }
        }
    }

    private void alignTableColumns(JTable table, int... centerCols) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c : centerCols) {
            table.getColumnModel().getColumn(c).setCellRenderer(center);
        }
    }
}
