package userservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerRepository {

    public void save(Passenger passenger) {
        if (existsByEmail(passenger.getEmail())) {
            System.out.println("Passenger with email " + passenger.getEmail() + " already exists");
            return;
        }
        String sql = "INSERT INTO passengers (name, email, phone, created_at) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, passenger.getName());
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, passenger.getPhone());
            pstmt.setTimestamp(4, Timestamp.valueOf(passenger.getCreatedAt()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                passenger.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Passenger findById(Long id) {
        String sql = "SELECT * FROM passengers WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Passenger p = new Passenger();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                p.setCreatedAt(rs.getTimestamp("created_at").toString());
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public List<Passenger> findAll() {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return passengers;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Passenger p = new Passenger();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                p.setCreatedAt(rs.getTimestamp("created_at").toString());
                passengers.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return passengers;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM passengers WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM passengers WHERE email = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }
}