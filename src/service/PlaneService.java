package service;

import model.Plane;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PlaneService implements IPlaneService{
    @Override
    public Plane createPlane(String planeNumber, int economyCapacity, int businessCapacity) throws SQLException {
        return null;
    }

    @Override
    public List<Plane> getAllPlanes() throws SQLException {
        return null;
    }

    @Override
    public Plane getPlaneById(int id) throws SQLException {
        return null;
    }

    @Override
    public void updatePlane(Plane plane) throws SQLException {

    }

    @Override
    public void deletePlane(int id) throws SQLException {

    }

    @Override
    public boolean isPlaneAvailable(int planeId, LocalDateTime departureTime, LocalDateTime arrivalTime) throws SQLException {
        return false;
    }
}
