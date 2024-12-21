package repository;

import model.Ticket;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository implements ITicketRepository {
    @Override
    public Ticket create(Ticket ticket) throws SQLException {
        String query = """
                    INSERT INTO tickets (flight_id, user_id, departure, destination, departure_time, seat_type, seat_number, price)
                    VALUES (?,?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ticket.getFlightId());
            stmt.setInt(2, ticket.getUserId());
            stmt.setString(3, ticket.getDeparture());
            stmt.setString(4, ticket.getDestination());
            stmt.setTimestamp(5, Timestamp.valueOf(ticket.getDepartureTime()));
            stmt.setString(6, ticket.getSeatType());
            stmt.setString(7, ticket.getSeatNumber());
            stmt.setDouble(8, ticket.getPrice());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }
        }
        return ticket;
    }

    @Override
    public List<Ticket> findByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM tickets WHERE user_id = ?";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findAll() throws SQLException {
        String query = "SELECT * FROM tickets";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findByFlightId(int flightId) throws SQLException {
        String query = "SELECT * FROM tickets WHERE flight_id = ?";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
        }
        return tickets;
    }

    @Override
    public Ticket findById(int id) throws SQLException {
        String query = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createTicketFromResultSet(rs);
            }
        }
        return null; // Return null if not found
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Ticket createTicketFromResultSet(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("id"),
                rs.getInt("flight_id"),
                rs.getInt("user_id"),
                rs.getString("seat_type"),
                rs.getString("seat_number"),
                rs.getDouble("price"),
                rs.getString("departure"),
                rs.getString("destination"),
                rs.getTimestamp("departure_time").toLocalDateTime()
        );
    }
}
