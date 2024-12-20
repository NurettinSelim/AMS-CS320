package repository;

import model.Passenger;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserRepository implements IUserRepository{
    @Override
    public User create(User user) throws SQLException {
        return null;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        return null;
    }

    @Override
    public void update(User user) throws SQLException {

    }

    @Override
    public List<User> findAll() throws SQLException {
        return null;
    }

    @Override
    public List<Passenger> findAllPassengers() throws SQLException {
        return null;
    }


    @Override
    public void delete(int id) throws SQLException {

    }
}
