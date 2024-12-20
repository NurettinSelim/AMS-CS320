package service;

import model.Flight;
import model.Plane;
import repository.FlightRepository;
import repository.PlaneRepository;
import util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

class FlightServiceTest {

    private FlightService flightService;
    private FlightRepository flightRepository;
    private PlaneRepository planeRepository;

    @BeforeEach
    void setUp() throws SQLException {
        flightRepository = new FlightRepository();
        planeRepository = new PlaneRepository();
        flightService = new FlightService();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM flights");
            stmt.executeUpdate("DELETE FROM planes");
        }
    }

    @Test
    void createFlight() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeRepository.create(plane); // Creating a plane for the test

        String flightNumber = "FL123";
        LocalDateTime departureTime = LocalDateTime.now().plusDays(1);
        LocalDateTime arrivalTime = departureTime.plusHours(2);
        String departure = "New York";
        String destination = "Los Angeles";
        double economyPrice = 500;
        double businessPrice = 1200;
        int economyCapacity = 150;
        int businessCapacity = 50;

        Flight createdFlight = flightService.createFlight(flightNumber, departureTime, arrivalTime, departure,
                destination, plane.getId(), economyPrice, businessPrice, economyCapacity, businessCapacity);

        assertNotNull(createdFlight);
        assertTrue(createdFlight.getId() > 0);
        assertEquals(flightNumber, createdFlight.getFlightNumber());
        assertEquals(departureTime, createdFlight.getDepartureTime());
        assertEquals(arrivalTime, createdFlight.getArrivalTime());
        assertEquals(departure, createdFlight.getDeparture());
        assertEquals(destination, createdFlight.getDestination());
        assertEquals(plane.getId(), createdFlight.getPlaneId());
        assertEquals(economyPrice, createdFlight.getEconomyPrice());
        assertEquals(businessPrice, createdFlight.getBusinessPrice());
        assertEquals(economyCapacity, createdFlight.getEconomySeatsAvailable());
        assertEquals(businessCapacity, createdFlight.getBusinessSeatsAvailable());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM flights WHERE id = ?")) {
            stmt.setInt(1, createdFlight.getId());
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(flightNumber, rs.getString("flight_number"));
            assertEquals(departureTime, rs.getTimestamp("departure_time").toLocalDateTime());
            assertEquals(arrivalTime, rs.getTimestamp("arrival_time").toLocalDateTime());
            assertEquals(departure, rs.getString("departure"));
            assertEquals(destination, rs.getString("destination"));
            assertEquals(plane.getId(), rs.getInt("plane_id"));
            assertEquals(economyPrice, rs.getDouble("economy_price"));
            assertEquals(businessPrice, rs.getDouble("business_price"));
            assertEquals(economyCapacity, rs.getInt("economy_seats_available"));
            assertEquals(businessCapacity, rs.getInt("business_seats_available"));
        }
    }

    @Test
    void createFlightWithInvalidPlane() {
        assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight("FL123", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                    "New York", "Los Angeles", 999, 500, 1200, 150, 50);
        });
    }

    @Test
    void createFlightWhenPlaneIsNotAvailable() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeRepository.create(plane); // Create the plane

        flightService.createFlight("FL123", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                "New York", "Los Angeles", plane.getId(), 500, 1200, 150, 50);

        Plane finalPlane = plane;
        assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight("FL124", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                    "New York", "San Francisco", finalPlane.getId(), 500, 1200, 150, 50);
        });
    }

    @Test
    void searchFlights() throws SQLException {
        String departure = "New York";
        String destination = "London";
        LocalDateTime departureTime = LocalDateTime.of(2024, 12, 20, 15, 0);

        Flight flight1 = new Flight(1, "AA100", departureTime, departureTime.plusHours(7), departure, destination, 101, 500.0, 1000.0, 50, 20);
        Flight flight2 = new Flight(2, "BB200", departureTime.plusDays(1), departureTime.plusHours(8), departure, destination, 102, 600.0, 1100.0, 60, 25);

        flightRepository.create(flight1);
        flightRepository.create(flight2);

        List<Flight> flights = flightService.searchFlights(departure, destination, departureTime);

        assertEquals(1, flights.size());
        assertEquals("AA100", flights.get(0).getFlightNumber());
    }

    @Test
    void getAllFlights() throws SQLException {
        Flight flight1 = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        Flight flight2 = new Flight(2, "BB200", LocalDateTime.of(2024, 12, 21, 10, 0), LocalDateTime.of(2024, 12, 21, 17, 0), "New York", "Paris", 102, 600.0, 1100.0, 60, 25);

        flightRepository.create(flight1);
        flightRepository.create(flight2);

        List<Flight> flights = flightService.getAllFlights();

        assertEquals(2, flights.size());
        assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("AA100")));
        assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("BB200")));
    }

    @Test
    void getFlightById() throws SQLException {
        Flight flight = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        Flight retrievedFlight = flightService.getFlightById(flight.getId());

        assertNotNull(retrievedFlight);
        assertEquals(flight.getId(), retrievedFlight.getId());
        assertEquals(flight.getFlightNumber(), retrievedFlight.getFlightNumber());
    }

    @Test
    void updateFlight() throws SQLException {
        Flight flight = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        flight.setFlightNumber("AA101");
        flight.setEconomyPrice(550.0);
        flight.setBusinessPrice(1050.0);
        flight.setEconomySeatsAvailable(55);
        flight.setBusinessSeatsAvailable(25);

        flightService.updateFlight(flight);

        Flight updatedFlight = flightService.getFlightById(flight.getId());

        assertNotNull(updatedFlight);
        assertEquals("AA101", updatedFlight.getFlightNumber());
        assertEquals(550.0, updatedFlight.getEconomyPrice());
        assertEquals(1050.0, updatedFlight.getBusinessPrice());
        assertEquals(55, updatedFlight.getEconomySeatsAvailable());
        assertEquals(25, updatedFlight.getBusinessSeatsAvailable());
    }

    @Test
    void deleteFlight() throws SQLException {
        Flight flight = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        flightService.deleteFlight(flight.getId());

        Flight deletedFlight = flightService.getFlightById(flight.getId());

        assertNull(deletedFlight);
    }

    @Test
    void updateFlightSeats() throws SQLException {
        Flight flight = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        flightService.updateFlightSeats(flight.getId(), "ECONOMY", 10);
        Flight updatedFlight = flightService.getFlightById(flight.getId());

        assertEquals(60, updatedFlight.getEconomySeatsAvailable());

        flightService.updateFlightSeats(flight.getId(), "BUSINESS", -5);
        updatedFlight = flightService.getFlightById(flight.getId());

        assertEquals(15, updatedFlight.getBusinessSeatsAvailable());
    }

    @Test
    void updateFlightSeats_invalidFlightId() {
        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlightSeats(999, "ECONOMY", 10));
    }

    @Test
    void updateFlightSeats_notEnoughSeats() throws SQLException {
        Flight flight = new Flight(1, "AA100", LocalDateTime.of(2024, 12, 20, 15, 0), LocalDateTime.of(2024, 12, 20, 22, 0), "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlightSeats(flight.getId(), "ECONOMY", -60));
        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlightSeats(flight.getId(), "BUSINESS", -25));
    }

    @Test
    void updateFlightSeats_invalidSeatType() {
        assertThrows(IllegalArgumentException.class, () -> flightService.updateFlightSeats(1, "FIRST_CLASS", 10));
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM flights");
            stmt.executeUpdate("DELETE FROM planes");
        }
    }
}