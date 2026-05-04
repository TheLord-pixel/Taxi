package tripservice;

import java.sql.*;

public class RatingController {

    public void addRating(Long tripId, Long passengerId, Long driverId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        String sql = "INSERT INTO ratings (trip_id, passenger_id, driver_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, tripId);
            pstmt.setLong(2, passengerId);
            pstmt.setLong(3, driverId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comment);
            pstmt.executeUpdate();
            System.out.println("Rating added for trip " + tripId + ": " + rating + " stars");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public double getAverageDriverRating(Long driverId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM ratings WHERE driver_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0;
    }
}