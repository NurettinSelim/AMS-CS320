package service;

import model.Flight;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FlightService implements IFlightService {
    @Override
    public Flight createFlight(String flightNumber, LocalDateTime departureTime, LocalDateTime arrivalTime, String departure, String destination, int planeId, double economyPrice, double businessPrice) throws SQLException {
        return null;
    }

    @Override
    public List<Flight> searchFlights(String departure, String destination, LocalDateTime departureTime) throws SQLException {
        return null;
    }

    @Override
    public List<Flight> getAllFlights() throws SQLException {
        return null;
    }

    @Override
    public Flight getFlightById(int id) throws SQLException {
        return null;
    }

    @Override
    public void updateFlight(Flight flight) throws SQLException {

    }

    @Override
    public void deleteFlight(int id) throws SQLException {

    }

    @Override
    public void updateFlightSeats(int flightId, String seatType, int change) throws SQLException {

    }
}
