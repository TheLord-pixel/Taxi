package userservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    public void save(Driver driver) {
        String sql = "INSERT INTO drivers (name, email, phone, license_number, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, driver.getName());
            pstmt.setString(2, driver.getEmail());
            pstmt.setString(3, driver.getPhone());
            pstmt.setString(4, driver.getLicenseNumber());
            pstmt.setString(5, driver.getStatus());
            pstmt.setTimestamp(6, Timestamp.valueOf(driver.getCreatedAt()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                driver.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Driver findById(Long id) {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Driver d = new Driver();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setEmail(rs.getString("email"));
                d.setPhone(rs.getString("phone"));
                d.setLicenseNumber(rs.getString("license_number"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at").toString());
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public List<Driver> findAll() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return drivers;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Driver d = new Driver();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setEmail(rs.getString("email"));
                d.setPhone(rs.getString("phone"));
                d.setLicenseNumber(rs.getString("license_number"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at").toString());
                drivers.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return drivers;
    }

    public Driver findAvailableDriver() {
        String sql = "SELECT * FROM drivers WHERE status = 'AVAILABLE' LIMIT 1";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Driver d = new Driver();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setEmail(rs.getString("email"));
                d.setPhone(rs.getString("phone"));
                d.setLicenseNumber(rs.getString("license_number"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at").toString());
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public void updateStatus(Long driverId, String status) {
        String sql = "UPDATE drivers SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, driverId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}