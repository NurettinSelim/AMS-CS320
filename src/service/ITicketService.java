package service;

import model.Ticket;

import java.sql.SQLException;
import java.util.List;

public interface ITicketService {
    Ticket purchaseTicket(int flightId, int userId, String seatType) throws SQLException;

    List<Ticket> getTicketsByUser(int userId) throws SQLException;

    List<Ticket> getTicketsByFlight(int flightId) throws SQLException;

    List<Ticket> getAllTickets() throws SQLException;

    void cancelTicket(int ticketId) throws SQLException;
} 