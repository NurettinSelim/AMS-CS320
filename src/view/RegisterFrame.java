package view;

import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Objects;

public class RegisterFrame extends JFrame implements IRegisterFrame {
    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final UserService userService;
    private final LoginFrame loginFrame;
    //private boolean adminMode = false;

    private String role = "passenger";
    private final JLabel secretLabel;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userService = new UserService();

        // Add key listener for secret admin mode (Ctrl + Alt + A)
        KeyStroke adminKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);

        getRootPane().registerKeyboardAction(e -> toggleAdminMode(),
                adminKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        setTitle("AMS - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.3; // Label width
        gbc.gridwidth = 1;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7; // Text field width
        nameField = new JTextField(30);
        formPanel.add(nameField, gbc);

        // Surname field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Surname:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        surnameField = new JTextField(30);
        formPanel.add(surnameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = new JTextField(30);
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(30);
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        confirmPasswordField = new JPasswordField(30);
        formPanel.add(confirmPasswordField, gbc);

        // Create buttons with specific sizes
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(100, 30);
        registerButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);

        // Create button panel with vertical spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add action listeners
        registerButton.addActionListener(this::handleRegister);
        cancelButton.addActionListener(e -> handleCancel());

        // Create a wrapper panel for form and buttons
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to main panel
        mainPanel.add(new JLabel("Register New Account", SwingConstants.CENTER), BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add secret label (initially invisible)
        secretLabel = new JLabel("Admin Mode", SwingConstants.CENTER);
        secretLabel.setForeground(Color.RED);
        secretLabel.setVisible(false);
        mainPanel.add(secretLabel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    @Override
    public void toggleAdminMode() {
        boolean isAdmin = !(Objects.equals(role, "manager"));
        role = "manager";
        secretLabel.setVisible(isAdmin);
        if (isAdmin) {
            JOptionPane.showMessageDialog(this,
                    "Admin mode activated!",
                    "Secret",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void handleRegister(ActionEvent e) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate input
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create user with admin status based on secret mode
            User user = new User(0, email, password, role, name, surname);
            userService.register(user);
            boolean isAdmin = Objects.equals(role, "manager");
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login." +
                            (isAdmin ? " (Admin privileges granted)" : ""),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            handleCancel();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    @Override
    public void handleCancel() {
        loginFrame.setVisible(true);
        dispose();
    }
} 
