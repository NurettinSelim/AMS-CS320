package repository;

import model.Ticket;

import java.sql.SQLException;
import java.util.List;

public interface ITicketRepository {
    Ticket create(Ticket ticket) throws SQLException;

    List<Ticket> findByUserId(int userId) throws SQLException;

    List<Ticket> findAll() throws SQLException;

    List<Ticket> findByFlightId(int flightId) throws SQLException;

    Ticket findById(int id) throws SQLException;

    void delete(int id) throws SQLException;
}