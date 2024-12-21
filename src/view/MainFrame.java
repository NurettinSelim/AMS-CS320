package view;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final ProfilePanel profilePanel;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("AMS - Airport Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        profilePanel = new ProfilePanel(currentUser);

        contentPanel.add(new FlightListPanel(currentUser, this), "flights");
        if (currentUser.getRole().equals("manager")) {
            contentPanel.add(new ManagePlanesPanel(), "planes");
            contentPanel.add(new ManageFlightsPanel(), "manage_flights");
            contentPanel.add(new PassengerListPanel(), "passengers");
        }
        contentPanel.add(profilePanel, "profile");

        cardLayout.show(contentPanel, "flights");
    }

    public void refreshTickets() {
        if (profilePanel != null) {
            profilePanel.refreshTickets();
        }
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBackground(new Color(240, 240, 240));

        sidebarPanel.add(Box.createVerticalStrut(20));

        addNavigationButton(sidebarPanel, "View Flights", "flights");

        if (currentUser.getRole().equals("manager")) {
            addNavigationButton(sidebarPanel, "Manage Planes", "planes");
            addNavigationButton(sidebarPanel, "Manage Flights", "manage_flights");
            addNavigationButton(sidebarPanel, "View Passengers", "passengers");
        }

        addNavigationButton(sidebarPanel, "My Profile", "profile");

        sidebarPanel.add(Box.createVerticalGlue());
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.addActionListener(e -> handleLogout());
        sidebarPanel.add(logoutButton);

        return sidebarPanel;
    }

    private void addNavigationButton(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        panel.add(button);
        panel.add(Box.createVerticalStrut(5));
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}