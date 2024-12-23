import view.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run GUI in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
