package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:ams.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            // load sqlite driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            createTablesIfNotExist();
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found", e);
        }
        return connection;
    }

    private static void createTablesIfNotExist() throws SQLException {
        String[] createTableQueries = {
                // Users table
                """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            email TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            role TEXT NOT NULL
                        )
                        """,
                // Planes table
                """
                        CREATE TABLE IF NOT EXISTS planes (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            plane_name TEXT UNIQUE NOT NULL,
                            capacity INTEGER NOT NULL
                        )
                        """,
                // Flights table
                """
                        CREATE TABLE IF NOT EXISTS flights (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            flight_number TEXT UNIQUE NOT NULL,
                            departure_time DATETIME NOT NULL,
                            arrival_time DATETIME NOT NULL,
                            departure TEXT NOT NULL,
                            destination TEXT NOT NULL,
                            plane_id INTEGER NOT NULL,
                            economy_price REAL NOT NULL,
                            business_price REAL NOT NULL,
                            economy_seats_available INTEGER NOT NULL,
                            business_seats_available INTEGER NOT NULL,
                            FOREIGN KEY (plane_id) REFERENCES planes(id)
                        )
                        """,
                // Tickets table
                """
                        CREATE TABLE IF NOT EXISTS tickets (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            flight_id INTEGER NOT NULL,
                            user_id INTEGER NOT NULL,
                            seat_type TEXT NOT NULL,
                            seat_number TEXT NOT NULL,
                            price REAL NOT NULL,
                            FOREIGN KEY (flight_id) REFERENCES flights(id),
                            FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                        """};

        try (var statement = connection.createStatement()) {
            for (String query : createTableQueries) {
                statement.execute(query);
            }

            // Insert admin user if not exists
            statement.execute("""
                        INSERT OR IGNORE INTO users (email, password, role)
                        VALUES ('admin@ams.com', 'admin', 'manager')
                    """);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}