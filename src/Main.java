//import util.DatabaseConnection;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//public class Main {
//    public static void main(String[] args) {
//        // connection
//        try {
//            Connection connection= DatabaseConnection.getConnection();
//            System.out.println("Connected to the database");
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
//
//            while(resultSet.next()){
//                System.out.println(resultSet.getString("id"));
//                System.out.println(resultSet.getString("email"));
//
//            }
//        } catch (Exception e) {
//            System.err.println("Error connecting to the database: " + e.getMessage());
//        }
//
//    }
//}

import javax.swing.*;

import model.Manager;
import model.Passenger;
import view.LoginFrame;
import view.MainFrame;
import view.PassengerListPanel;

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
//                Passenger ipek = new Passenger(1,"ipek", "ipek123","ipek","debreli");
//                Manager nehir = new Manager(2,"nehir.kirmizisakal@ams.com", "nehir123");
//                LoginFrame loginFrame = new LoginFrame();
//                loginFrame.setVisible(true);
                Passenger ipek = new Passenger(1,"ipek", "ipek123","ipek","debreli");
                Manager nehir = new Manager(2,"nehir.kirmizisakal@ams.com", "nehir123");
                MainFrame mainFrame = new MainFrame(nehir);
                mainFrame.setVisible(true);
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