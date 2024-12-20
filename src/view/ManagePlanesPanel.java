package view;

import model.Plane;
import service.PlaneService;
import util.UIUpdateManager;
import util.UIUpdateObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManagePlanesPanel extends JPanel implements UIUpdateObserver {
    private final PlaneService planeService;
    private final JTable planeTable;
    private final DefaultTableModel tableModel;

    public ManagePlanesPanel() {
        this.planeService = new PlaneService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"Plane Number", "Economy Capacity", "Business Capacity", "Total Capacity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        planeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(planeTable);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Plane");
        JButton editButton = new JButton("Edit Plane");
        JButton deleteButton = new JButton("Delete Plane");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Add components to panel
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> showAddPlaneDialog());
        editButton.addActionListener(e -> editSelectedPlane());
        deleteButton.addActionListener(e -> deleteSelectedPlane());

        // Load initial data
        refreshPlanes();

        UIUpdateManager.getInstance().addObserver(this);
    }

    @Override
    public void onUIUpdate(String updateType) {
        if (updateType.equals(UIUpdateManager.PLANE_UPDATE)) {
            loadPlanes();
        }
    }

    // Make sure to remove the observer when the panel is no longer needed
    public void cleanup() {
        UIUpdateManager.getInstance().removeObserver(this);
    }

    private void refreshPlanes() {
        try {
            List<Plane> planes = planeService.getAllPlanes();
            updateTableModel(planes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading planes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTableModel(List<Plane> planes) {
        tableModel.setRowCount(0);
        for (Plane plane : planes) {
            tableModel.addRow(new Object[]{
                    plane.getPlaneNumber(),
                    plane.getEconomyCapacity(),
                    plane.getBusinessCapacity(),
                    plane.getTotalCapacity()
            });
        }
    }

    private void showAddPlaneDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Plane", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField planeNumberField = new JTextField();
        JSpinner economyCapacitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        JSpinner businessCapacitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));

        formPanel.add(new JLabel("Plane Number:"));
        formPanel.add(planeNumberField);
        formPanel.add(new JLabel("Economy Capacity:"));
        formPanel.add(economyCapacitySpinner);
        formPanel.add(new JLabel("Business Capacity:"));
        formPanel.add(businessCapacitySpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String planeNumber = planeNumberField.getText().trim();
                int economyCapacity = (int) economyCapacitySpinner.getValue();
                int businessCapacity = (int) businessCapacitySpinner.getValue();

                if (planeNumber.isEmpty()) {
                    throw new IllegalArgumentException("Please enter a plane number");
                }

                planeService.createPlane(planeNumber, economyCapacity, businessCapacity);
                refreshPlanes();
                dialog.dispose();

                JOptionPane.showMessageDialog(this,
                        "Plane added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error adding plane: " + ex.getMessage(),
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
    }

    private void editSelectedPlane() {
        int selectedRow = planeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a plane to edit",
                    "Edit Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String planeNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int economyCapacity = (int) tableModel.getValueAt(selectedRow, 1);
        int businessCapacity = (int) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Plane", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField planeNumberField = new JTextField(planeNumber);
        JSpinner economyCapacitySpinner = new JSpinner(new SpinnerNumberModel(economyCapacity, 0, 1000, 1));
        JSpinner businessCapacitySpinner = new JSpinner(new SpinnerNumberModel(businessCapacity, 0, 1000, 1));

        formPanel.add(new JLabel("Plane Number:"));
        formPanel.add(planeNumberField);
        formPanel.add(new JLabel("Economy Capacity:"));
        formPanel.add(economyCapacitySpinner);
        formPanel.add(new JLabel("Business Capacity:"));
        formPanel.add(businessCapacitySpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String newPlaneNumber = planeNumberField.getText().trim();
                int newEconomyCapacity = (int) economyCapacitySpinner.getValue();
                int newBusinessCapacity = (int) businessCapacitySpinner.getValue();

                if (newPlaneNumber.isEmpty()) {
                    throw new IllegalArgumentException("Please enter a plane number");
                }

                // Get the plane ID (you'll need to modify the table model to store this)
                List<Plane> planes = planeService.getAllPlanes();
                Plane plane = planes.stream()
                        .filter(p -> p.getPlaneNumber().equals(planeNumber))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Plane not found"));

                plane.setPlaneNumber(newPlaneNumber);
                plane.setEconomyCapacity(newEconomyCapacity);
                plane.setBusinessCapacity(newBusinessCapacity);

                planeService.updatePlane(plane);
                refreshPlanes();
                dialog.dispose();

                JOptionPane.showMessageDialog(this,
                        "Plane updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating plane: " + ex.getMessage(),
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
    }

    private void deleteSelectedPlane() {
        int selectedRow = planeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a plane to delete",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String planeNumber = (String) tableModel.getValueAt(selectedRow, 0);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete plane " + planeNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Get the plane ID
                List<Plane> planes = planeService.getAllPlanes();
                Plane plane = planes.stream()
                        .filter(p -> p.getPlaneNumber().equals(planeNumber))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Plane not found"));

                planeService.deletePlane(plane.getId());
                refreshPlanes();

                JOptionPane.showMessageDialog(this,
                        "Plane deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting plane: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void loadPlanes() {
        try {
            List<Plane> planes = planeService.getAllPlanes();
            updateTableModel(planes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading planes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
