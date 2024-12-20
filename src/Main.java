import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // connection
        try {
            Connection connection= DatabaseConnection.getConnection();
            System.out.println("Connected to the database");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while(resultSet.next()){
                System.out.println(resultSet.getString("id"));
                System.out.println(resultSet.getString("email"));

            }
        } catch (Exception e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }

    }
}