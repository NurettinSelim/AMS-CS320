package repository;

import model.User;
import view.PassengerListPanel;
import model.Passenger;

import java.sql.SQLException;
import java.util.List;

public interface IUserRepository {
    User create(User user) throws SQLException;

    User findByEmail(String email) throws SQLException;

    void update(User user) throws SQLException;

    List<User> findAll() throws SQLException;

    List<Passenger> findAllPassengers() throws SQLException;
    void delete(int id) throws SQLException;
}