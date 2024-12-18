package service;

import model.Ticket;
import repository.TicketRepository;

import java.sql.SQLException;
import java.util.List;

public class TicketService implements ITicketService{

    private final TicketRepository ticketRepository;
    private final FlightService flightService;

    public TicketService() {
        this.ticketRepository = new TicketRepository();
        this.flightService = new FlightService();
    }

    @Override
    public Ticket purchaseTicket(int flightId, int userId, String seatType) throws SQLException {
        return null;
    }

    @Override
    public List<Ticket> getTicketsByUser(int userId) throws SQLException {
        return null;
    }

    @Override
    public List<Ticket> getTicketsByFlight(int flightId) throws SQLException {
        return null;
    }

    @Override
    public List<Ticket> getAllTickets() throws SQLException {
        return null;
    }

    @Override
    public void cancelTicket(int ticketId) throws SQLException {

    }
}
