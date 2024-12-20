package service;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import java.sql.SQLException;
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
        User user = new User(1, email, password, "USER");

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
        User user = new User(1, "test@example.com", "password123", "USER");

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
        User user = new User(0, "newuser@example.com", "password123", "USER");

        User createdUser = userService.register(user);

        assertNotNull(createdUser.getId());
        assertEquals("newuser@example.com", createdUser.getEmail());
        assertEquals("USER", createdUser.getRole());
    }

    @Test
    void register_emailAlreadyExists() throws SQLException {
        User existingUser = new User(0, "existinguser@example.com", "password123", "USER");
        userRepository.create(existingUser);

        User newUser = new User(0, "existinguser@example.com", "password456", "USER");

        assertThrows(IllegalArgumentException.class, () -> userService.register(newUser));
    }

    @Test
    void updateUser() throws SQLException {
        User existingUser = new User(1, "user@example.com", "password123", "USER");
        userRepository.create(existingUser);

        existingUser.setPassword("newpassword123");

        userService.updateUser(existingUser);

        User updatedUser = userRepository.findByEmail("user@example.com");
        assertEquals("newpassword123", updatedUser.getPassword());
    }

    @Test
    void updateUser_emailAlreadyExists() throws SQLException {
        User existingUser1 = new User(1, "user1@example.com", "password123", "USER");
        userRepository.create(existingUser1);

        User existingUser2 = new User(2, "user2@example.com", "password456", "USER");
        userRepository.create(existingUser2);

        existingUser1.setEmail("user2@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(existingUser1));
    }

    @Test
    void getAllUsers() throws SQLException {
        User user1 = new User(1, "user1@example.com", "password123", "USER");
        User user2 = new User(2, "user2@example.com", "password456", "ADMIN");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void deleteUser() throws SQLException {
        User user = new User(1, "user1@example.com", "password123", "USER");

        userRepository.create(user);
        userService.deleteUser(user.getId());

        User deletedUser = userRepository.findByEmail(user.getEmail());
        assertNull(deletedUser);
    }


}
