package repository;

import model.Plane;

import java.sql.SQLException;
import java.util.List;

public interface IPlaneRepository {
    Plane create(Plane plane) throws SQLException;

    List<Plane> findAll() throws SQLException;

    Plane findById(int id) throws SQLException;

    void update(Plane plane) throws SQLException;

    void delete(int id) throws SQLException;
}