package service;

import model.Flight;
import model.Plane;
import model.Ticket;
import model.User;
import org.junit.jupiter.api.AfterEach;
import repository.ITicketRepository;
import repository.IFlightRepository;
import repository.TicketRepository;
import repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    private TicketService ticketService;
    private IFlightRepository flightRepository;
    private ITicketRepository ticketRepository;
    private FlightService flightService;
    private PlaneService planeService;
    private UserService userService;




    @BeforeEach
    void setUp() {
        ticketRepository = new TicketRepository();
        flightRepository = new FlightRepository();
        flightService = new FlightService();
        ticketService = new TicketService();
        planeService = new PlaneService();
        userService = new UserService();
    }

    @Test
    void purchaseTicket_validDetails_createsTicket() throws SQLException {

        Plane plane = planeService.createPlane( "Boeing 747", 200);
        Time departureTime = Time.valueOf("14:00:00");
        Time arrivalTime = Time.valueOf("16:00:00");

        flightService.createFlight("FL123", departureTime, arrivalTime, "New York", "Los Angeles", plane.getId(),
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
        Plane plane = planeService.createPlane("Boeing 747", 200);
        Time departureTime = Time.valueOf("14:00:00");
        Time arrivalTime = Time.valueOf("16:00:00");

        Flight flight = flightService.createFlight("FL123", departureTime, arrivalTime, "New York", "Los Angeles", plane.getId(),
                100.0, 200.0, 0, 0);


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
        // Create a plane for the flight
        Plane plane = new Plane(0, "Boeing 747", 200); // Plane ID will be auto-generated
        Plane createdPlane = planeService.createPlane(plane.getPlaneName(), plane.getCapacity());

        // Create a flight with the created plane
        String flightNumber = "FL123";
        Time departureTime = Time.valueOf("14:00:00");
        Time arrivalTime = Time.valueOf("16:00:00");
        String departure = "New York";
        String destination = "Los Angeles";
        double economyPrice = 100.0;
        double businessPrice = 200.0;
        int economySeatsAvailable = 150;
        int businessSeatsAvailable = 50;

        Flight createdFlight = flightService.createFlight(
                flightNumber, departureTime, arrivalTime, departure, destination, createdPlane.getId(),
                economyPrice, businessPrice, economySeatsAvailable, businessSeatsAvailable
        );

        // Create a user
        User createdUser = userService.register(0, "email3@example.com", "password123", "passenger", "John", "Doe");

        // Purchase a ticket for the created flight and user
        Ticket ticket = ticketService.purchaseTicket(createdFlight.getId(), createdUser.getId(), "ECONOMY");

        // Get the ticket ID for cancellation
        int ticketId = ticket.getId();

        // Get the original seat count for the flight
        Flight flight = flightService.getFlightById(createdFlight.getId());
        int originalSeatCount = flight.getEconomySeatsAvailable();

        System.out.println("Ticket count before cancellation: " + ticketService.getAllTickets());
        ticketService.cancelTicket(ticketId);
        System.out.println("Ticket count after cancellation: " + ticketService.getAllTickets());



        // Verify the ticket is cancelled
        Ticket cancelledTicket = ticketRepository.findById(ticketId);
        assertNull(cancelledTicket); // Ticket should be removed

        // Verify the seat count has increased by 1
        int updatedSeatCount = flight.getEconomySeatsAvailable();
        assertEquals(originalSeatCount , updatedSeatCount+1);
    }

    @Test
    void cancelTicket_invalidTicketId() {
        int invalidTicketId = -1;
        assertThrows(IllegalArgumentException.class, () -> ticketService.cancelTicket(invalidTicketId));
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM flights");
            stmt.executeUpdate("DELETE FROM planes");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM tickets");

        }
    }

}
