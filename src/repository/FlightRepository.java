package repository;

import model.Flight;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightRepository implements IFlightRepository{
    @Override
    public Flight create(Flight flight) throws SQLException {
        String query = """
            INSERT INTO flights (flight_number, departure_time, arrival_time, departure, 
                               destination, plane_id, economy_price, business_price, 
                               economy_seats_available, business_seats_available)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, flight.getFlightNumber());
            stmt.setTimestamp(2, new Timestamp(flight.getDepartureTime().getTime()));
            stmt.setTimestamp(3, new Timestamp(flight.getArrivalTime().getTime()));
            stmt.setString(4, flight.getDeparture());
            stmt.setString(5, flight.getDestination());
            stmt.setInt(6, flight.getPlaneId());
            stmt.setDouble(7, flight.getEconomyPrice());
            stmt.setDouble(8, flight.getBusinessPrice());
            stmt.setInt(9, flight.getEconomySeatsAvailable());
            stmt.setInt(10, flight.getBusinessSeatsAvailable());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating flight failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    flight.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating flight failed, no ID obtained.");
                }
            }
        }
        return flight;
    }


    @Override
    public List<Flight> findAll() throws SQLException {
        String query = "SELECT * FROM flights";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                flights.add(createFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    @Override
    public Flight findById(int id) throws SQLException {
        String query = "SELECT * FROM flights WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createFlightFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public void update(Flight flight) throws SQLException {
        String query = """
            UPDATE flights 
            SET flight_number = ?, departure_time = ?, arrival_time = ?, 
                departure = ?, destination = ?, plane_id = ?, 
                economy_price = ?, business_price = ?,
                economy_seats_available = ?, business_seats_available = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, flight.getFlightNumber());
            stmt.setTimestamp(2, new Timestamp(flight.getDepartureTime().getTime()));
            stmt.setTimestamp(3, new Timestamp(flight.getArrivalTime().getTime()));
            stmt.setString(4, flight.getDeparture());
            stmt.setString(5, flight.getDestination());
            stmt.setInt(6, flight.getPlaneId());
            stmt.setDouble(7, flight.getEconomyPrice());
            stmt.setDouble(8, flight.getBusinessPrice());
            stmt.setInt(9, flight.getEconomySeatsAvailable());
            stmt.setInt(10, flight.getBusinessSeatsAvailable());
            stmt.setInt(11, flight.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM flights WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Flight> searchFlights(String departure, String destination, Time departureTime) throws SQLException {
        String query = """
            SELECT * FROM flights 
            WHERE departure = ? 
            AND destination = ? 
            AND time(departure_time) = ?
        """;

        List<Flight> flights = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, departure);
            stmt.setString(2, destination);
            stmt.setTime(3, departureTime);


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                flights.add(createFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    private Flight createFlightFromResultSet(ResultSet rs) throws SQLException {
        return new Flight(
                rs.getInt("id"),
                rs.getString("flight_number"),
                rs.getTime("departure_time"),
                rs.getTime("arrival_time"),
                rs.getString("departure"),
                rs.getString("destination"),
                rs.getInt("plane_id"),
                rs.getDouble("economy_price"),
                rs.getDouble("business_price"),
                rs.getInt("economy_seats_available"),
                rs.getInt("business_seats_available")
        );
    }
}
