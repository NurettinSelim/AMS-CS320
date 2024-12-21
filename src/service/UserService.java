package service;

import model.User;
import repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService implements IUserService{
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    @Override
    public User authenticate(String email, String password) throws SQLException {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) throws SQLException {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.create(user);
    }

    @Override
    public User register(int id, String email, String password, String role, String name, String surname) throws SQLException {
        return register(new User(0, email, password, "passenger", name, surname));
    }

    @Override
    public void updateUser(User user) throws SQLException {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            throw new IllegalArgumentException("Email already exists");
        }
        userRepository.update(user);
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllPassengers() throws SQLException {
        return userRepository.findAllPassengers();
    }
    @Override
    public void deleteUser(int userId) throws SQLException {
        userRepository.delete(userId);
    }
}
