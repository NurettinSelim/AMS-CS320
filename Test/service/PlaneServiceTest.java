package service;

import model.Plane;
import repository.PlaneRepository;
import util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

class PlaneServiceTest {

    private PlaneService planeService;
    private PlaneRepository planeRepository;

    @BeforeEach
    void setUp() throws SQLException {

        planeRepository = new PlaneRepository();
        planeService = new PlaneService();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM planes"); // Clean up before each test
        }
    }

    @Test
    void createPlane() throws SQLException {
        String planeName = "Boeing 747";
        int capacity = 200;

        Plane createdPlane = planeService.createPlane(planeName, capacity);

        assertNotNull(createdPlane);
        assertTrue(createdPlane.getId() > 0);
        assertEquals(planeName, createdPlane.getPlaneName());
        assertEquals(capacity, createdPlane.getCapacity());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM planes WHERE id = ?")) {
            stmt.setInt(1, createdPlane.getId());
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(planeName, rs.getString("plane_name"));
            assertEquals(capacity, rs.getInt("capacity"));
        }
    }

    @Test
    void getAllPlanes() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO planes (plane_name, capacity) VALUES (?, ?)")) {
            stmt.setString(1, "Boeing 747");
            stmt.setInt(2, 200);
            stmt.executeUpdate();

            stmt.setString(1, "Airbus A320");
            stmt.setInt(2, 150);
            stmt.executeUpdate();
        }

        List<Plane> planes = planeService.getAllPlanes();

        assertNotNull(planes);
        assertEquals(2, planes.size());
        assertEquals("Boeing 747", planes.get(0).getPlaneName());
        assertEquals(200, planes.get(0).getCapacity());
        assertEquals("Airbus A320", planes.get(1).getPlaneName());
        assertEquals(150, planes.get(1).getCapacity());
    }

    @Test
    void getPlaneById() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeService.createPlane(plane.getPlaneName(), plane.getCapacity());

        Plane fetchedPlane = planeService.getPlaneById(plane.getId());

        assertNotNull(fetchedPlane);
        assertEquals(plane.getId(), fetchedPlane.getId());
        assertEquals(plane.getPlaneName(), fetchedPlane.getPlaneName());
        assertEquals(plane.getCapacity(), fetchedPlane.getCapacity());
    }

    @Test
    void updatePlane() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeService.createPlane(plane.getPlaneName(), plane.getCapacity());

        plane.setPlaneName("Boeing 777");
        plane.setCapacity(250);

        planeService.updatePlane(plane);

        Plane updatedPlane = planeService.getPlaneById(plane.getId());

        assertNotNull(updatedPlane);
        assertEquals("Boeing 777", updatedPlane.getPlaneName());
        assertEquals(250, updatedPlane.getCapacity());
    }

    @Test
    void deletePlane() throws SQLException {
        Plane plane = new Plane(0, "Boeing 747", 200);
        plane = planeService.createPlane(plane.getPlaneName(), plane.getCapacity());

        int planeId = plane.getId();

        planeService.deletePlane(planeId);

        Plane deletedPlane = planeService.getPlaneById(planeId);

        assertNull(deletedPlane);
    }

    @Test
    void isPlaneAvailable() throws SQLException {
        Plane plane = planeService.createPlane("Boeing 747", 200);

        Time departureTime = Time.valueOf("15:00:00");
        Time arrivalTime = new Time(departureTime.getTime() + 2 * 60 * 60 * 1000);

        boolean isAvailable = planeService.isPlaneAvailable(plane.getId(), departureTime, arrivalTime);
        assertTrue(isAvailable);

        String departure = "New York";
        String destination = "Los Angeles";
        double economyPrice = 150.0;
        double businessPrice = 300.0;
        int economySeatsAvailable = 150;
        int businessSeatsAvailable = 50;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
            INSERT INTO flights (plane_id, flight_number, departure_time, arrival_time, departure, destination, economy_price, business_price, economy_seats_available, business_seats_available) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)) {

            stmt.setInt(1, plane.getId());
            stmt.setString(2, "FL123");
            stmt.setTime(3, departureTime);
            stmt.setTime(4, arrivalTime);

            stmt.setString(5,  departure);
            stmt.setString(6, destination);

            stmt.setDouble(7,economyPrice);
            stmt.setDouble(8, businessPrice);
            stmt.setInt(9, economySeatsAvailable);
            stmt.setInt(10, businessSeatsAvailable);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting flight data", e);
        }

        isAvailable = planeService.isPlaneAvailable(plane.getId(), departureTime, arrivalTime);
        assertFalse(isAvailable);
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
