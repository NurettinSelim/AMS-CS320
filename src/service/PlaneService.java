package service;

import model.Plane;
import repository.PlaneRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PlaneService implements IPlaneService{
    private final PlaneRepository planeRepository;
    public PlaneService() {
        this.planeRepository = new PlaneRepository();
    }

    @Override
    public Plane createPlane(String planeName, int capacity) throws SQLException {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        Plane plane = new Plane(0, planeName, capacity);
        return planeRepository.create(plane);
    }

    @Override
    public List<Plane> getAllPlanes() throws SQLException {
        return planeRepository.findAll();
    }

    @Override
    public Plane getPlaneById(int id) throws SQLException {
        return planeRepository.findById(id);
    }

    @Override
    public void updatePlane(Plane plane) throws SQLException {
        if (plane.getCapacity()< 0) {
            throw new IllegalArgumentException("Capacities cannot be negative");
        }
        planeRepository.update(plane);
    }

    @Override
    public void deletePlane(int id) throws SQLException {
        planeRepository.delete(id);
    }

    @Override
    public boolean isPlaneAvailable(int planeId, LocalDateTime departureTime, LocalDateTime arrivalTime) throws SQLException {
        return planeRepository.isPlaneAvailable(planeId, departureTime, arrivalTime);
    }
}
