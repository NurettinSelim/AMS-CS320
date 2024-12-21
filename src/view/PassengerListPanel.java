package view;

import model.Flight;
import model.Ticket;
import model.User;
import service.FlightService;
import service.TicketService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassengerListPanel extends JPanel {
    private final UserService userService;
    private final TicketService ticketService;
    private final FlightService flightService;
    private final JTable passengerTable;
    private final JTable ticketTable;
    private final DefaultTableModel passengerTableModel;
    private final DefaultTableModel ticketTableModel;
    private final JTextField searchField;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PassengerListPanel() {
        this.userService = new UserService();
        this.ticketService = new TicketService();
        this.flightService = new FlightService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create split pane for passengers and tickets
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Equal split

        // Create passenger panel
        JPanel passengerPanel = new JPanel(new BorderLayout(5, 5));
        passengerPanel.setBorder(BorderFactory.createTitledBorder("Passengers"));

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete Selected Passenger");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(deleteButton);

        // Create passenger table
        String[] passengerColumns = {"ID", "Name", "Surname", "Email"};
        passengerTableModel = new DefaultTableModel(passengerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengerTable = new JTable(passengerTableModel);
        JScrollPane passengerScrollPane = new JScrollPane(passengerTable);

        // Add components to passenger panel
        passengerPanel.add(searchPanel, BorderLayout.NORTH);
        passengerPanel.add(passengerScrollPane, BorderLayout.CENTER);

        // Create tickets panel
        JPanel ticketsPanel = new JPanel(new BorderLayout(5, 5));
        ticketsPanel.setBorder(BorderFactory.createTitledBorder("Passenger's Tickets"));

        // Create ticket table
        String[] ticketColumns = {"Ticket ID", "Flight Number", "Departure",
                "Destination", "Departure Time", "Seat Type",
                "Seat Number", "Price"};
        ticketTableModel = new DefaultTableModel(ticketColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(ticketTableModel);
        JScrollPane ticketScrollPane = new JScrollPane(ticketTable);

        // Create ticket actions panel
        JPanel ticketActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton cancelTicketButton = new JButton("Cancel Selected Ticket");
        ticketActionsPanel.add(cancelTicketButton);

        // Add components to tickets panel
        ticketsPanel.add(ticketScrollPane, BorderLayout.CENTER);
        ticketsPanel.add(ticketActionsPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setTopComponent(passengerPanel);
        splitPane.setBottomComponent(ticketsPanel);

        // Add split pane to main panel
        add(splitPane, BorderLayout.CENTER);

        // Add action listeners
        //searchButton.addActionListener(e -> searchPassengers());
        deleteButton.addActionListener(e -> deleteSelectedPassenger());
        passengerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedPassengerTickets();
            }
        });
        cancelTicketButton.addActionListener(e -> cancelSelectedTicket());

        // Load initial data
        refreshPassengers();
    }

    private void refreshPassengers() {
        try {
            List<User> users = userService.getAllUsers();
            updateUserTableModel(users);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading passengers: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchPassengers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        try {
            List<User> allPassengers = userService.getAllPassengers();
            List<User> filteredPassengers = allPassengers.stream()
                    .filter(p -> !p.getRole().equals("manager") && // Only show non-admin users
                            (p.getName().toLowerCase().contains(searchTerm) ||
                                    p.getSurname().toLowerCase().contains(searchTerm) ||
                                    p.getEmail().toLowerCase().contains(searchTerm)))
                    .toList();
            updateUserTableModel(filteredPassengers);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching passengers: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateUserTableModel(List<User> users) {
        passengerTableModel.setRowCount(0);
        for (User user : users) {
            if (user.getRole().equals("passenger")) { // Only show non-admin users
                passengerTableModel.addRow(new Object[]{
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail()

                });
            }
        }
    }

    private void loadSelectedPassengerTickets() {
        int selectedRow = passengerTable.getSelectedRow();
        if (selectedRow == -1) {
            ticketTableModel.setRowCount(0);
            return;
        }

        int passengerId = (int) passengerTableModel.getValueAt(selectedRow, 0);

        try {
            List<Ticket> tickets = ticketService.getTicketsByUser(passengerId);
            List<Flight> flights = flightService.getAllFlights();
            updateTicketTableModel(tickets, flights);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading tickets: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTicketTableModel(List<Ticket> tickets, List<Flight> flights) {
        ticketTableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            Flight flight = flights.stream()
                    .filter(f -> f.getId() == ticket.getFlightId())
                    .findFirst()
                    .orElse(null);

            if (flight != null) {
                ticketTableModel.addRow(new Object[]{
                        ticket.getId(),
                        flight.getFlightNumber(),
                        flight.getDeparture(),
                        flight.getDestination(),
                        flight.getDepartureTime().format(formatter),
                        ticket.getSeatType(),
                        ticket.getSeatNumber(),
                        String.format("$%.2f", ticket.getPrice()),
                });
            }
        }
    }

    private void cancelSelectedTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a ticket to cancel",
                    "Cancel Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ticketId = (int) ticketTableModel.getValueAt(selectedRow, 7);
        String flightNumber = (String) ticketTableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel ticket for flight " + flightNumber + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                ticketService.cancelTicket(ticketId);
                loadSelectedPassengerTickets(); // Refresh tickets
                JOptionPane.showMessageDialog(this,
                        "Ticket cancelled successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error cancelling ticket: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deleteSelectedPassenger() {
        int selectedRow = passengerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a passenger to delete",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int passengerId = (int) passengerTableModel.getValueAt(selectedRow, 0);
        String passengerName = passengerTableModel.getValueAt(selectedRow, 1) + " " +
                passengerTableModel.getValueAt(selectedRow, 2);

        try {
            // Check if passenger has tickets
            List<Ticket> tickets = ticketService.getTicketsByUser(passengerId);
            if (!tickets.isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "This passenger has " + tickets.size() + " ticket(s). " +
                                "Deleting the passenger will also cancel all their tickets.\n" +
                                "Are you sure you want to delete " + passengerName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    // Cancel all tickets first
                    for (Ticket ticket : tickets) {
                        ticketService.cancelTicket(ticket.getId());
                    }
                } else {
                    return;
                }
            } else {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete " + passengerName + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Delete the passenger
            userService.deleteUser(passengerId);

            // Refresh the UI
            refreshPassengers();
            ticketTableModel.setRowCount(0); // Clear ticket table

            JOptionPane.showMessageDialog(this,
                    "Passenger deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting passenger: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
