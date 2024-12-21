package service;

import model.Flight;
import model.Plane;
import repository.FlightRepository;
import repository.PlaneRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FlightService implements IFlightService {

    private final FlightRepository flightRepository;
    private final PlaneRepository planeRepository;

    public FlightService() {
        this.flightRepository = new FlightRepository();
        this.planeRepository = new PlaneRepository();
    }
    @Override
    public Flight createFlight(String flightNumber, LocalDateTime departureTime, LocalDateTime arrivalTime,
                               String departure, String destination, int planeId, double economyPrice,
                               double businessPrice, int economyCapacity, int businessCapacity) throws SQLException {
                                                        //capacity parametrelerini ekledim

        Plane plane = planeRepository.findById(planeId);
        if (plane == null || plane.isEmpty()) {
            throw new IllegalArgumentException("Invalid plane ID");
        }

        if (!planeRepository.isPlaneAvailable(planeId, departureTime, arrivalTime)) {
            throw new IllegalArgumentException("Plane is not available for the specified time slot");
        }

        Flight flight = new Flight(0, flightNumber, departureTime, arrivalTime, departure, destination,
                planeId, economyPrice, businessPrice, economyCapacity, businessCapacity);

        return flightRepository.create(flight);
    }

    @Override
    public List<Flight> searchFlights(String departure, String destination, LocalDateTime departureTime) throws SQLException {
        return flightRepository.searchFlights(departure, destination, departureTime);
    }

    @Override
    public List<Flight> getAllFlights() throws SQLException {
        return flightRepository.findAll();
    }

    @Override
    public Flight getFlightById(int id) throws SQLException {
        return flightRepository.findById(id);
    }

    @Override
    public void updateFlight(Flight flight) throws SQLException {
        if (planeRepository.findById(flight.getPlaneId()).isEmpty()) {
            throw new IllegalArgumentException("Invalid plane ID");
        }
        flightRepository.update(flight);
    }

    @Override
    public void deleteFlight(int id) throws SQLException {
        flightRepository.delete(id);
    }

    @Override
    public void updateFlightSeats(int flightId, String seatType, int change) throws SQLException {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("Invalid flight ID");
        }
        if ("ECONOMY".equalsIgnoreCase(seatType)) {
            int newSeats = flight.getEconomySeatsAvailable() + change;
            if (newSeats < 0) {
                throw new IllegalArgumentException("Not enough economy seats available");
            }
            flight.setEconomySeatsAvailable(newSeats);
        } else if ("BUSINESS".equalsIgnoreCase(seatType)) {
            int newSeats = flight.getBusinessSeatsAvailable() + change;
            if (newSeats < 0) {
                throw new IllegalArgumentException("Not enough business seats available");
            }
            flight.setBusinessSeatsAvailable(newSeats);
        } else {
            throw new IllegalArgumentException("Invalid seat type");
        }

        flightRepository.update(flight);
    }
}


