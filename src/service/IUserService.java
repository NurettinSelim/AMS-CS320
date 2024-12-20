package service;

import model.Passenger;
import model.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    User authenticate(String email, String password) throws SQLException;
    
    User register(User user) throws SQLException;
    
    //User register(String name, String surname, String email, String password) throws SQLException;
    User register(int id, String email, String password, String role) throws SQLException;
    
    void updateUser(User user) throws SQLException;
    
    List<User> getAllUsers() throws SQLException;

    List<Passenger> getAllPassengers() throws SQLException;
    
    void deleteUser(int userId) throws SQLException;
} 