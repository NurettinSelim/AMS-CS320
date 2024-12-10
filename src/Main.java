import util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // conenction
        try {
            DatabaseConnection.getConnection();
            System.out.println("Connected to the database");
        } catch (Exception e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }

    }
}