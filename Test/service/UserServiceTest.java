package service;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userService = new UserService();
    }

    @Test
    void authenticate() throws SQLException {
        String email = "test@example.com";
        String password = "password123";
        User user = new User(1, email, password, "passenger","oscar", "isaac");

        userRepository.create(user);

        User authenticatedUser = userService.authenticate(email, password);

        assertNotNull(authenticatedUser);
        assertEquals(user.getEmail(), authenticatedUser.getEmail());
        assertEquals(user.getPassword(), authenticatedUser.getPassword());
        assertEquals(user.getRole(), authenticatedUser.getRole());
    }

    @Test
    void authenticate_invalidCredentials() throws SQLException {
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = new User(1, "test@example.com", "password123", "passenger", "oscar", "isaac");

        userRepository.create(user);

        User authenticatedUser = userService.authenticate(email, password);

        assertNull(authenticatedUser);
    }

    @Test
    void authenticate_userNotFound() throws SQLException {
        String email = "nonexistent@example.com";
        String password = "password123";

        User authenticatedUser = userService.authenticate(email, password);

        assertNull(authenticatedUser);
    }

    @Test
    void register() throws SQLException {
        User user = new User(0, "newuser@example.com", "password123", "USER", "oscar", "isaac");

        User createdUser = userService.register(user);

        assertNotNull(createdUser.getId());
        assertEquals("newuser@example.com", createdUser.getEmail());
        assertEquals("USER", createdUser.getRole());
    }

    @Test
    void register_emailAlreadyExists() throws SQLException {
        User existingUser = new User(0, "existinguser@example.com", "password123", "passenger","oscar", "isaac");
        userRepository.create(existingUser);

        User newUser = new User(0, "existinguser@example.com", "password456", "passenger","oscar", "isaac");

        assertThrows(IllegalArgumentException.class, () -> userService.register(newUser));
    }

    @Test
    void updateUser() throws SQLException {
        User existingUser = new User(1, "user@example.com", "password123", "passenger","oscar", "isaac");
        userRepository.create(existingUser);

        existingUser.setPassword("newpassword123");

        userService.updateUser(existingUser);

        User updatedUser = userRepository.findByEmail("user@example.com");
        assertEquals("newpassword123", updatedUser.getPassword());
    }

    @Test
    void updateUser_emailAlreadyExists() throws SQLException {
        User existingUser1 = new User(1, "user1@example.com", "password123", "passenger","oscar", "isaac");
        userRepository.create(existingUser1);

        User existingUser2 = new User(2, "user2@example.com", "password456", "passenger","oscar", "isaac");
        userRepository.create(existingUser2);

        existingUser1.setEmail("user2@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(existingUser1));
    }

    @Test
    void getAllUsers() throws SQLException {
        User user1 = new User(1, "user1@example.com", "password123", "passenger","oscar", "isaac");
        User user2 = new User(2, "user2@example.com", "password456", "manager","jennifer", "lawrence");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size()); //with initial admin

        boolean foundUser1 = false;
        boolean foundUser2 = false;

        for (User user : users) {
            if (user.getId() == user1.getId() && user.getEmail().equals(user1.getEmail())) {
                foundUser1 = true;
            }
            if (user.getId() == user2.getId() && user.getEmail().equals(user2.getEmail())) {
                foundUser2 = true;
            }
        }

        assertTrue(foundUser1);
        assertTrue(foundUser2);
    }

    @Test
    void deleteUser() throws SQLException {
        User user = new User(1, "user1@example.com", "password123", "passenger","oscar", "isaac");

        userRepository.create(user);
        userService.deleteUser(user.getId());

        User deletedUser = userRepository.findByEmail(user.getEmail());
        assertNull(deletedUser);
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