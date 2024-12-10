package service;

import model.Plane;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IPlaneService {
    Plane createPlane(String planeNumber, int economyCapacity, int businessCapacity) throws SQLException;
    
    List<Plane> getAllPlanes() throws SQLException;
    
    Plane getPlaneById(int id) throws SQLException;
    
    void updatePlane(Plane plane) throws SQLException;
    
    void deletePlane(int id) throws SQLException;
    
    boolean isPlaneAvailable(int planeId, LocalDateTime departureTime, LocalDateTime arrivalTime) 
            throws SQLException;
} 