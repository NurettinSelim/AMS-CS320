package view;

import model.Flight;
import model.User;
import service.FlightService;
import service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightListPanel extends JPanel implements IFlightListPanel {
    private final User currentUser;
    private final FlightService flightService;
    private final TicketService ticketService;
    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private final JTextField departureField;
    private final JTextField destinationField;
    private final JTextField timeField;
    private final JComboBox<String> seatTypeComboBox;
    private final MainFrame mainFrame;

    public FlightListPanel(User currentUser, MainFrame mainFrame) {
        this.currentUser = currentUser;
        this.mainFrame = mainFrame;
        this.flightService = new FlightService();
        this.ticketService = new TicketService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        departureField = new JTextField(12);
        destinationField = new JTextField(12);
        timeField = new HintTextField("yyyy-MM-dd HH:mm", 13);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("From:"));
        searchPanel.add(departureField);
        searchPanel.add(new JLabel("To:"));
        searchPanel.add(destinationField);
        searchPanel.add(new JLabel("Date/Time:"));
        searchPanel.add(timeField);
        searchPanel.add(searchButton);

        String[] columns = {"Flight Number", "Departure", "Destination", "Departure Time",
                "Arrival Time", "Economy Price", "Business Price",
                "Economy Seats", "Business Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);

        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seatTypeComboBox = new JComboBox<>(new String[]{"ECONOMY", "BUSINESS"});
        JButton bookButton = new JButton("Book Selected Flight");

        bookingPanel.add(new JLabel("Seat Type:"));
        bookingPanel.add(seatTypeComboBox);
        bookingPanel.add(bookButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bookingPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchFlights());
        bookButton.addActionListener(e -> bookSelectedFlight());

        refreshFlights();
    }

    @Override
    public void refreshFlights() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            updateTableModel(flights);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading flights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void searchFlights() {
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();

        if (departure.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both departure and destination",
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDateTime departureTime = LocalDateTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            List<Flight> flights = flightService.searchFlights(departure, destination, departureTime);
            updateTableModel(flights);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching flights: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void updateTableModel(List<Flight> flights) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Flight flight : flights) {
            tableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getDeparture(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(formatter),
                    flight.getArrivalTime().format(formatter),
                    String.format("$%.2f", flight.getEconomyPrice()),
                    String.format("$%.2f", flight.getBusinessPrice()),
                    flight.getEconomySeatsAvailable(),
                    flight.getBusinessSeatsAvailable()
            });
        }
    }

    @Override
    public void bookSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to book",
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
            String seatType = (String) seatTypeComboBox.getSelectedItem();

            List<Flight> flights = flightService.getAllFlights();
            Flight selectedFlight = flights.stream()
                    .filter(f -> f.getFlightNumber().equals(flightNumber))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Flight not found"));

            int availableSeats = seatType.equals("ECONOMY")
                    ? selectedFlight.getEconomySeatsAvailable()
                    : selectedFlight.getBusinessSeatsAvailable();

            if (availableSeats <= 0) {
                JOptionPane.showMessageDialog(this,
                        "No " + seatType.toLowerCase() + " seats available",
                        "Booking Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ticketService.purchaseTicket(selectedFlight.getId(), currentUser.getId(), seatType);

            JOptionPane.showMessageDialog(this,
                    "Ticket booked successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshFlights();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error booking ticket: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}