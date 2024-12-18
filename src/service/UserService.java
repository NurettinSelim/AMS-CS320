package service;

import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService implements IUserService{
    @Override
    public User authenticate(String email, String password) throws SQLException {
        return null;
    }

    @Override
    public User register(User user) throws SQLException {
        return null;
    }

    @Override
    public User register(String name, String surname, String email, String password) throws SQLException {
        return null;
    }

    @Override
    public void updateUser(User user) throws SQLException {

    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return null;
    }

    @Override
    public void deleteUser(int userId) throws SQLException {

    }
}
