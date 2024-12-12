package view;

import javax.swing.*;

public class LoginFrame extends JFrame {
    public final JTextField emailField;
    public final JPasswordField passwordField;
    public final JButton loginButton;
    public final JButton registerButton;

    public LoginFrame() {
        this.setTitle("Airport Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
    }
}
