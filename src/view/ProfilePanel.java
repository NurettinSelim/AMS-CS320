package view;

import model.Ticket;
import model.User;
import service.TicketService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProfilePanel extends JPanel {
    private final User currentUser;
    private final UserService userService;
    private final TicketService ticketService;
    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField emailField;
    private final JTable ticketTable;
    private final DefaultTableModel tableModel;

    public ProfilePanel(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.ticketService = new TicketService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create profile panel
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add profile fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(user.getName(), 20);
        profilePanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        profilePanel.add(new JLabel("Surname:"), gbc);

        gbc.gridx = 1;
        surnameField = new JTextField(user.getSurname(), 20);
        profilePanel.add(surnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        profilePanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail(), 20);
        profilePanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        profilePanel.add(changePasswordButton, gbc);

        gbc.gridy = 4;
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        profilePanel.add(saveButton, gbc);

        // Create tickets panel
        JPanel ticketsPanel = new JPanel(new BorderLayout());
        ticketsPanel.setBorder(BorderFactory.createTitledBorder("My Tickets"));

        String[] columns = {"Flight Number", "Departure", "Destination", 
                          "Departure Time", "Seat Type", "Seat Number", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);

        JButton cancelTicketButton = new JButton("Cancel Selected Ticket");
        cancelTicketButton.addActionListener(e -> cancelSelectedTicket());
        
        ticketsPanel.add(scrollPane, BorderLayout.CENTER);
        ticketsPanel.add(cancelTicketButton, BorderLayout.SOUTH);

        // Add panels to main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(profilePanel, BorderLayout.NORTH);
        mainPanel.add(ticketsPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Load initial ticket data
        refreshTickets();
    }

    public void refreshTickets() {
        try {
            List<Ticket> tickets = ticketService.getTicketsByUser(currentUser.getId());
            updateTableModel(tickets);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTableModel(List<Ticket> tickets) {
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                ticket.getId(),
                ticket.getFlightId(),
                ticket.getSeatType(),
                ticket.getSeatNumber(),
                String.format("$%.2f", ticket.getPrice())
            });
        }
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Change Password", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        formPanel.add(new JLabel("Current Password:"));
        formPanel.add(currentPasswordField);
        formPanel.add(new JLabel("New Password:"));
        formPanel.add(newPasswordField);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!currentPassword.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(dialog,
                    "Current password is incorrect",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                    "New passwords do not match",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                currentUser.setPassword(newPassword);
                userService.updateUser(currentUser);
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "Password changed successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error changing password: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void saveChanges() {
        String newName = nameField.getText().trim();
        String newSurname = surnameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty() || newSurname.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            currentUser.setName(newName);
            currentUser.setSurname(newSurname);
            currentUser.setEmail(newEmail);
            userService.updateUser(currentUser);
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating profile: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this ticket?",
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