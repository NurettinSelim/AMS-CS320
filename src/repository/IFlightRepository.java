package repository;

import model.Flight;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

public interface IFlightRepository {
    Flight create(Flight flight) throws SQLException;

    List<Flight> findAll() throws SQLException;

    Flight findById(int id) throws SQLException;

    void update(Flight flight) throws SQLException;

    void delete(int id) throws SQLException;

    List<Flight> searchFlights(String departure, String destination, Time departureTime) throws SQLException;
}