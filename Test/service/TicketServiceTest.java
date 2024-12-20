package service;

import model.Flight;
import model.Plane;
import model.Ticket;
import repository.ITicketRepository;
import repository.IFlightRepository;
import repository.TicketRepository;
import repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    private TicketService ticketService;
    private IFlightRepository flightRepository;
    private ITicketRepository ticketRepository;
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        // Initialize repositories and services
        ticketRepository = new TicketRepository();
        flightRepository = new FlightRepository();
        flightService = new FlightService();
        ticketService = new TicketService();
    }

    @Test
    void purchaseTicket_validDetails_createsTicket() throws SQLException {
        int planeId = 1;
        Plane plane = new Plane(planeId, "Boeing 747", 200); // Assuming the plane details
        flightService.createFlight("FL123", LocalDateTime.of(2024, 12, 20, 14, 0),
                LocalDateTime.of(2024, 12, 20, 16, 0), "New York", "Los Angeles", planeId,
                100.0, 200.0, 150, 50);

        Flight flight = flightRepository.findAll().get(0);

        int userId = 1;
        String seatType = "ECONOMY";

        Ticket ticket = ticketService.purchaseTicket(flight.getId(), userId, seatType);

        assertNotNull(ticket);
        assertEquals(flight.getId(), ticket.getFlightId());
        assertEquals(userId, ticket.getUserId());
        assertEquals(seatType, ticket.getSeatType());
        assertTrue(ticket.getPrice() > 0);
    }

    @Test
    void purchaseTicket_invalidFlightId() {
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTicket(999, 1, "ECONOMY");
        });
    }

    @Test
    void purchaseTicket_noAvailableSeats() throws SQLException {
        int planeId = 1;
        Plane plane = new Plane(planeId, "Boeing 747", 200);
        Flight flight = flightService.createFlight("FL123", LocalDateTime.of(2024, 12, 20, 14, 0),
                LocalDateTime.of(2024, 12, 20, 16, 0), "New York", "Los Angeles", planeId,
                100.0, 200.0, 0, 0);  // Setting zero capacity for both seat types

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTicket(flight.getId(), 1, "ECONOMY");
        });
    }

    @Test
    void getTicketsByUser() throws SQLException {
        int userId = 1;

        List<Ticket> tickets = ticketService.getTicketsByUser(userId);

        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
        assertEquals(userId, tickets.get(0).getUserId());
    }

    @Test
    void getTicketsByFlight() throws SQLException {
        int flightId = 101;

        List<Ticket> tickets = ticketService.getTicketsByFlight(flightId);

        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
        assertEquals(flightId, tickets.get(0).getFlightId());
    }

    @Test
    void getAllTickets() throws SQLException {
        List<Ticket> tickets = ticketService.getAllTickets();

        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
    }

    @Test
    void cancelTicket() throws SQLException {
        int ticketId = 1;
        Ticket ticket = ticketRepository.findById(ticketId);
        int originalSeatCount = (ticket.getSeatType().equals("ECONOMY"))
                ? flightService.getFlightById(ticket.getFlightId()).getEconomySeatsAvailable()
                : flightService.getFlightById(ticket.getFlightId()).getBusinessSeatsAvailable();

        ticketService.cancelTicket(ticketId);

        Ticket cancelledTicket = ticketRepository.findById(ticketId);
        assertNull(cancelledTicket);

        Flight flight = flightService.getFlightById(ticket.getFlightId());
        int updatedSeatCount = (ticket.getSeatType().equals("ECONOMY"))
                ? flight.getEconomySeatsAvailable()
                : flight.getBusinessSeatsAvailable();

        assertEquals(originalSeatCount + 1, updatedSeatCount);
    }

    @Test
    void cancelTicket_invalidTicketId() {
        int invalidTicketId = -1;
        assertThrows(IllegalArgumentException.class, () -> ticketService.cancelTicket(invalidTicketId));
    }

}
