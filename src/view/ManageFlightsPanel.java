package view;

import model.Flight;
import model.Plane;
import service.FlightService;
import service.PlaneService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageFlightsPanel extends JPanel {
    private final FlightService flightService;
    private final PlaneService planeService;
    private final JTable flightTable;
    private final DefaultTableModel tableModel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ManageFlightsPanel() {
        this.flightService = new FlightService();
        this.planeService = new PlaneService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"Flight Number", "Departure", "Destination", "Departure Time",
                "Arrival Time", "Plane", "Economy Price", "Business Price",
                "Economy Seats", "Business Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Flight");
        JButton editButton = new JButton("Edit Flight");
        JButton deleteButton = new JButton("Delete Flight");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Add components to panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> showAddFlightDialog());
        editButton.addActionListener(e -> editSelectedFlight());
        deleteButton.addActionListener(e -> deleteSelectedFlight());

        // Load initial data
        refreshFlights();
    }

    private void refreshFlights() {
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

    private void updateTableModel(List<Flight> flights) {
        tableModel.setRowCount(0);
        try {
            for (Flight flight : flights) {
                Plane plane = planeService.getPlaneById(flight.getPlaneId());

                tableModel.addRow(new Object[]{
                        flight.getFlightNumber(),
                        flight.getDeparture(),
                        flight.getDestination(),
                        flight.getDepartureTime().toString(),
                        flight.getArrivalTime().toString(),
                        plane.getPlaneName(),
                        String.format("$%.2f", flight.getEconomyPrice()),
                        String.format("$%.2f", flight.getBusinessPrice()),
                        flight.getEconomySeatsAvailable(),
                        flight.getBusinessSeatsAvailable()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddFlightDialog() {
        try {
            List<Plane> planes = planeService.getAllPlanes();
            if (planes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add planes before creating flights",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Flight", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Create form fields
            JTextField flightNumberField = new JTextField();
            JTextField departureField = new JTextField();
            JTextField destinationField = new JTextField();
            JTextField departureDateField = new JTextField("YYYY-MM-DD HH:mm");
            JTextField arrivalDateField = new JTextField("YYYY-MM-DD HH:mm");
            JComboBox<String> planeComboBox = new JComboBox<>(
                    planes.stream()
                            .map(Plane::getPlaneName)
                            .toArray(String[]::new)
            );
            JSpinner economyPriceSpinner = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 10000.0, 10.0));
            JSpinner businessPriceSpinner = new JSpinner(new SpinnerNumberModel(200.0, 0.0, 20000.0, 10.0));
            JSpinner economyCapacitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 5));
            JSpinner businessCapacitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 500, 5));

            // Add form fields to panel
            formPanel.add(new JLabel("Flight Number:"));
            formPanel.add(flightNumberField);
            formPanel.add(new JLabel("Departure:"));
            formPanel.add(departureField);
            formPanel.add(new JLabel("Destination:"));
            formPanel.add(destinationField);
            formPanel.add(new JLabel("Departure Time:"));
            formPanel.add(departureDateField);
            formPanel.add(new JLabel("Arrival Time:"));
            formPanel.add(arrivalDateField);
            formPanel.add(new JLabel("Plane:"));
            formPanel.add(planeComboBox);
            formPanel.add(new JLabel("Economy Price:"));
            formPanel.add(economyPriceSpinner);
            formPanel.add(new JLabel("Business Price:"));
            formPanel.add(businessPriceSpinner);
            formPanel.add(new JLabel("Economy Seats:"));
            formPanel.add(economyCapacitySpinner);
            formPanel.add(new JLabel("Business Seats:"));
            formPanel.add(businessCapacitySpinner);

            // Create buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            saveButton.addActionListener(e -> {
                try {
                    String flightNumber = flightNumberField.getText();
                    String departure = departureField.getText().trim();
                    String destination = destinationField.getText().trim();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime departureTime = LocalDateTime.parse(departureDateField.getText(), formatter);
                    LocalDateTime arrivalTime = LocalDateTime.parse(arrivalDateField.getText(), formatter);  // Append seconds if not included
                    String selectedPlaneName = (String) planeComboBox.getSelectedItem();
                    double economyPrice = (double) economyPriceSpinner.getValue();
                    double businessPrice = (double) businessPriceSpinner.getValue();
                    int economyCapacity = (int) economyCapacitySpinner.getValue();
                    int businessCapacity = (int) businessCapacitySpinner.getValue();

                    if (flightNumber.isEmpty() || departure.isEmpty() || destination.isEmpty()) {
                        throw new IllegalArgumentException("Please fill in all fields");
                    }

                    if (departureTime.isAfter(arrivalTime)) {
                        throw new IllegalArgumentException("Departure time must be before arrival time");
                    }

                    Plane selectedPlane = planes.stream()
                            .filter(p -> p.getPlaneName().equals(selectedPlaneName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Plane not found"));

                    flightService.createFlight(flightNumber, departureTime, arrivalTime,
                            departure, destination, selectedPlane.getId(),
                            economyPrice, businessPrice, economyCapacity, businessCapacity);

                    refreshFlights();
                    dialog.dispose();

                    JOptionPane.showMessageDialog(this,
                            "Flight added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error adding flight: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading planes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to edit",
                    "Edit Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
            List<Flight> flights = flightService.getAllFlights();
            Flight selectedFlight = flights.stream()
                    .filter(f -> f.getFlightNumber().equals(flightNumber))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Flight not found"));

            showEditFlightDialog(selectedFlight);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error editing flight: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showEditFlightDialog(Flight flight) {
        try {
            List<Plane> planes = planeService.getAllPlanes();
            Plane currentPlane = planeService.getPlaneById(flight.getPlaneId());

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Flight", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Create form fields
            JTextField flightNumberField = new JTextField(flight.getFlightNumber());
            JTextField departureField = new JTextField(flight.getDeparture());
            JTextField destinationField = new JTextField(flight.getDestination());
            JTextField departureDateField = new JTextField(flight.getDepartureTime().format(formatter));
            JTextField arrivalDateField = new JTextField(flight.getArrivalTime().format(formatter));
            JComboBox<String> planeComboBox = new JComboBox<>(
                    planes.stream()
                            .map(Plane::getPlaneName)
                            .toArray(String[]::new)
            );
            planeComboBox.setSelectedItem(currentPlane.getPlaneName());
            JSpinner economyPriceSpinner = new JSpinner(new SpinnerNumberModel(
                    flight.getEconomyPrice(), 0.0, 10000.0, 10.0));
            JSpinner businessPriceSpinner = new JSpinner(new SpinnerNumberModel(
                    flight.getBusinessPrice(), 0.0, 20000.0, 10.0));

            // Add form fields to panel
            formPanel.add(new JLabel("Flight Number:"));
            formPanel.add(flightNumberField);
            formPanel.add(new JLabel("Departure:"));
            formPanel.add(departureField);
            formPanel.add(new JLabel("Destination:"));
            formPanel.add(destinationField);
            formPanel.add(new JLabel("Departure Time:"));
            formPanel.add(departureDateField);
            formPanel.add(new JLabel("Arrival Time:"));
            formPanel.add(arrivalDateField);
            formPanel.add(new JLabel("Plane:"));
            formPanel.add(planeComboBox);
            formPanel.add(new JLabel("Economy Price:"));
            formPanel.add(economyPriceSpinner);
            formPanel.add(new JLabel("Business Price:"));
            formPanel.add(businessPriceSpinner);

            // Create buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            saveButton.addActionListener(e -> {
                try {
                    String newFlightNumber = flightNumberField.getText().trim();
                    String newDeparture = departureField.getText().trim();
                    String newDestination = destinationField.getText().trim();
                    String departureText = departureDateField.getText();
                    String arrivalText = arrivalDateField.getText();
                    LocalDateTime newDepartureTime = LocalDateTime.parse(departureDateField.getText(), formatter);
                    LocalDateTime newArrivalTime = LocalDateTime.parse(arrivalDateField.getText(), formatter);
                    String selectedPlaneName = (String) planeComboBox.getSelectedItem();
                    double newEconomyPrice = (double) economyPriceSpinner.getValue();
                    double newBusinessPrice = (double) businessPriceSpinner.getValue();

                    // Validate input
                    if (newFlightNumber.isEmpty() || newDeparture.isEmpty() || newDestination.isEmpty()) {
                        throw new IllegalArgumentException("Please fill in all fields");
                    }

                    if (newDepartureTime.isAfter(newArrivalTime)) {
                        throw new IllegalArgumentException("Departure time must be before arrival time");
                    }

                    Plane selectedPlane = planes.stream()
                            .filter(p -> p.getPlaneName().equals(selectedPlaneName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Plane not found"));

                    flight.setFlightNumber(newFlightNumber);
                    flight.setDeparture(newDeparture);
                    flight.setDestination(newDestination);
                    flight.setDepartureTime(newDepartureTime);
                    flight.setArrivalTime(newArrivalTime);
                    flight.setPlaneId(selectedPlane.getId());
                    flight.setEconomyPrice(newEconomyPrice);
                    flight.setBusinessPrice(newBusinessPrice);

                    flightService.updateFlight(flight);
                    refreshFlights();
                    dialog.dispose();

                    JOptionPane.showMessageDialog(this,
                            "Flight updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error updating flight: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading planes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to delete",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete flight " + flightNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                List<Flight> flights = flightService.getAllFlights();
                Flight flight = flights.stream()
                        .filter(f -> f.getFlightNumber().equals(flightNumber))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Flight not found"));

                flightService.deleteFlight(flight.getId());
                refreshFlights();

                JOptionPane.showMessageDialog(this,
                        "Flight deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting flight: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}