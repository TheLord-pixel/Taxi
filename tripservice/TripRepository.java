package tripservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {

    public void save(Trip trip) {
        String sql = "INSERT INTO trips (passenger_id, driver_id, origin, destination, status, price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, trip.getPassengerId());
            if (trip.getDriverId() != null) {
                pstmt.setLong(2, trip.getDriverId());
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            pstmt.setString(3, trip.getOrigin());
            pstmt.setString(4, trip.getDestination());
            pstmt.setString(5, trip.getStatus());
            pstmt.setDouble(6, trip.getPrice());
            pstmt.setTimestamp(7, Timestamp.valueOf(trip.getCreatedAt()));
            pstmt.setTimestamp(8, Timestamp.valueOf(trip.getUpdatedAt()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                trip.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Trip findById(Long id) {
        String sql = "SELECT * FROM trips WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractTrip(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public List<Trip> findByPassengerId(Long passengerId) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE passenger_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return trips;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, passengerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                trips.add(extractTrip(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return trips;
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE trips SET status = ?, updated_at = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setTimestamp(2, Timestamp.valueOf(now));
            pstmt.setLong(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private Trip extractTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setId(rs.getLong("id"));
        trip.setPassengerId(rs.getLong("passenger_id"));
        Long driverId = rs.getLong("driver_id");
        if (!rs.wasNull()) {
            trip.setDriverId(driverId);
        }
        trip.setOrigin(rs.getString("origin"));
        trip.setDestination(rs.getString("destination"));
        trip.setStatus(rs.getString("status"));
        trip.setPrice(rs.getDouble("price"));
        trip.setCreatedAt(rs.getTimestamp("created_at").toString());
        trip.setUpdatedAt(rs.getTimestamp("updated_at").toString());
        return trip;
    }

    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return trips;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trips.add(extractTrip(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return trips;
    }
}