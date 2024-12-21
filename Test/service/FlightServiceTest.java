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
import java.time.LocalTime;
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
        plane = planeRepository.create(plane);

        String flightNumber = "FL123";
        Time departureTime = Time.valueOf(LocalTime.now().plusHours(1));
        Time arrivalTime = Time.valueOf(departureTime.toLocalTime().plusHours(2));
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
            assertEquals(departureTime, rs.getTime("departure_time"));
            assertEquals(arrivalTime, rs.getTime("arrival_time"));
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
            Time departureTime = Time.valueOf("10:00:00");
            Time arrivalTime = Time.valueOf("12:00:00");

            flightService.createFlight("FL123", departureTime, arrivalTime,
                    "New York", "Los Angeles", 999, 500, 1200, 150, 50);
        });
    }


    @Test
    void createFlightWhenPlaneIsNotAvailable() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeRepository.create(plane); // Create the plane

        Time departureTime = Time.valueOf("10:00:00");
        Time arrivalTime = Time.valueOf("12:00:00");

        flightService.createFlight("FL123", departureTime, arrivalTime,
                "New York", "Los Angeles", plane.getId(), 500, 1200, 150, 50);

        Plane finalPlane = plane;
        assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight("FL124", departureTime, arrivalTime,
                    "New York", "San Francisco", finalPlane.getId(), 500, 1200, 150, 50);
        });
    }

    @Test
    void searchFlights() throws SQLException {
        Plane plane1 = new Plane(101, "Boeing 777", 300);
        Plane plane2 = new Plane(102, "Airbus A380", 400);

        planeRepository.create(plane1);
        planeRepository.create(plane2);

        String departure = "New York";
        String destination = "London";
        Time departureTime = Time.valueOf("15:00:00");

        Flight flight1 = new Flight(1, "AA100", departureTime, Time.valueOf("22:00:00"), departure, destination, plane1.getId(), 500.0, 1000.0, 50, 20);

        Time flight2DepartureTime = new Time(departureTime.getTime() + 24 * 60 * 60 * 1000);
        Time flight2ArrivalTime = new Time(flight2DepartureTime.getTime() + 8 * 60 * 60 * 1000);

        Flight flight2 = new Flight(2, "BB200", flight2DepartureTime, flight2ArrivalTime, departure, destination, plane2.getId(), 600.0, 1100.0, 60, 25);

        flightRepository.create(flight1);
        flightRepository.create(flight2);

        System.out.println("Flight 1 created: " + flight1.getFlightNumber());
        System.out.println("Flight 2 created: " + flight2.getFlightNumber());

        List<Flight> flights = flightService.searchFlights(departure, destination, departureTime);

        assertEquals(1, flights.size());
        assertEquals("AA100", flights.get(0).getFlightNumber());
    }

    @Test
    void getAllFlights() throws SQLException {
        Time departureTime1 = Time.valueOf("15:00:00");
        Time arrivalTime1 = Time.valueOf("22:00:00");

        Time departureTime2 = Time.valueOf("10:00:00");
        Time arrivalTime2 = Time.valueOf("17:00:00");

        Flight flight1 = new Flight(1, "AA100", departureTime1, arrivalTime1, "New York", "London", 101, 500.0, 1000.0, 50, 20);
        Flight flight2 = new Flight(2, "BB200", departureTime2, arrivalTime2, "New York", "Paris", 102, 600.0, 1100.0, 60, 25);

        flightRepository.create(flight1);
        flightRepository.create(flight2);

        List<Flight> flights = flightService.getAllFlights();

        assertEquals(2, flights.size());
        assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("AA100")));
        assertTrue(flights.stream().anyMatch(f -> f.getFlightNumber().equals("BB200")));
    }


    @Test
    void getFlightById() throws SQLException {
        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = Time.valueOf("22:00:00");

        Flight flight = new Flight(1, "AA100", departureTime, arrivalTime, "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        Flight retrievedFlight = flightService.getFlightById(flight.getId());

        assertNotNull(retrievedFlight);
        assertEquals(flight.getId(), retrievedFlight.getId());
        assertEquals(flight.getFlightNumber(), retrievedFlight.getFlightNumber());
    }

    @Test
    void updateFlight() throws SQLException {
        Plane plane = new Plane(0, "Boeing 777", 300);
        planeRepository.create(plane);

        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = Time.valueOf("22:00:00");

        Flight flight = new Flight(0, "AA100", departureTime, arrivalTime, "New York", "London", plane.getId(), 500.0, 1000.0, 50, 20);
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
        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = Time.valueOf("22:00:00");

        Flight flight = new Flight(1, "AA100", departureTime, arrivalTime, "New York", "London", 101, 500.0, 1000.0, 50, 20);
        flightRepository.create(flight);

        flightService.deleteFlight(flight.getId());

        Flight deletedFlight = flightService.getFlightById(flight.getId());

        assertNull(deletedFlight);
    }

    @Test
    void updateFlightSeats() throws SQLException {
        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = Time.valueOf("22:00:00");

        Flight flight = new Flight(1, "AA100", departureTime, arrivalTime, "New York", "London", 101, 500.0, 1000.0, 50, 20);
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
        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = Time.valueOf("22:00:00");

        Flight flight = new Flight(1, "AA100", departureTime, arrivalTime, "New York", "London", 101, 500.0, 1000.0, 50, 20);
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
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM tickets");
        }
    }
}
