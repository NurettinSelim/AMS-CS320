package service;

import model.Flight;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IFlightService {
    Flight createFlight(String flightNumber, LocalDateTime departureTime, LocalDateTime arrivalTime,
                        String departure, String destination, int planeId, double economyPrice,
                        double businessPrice, int economyCapacity, int businessCapacity) throws SQLException;

    List<Flight> searchFlights(String departure, String destination, LocalDateTime departureTime) 
            throws SQLException;

    List<Flight> getAllFlights() throws SQLException;

    Flight getFlightById(int id) throws SQLException;

    void updateFlight(Flight flight) throws SQLException;

    void deleteFlight(int id) throws SQLException;

    void updateFlightSeats(int flightId, String seatType, int change) throws SQLException;
} 