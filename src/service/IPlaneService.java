package service;

import model.Plane;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

public interface IPlaneService {
    Plane createPlane(String planeName, int capacity) throws SQLException;
    
    List<Plane> getAllPlanes() throws SQLException;
    
    Plane getPlaneById(int id) throws SQLException;
    
    void updatePlane(Plane plane) throws SQLException;
    
    void deletePlane(int id) throws SQLException;
    
    boolean isPlaneAvailable(int planeId, Time departureTime, Time arrivalTime)
            throws SQLException;
} 