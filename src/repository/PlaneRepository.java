package repository;

import model.Plane;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaneRepository implements IPlaneRepository{  //changed
    @Override
    public Plane create(Plane plane) throws SQLException {
        String query = """
            INSERT INTO planes (plane_name, capacity)
            VALUES (?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, plane.getPlaneName());
            stmt.setInt(2, plane.getCapacity());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating plane failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    plane.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating plane failed, no ID obtained.");
                }
            }
        }
        return plane;
    }

    @Override
    public List<Plane> findAll() throws SQLException {
        String query = "SELECT * FROM planes";
        List<Plane> planes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                planes.add(new Plane(
                        rs.getInt("id"),
                        rs.getString("plane_name"),
                        rs.getInt("capacity")
                ));
            }
        }
        return planes;
    }

    @Override
    public Plane findById(int id) throws SQLException {
        String query = "SELECT * FROM planes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Plane(
                        rs.getInt("id"),
                        rs.getString("plane_name"),
                        rs.getInt("capacity")
                );
            }
        }
        return null; // Return null if not found
    }

    @Override
    public void update(Plane plane) throws SQLException {
        String query = """
            UPDATE planes 
            SET plane_name = ?, capacity = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, plane.getPlaneName());
            stmt.setInt(2, plane.getCapacity());
            stmt.setInt(3, plane.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM planes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
