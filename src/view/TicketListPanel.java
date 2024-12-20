package view;

import model.Flight;
import model.Passenger;
import model.Ticket;
import service.FlightService;
import service.TicketService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketListPanel extends JPanel{
    private final TicketService ticketService;
    private final FlightService flightService;
    private final UserService userService;
    private final JTable ticketTable;
    private final DefaultTableModel tableModel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final JTextField searchField;

    public TicketListPanel() {
        this.ticketService = new TicketService();
        this.flightService = new FlightService();
        this.userService = new UserService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search (Flight/Passenger):"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create table
        String[] columns = {"Ticket ID", "Flight Number", "Passenger Name", "Departure", 
                          "Destination", "Departure Time", "Seat Type", "Seat Number", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton cancelButton = new JButton("Cancel Selected Ticket");

        buttonPanel.add(cancelButton);

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        searchButton.addActionListener(e -> searchTickets());
        cancelButton.addActionListener(e -> cancelSelectedTicket());

        // Load initial data
        refreshTickets();
    }

    private void refreshTickets() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            List<Passenger> passengers = userService.getAllPassengers();
            List<Ticket> tickets = ticketService.getAllTickets();
            updateTableModel(tickets, flights, passengers);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchTickets() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        try {
            List<Flight> flights = flightService.getAllFlights();
            List<Passenger> passengers = userService.getAllPassengers();
            List<Ticket> allTickets = ticketService.getAllTickets();
            
            List<Ticket> filteredTickets = allTickets.stream()
                .filter(ticket -> {
                    Flight flight = flights.stream()
                        .filter(f -> f.getId() == ticket.getFlightId())
                        .findFirst()
                        .orElse(null);
                    Passenger passenger = passengers.stream()
                        .filter(u -> u.getId() == ticket.getUserId())
                        .findFirst()
                        .orElse(null);
                    
                    if (flight == null || passenger == null) return false;
                    
                    return flight.getFlightNumber().toLowerCase().contains(searchTerm) ||
                           flight.getDeparture().toLowerCase().contains(searchTerm) ||
                           flight.getDestination().toLowerCase().contains(searchTerm) ||
                           passenger.getName().toLowerCase().contains(searchTerm) ||
                           passenger.getSurname().toLowerCase().contains(searchTerm) ||
                           ticket.getSeatNumber().toLowerCase().contains(searchTerm);
                })
                .toList();
            
            updateTableModel(filteredTickets, flights, passengers);
            
            if (filteredTickets.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No tickets found matching the search criteria",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error searching tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTableModel(List<Ticket> tickets, List<Flight> flights, List<Passenger> passengers) {
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            Flight flight = flights.stream()
                .filter(f -> f.getId() == ticket.getFlightId())
                .findFirst()
                .orElse(null);
            Passenger passenger = passengers.stream()
                .filter(u -> u.getId() == ticket.getUserId())
                .findFirst()
                .orElse(null);
            
            if (flight != null && passenger != null) {
                tableModel.addRow(new Object[]{
                    ticket.getId(),
                    flight.getFlightNumber(),
                    passenger.getName() + " " + passenger.getSurname(),
                    flight.getDeparture(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(formatter),
                    ticket.getSeatType(),
                    ticket.getSeatNumber(),
                    String.format("$%.2f", ticket.getPrice())
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

        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        String flightNumber = (String) tableModel.getValueAt(selectedRow, 1);
        String passengerName = (String) tableModel.getValueAt(selectedRow, 2);

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel ticket for flight " + flightNumber + 
            " booked by " + passengerName + "?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                ticketService.cancelTicket(ticketId);
                refreshTickets();
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
} 