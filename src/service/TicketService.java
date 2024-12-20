package service;

import model.Flight;
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
        Flight flight = flightService.getFlightById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Invalid flight ID");
        }

        int availableSeats;
        double price;

        if ("ECONOMY".equals(seatType)) {
            availableSeats = flight.getEconomySeatsAvailable();
            price = flight.getEconomyPrice();
        } else if ("BUSINESS".equals(seatType)) {
            availableSeats = flight.getBusinessSeatsAvailable();
            price = flight.getBusinessPrice();
        } else {
            throw new IllegalArgumentException("Invalid seat type");
        }

        if (availableSeats <= 0) {
            throw new IllegalArgumentException("No seats available");
        }

        String seatNumber = generateSeatNumber(flightId, seatType);

        Ticket ticket = new Ticket(0, flightId, userId, seatType, seatNumber, price);
        ticket = ticketRepository.create(ticket);

        flightService.updateFlightSeats(flightId, seatType, -1);

        return ticket;
    }

    @Override
    public List<Ticket> getTicketsByUser(int userId) throws SQLException {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public List<Ticket> getTicketsByFlight(int flightId) throws SQLException {
        return ticketRepository.findByFlightId(flightId);
    }

    @Override
    public List<Ticket> getAllTickets() throws SQLException {
        return ticketRepository.findAll();
    }

    @Override
    public void cancelTicket(int ticketId) throws SQLException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
        flightService.updateFlightSeats(ticket.getFlightId(), ticket.getSeatType(), 1);

        ticketRepository.delete(ticketId);
    }

    private String generateSeatNumber(int flightId, String seatType) throws SQLException {
        List<Ticket> existingTickets = ticketRepository.findByFlightId(flightId);
        long typeCount = existingTickets.stream()
                .filter(t -> t.getSeatType().equals(seatType))
                .count();

        String prefix = seatType.equals("ECONOMY") ? "E" : "B";
        return prefix + (typeCount + 1);
    }
}
