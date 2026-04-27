package tripservice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;

class UserServiceClient {
    public static boolean passengerExists(Long id) {
        var passengers = userservice.PassengerController.getPassengers();
        return passengers.containsKey(id);
    }

    public static userservice.Driver findAvailableDriver() {
        return new userservice.DriverController().findAvailableDriver();
    }

    public static void updateDriverStatus(Long driverId, String status) {
        new userservice.DriverController().updateDriverStatus(driverId, status);
    }

    public static String getPassengerName(Long id) {
        var passenger = userservice.PassengerController.getPassengers().get(id);
        return passenger != null ? passenger.getName() : "Unknown";
    }

    public static String getDriverName(Long id) {
        var driver = userservice.DriverController.getDrivers().get(id);
        return driver != null ? driver.getName() : "Unknown";
    }
}

class NotificationServiceClient {
    private static notificationservice.NotificationController controller;

    public static void setController(notificationservice.NotificationController c) {
        controller = c;
    }

    public static void notifyPassenger(Long passengerId, String message) {
        if (controller != null) {
            controller.createTask(null, "PASSENGER", String.valueOf(passengerId), message);
        }
    }

    public static void notifyDriver(Long driverId, String message) {
        if (controller != null) {
            controller.createTask(null, "DRIVER", String.valueOf(driverId), message);
        }
    }
}

public class TripController {
    private static final ConcurrentHashMap<Long, Trip> trips = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public synchronized Trip createTrip(Long passengerId, String origin, String destination) {
        if (!UserServiceClient.passengerExists(passengerId)) {
            throw new IllegalArgumentException("Passenger not found: " + passengerId);
        }

        userservice.Driver availableDriver = UserServiceClient.findAvailableDriver();
        if (availableDriver == null) {
            throw new IllegalStateException("No available drivers at the moment");
        }

        synchronized (availableDriver) {
            if (!"AVAILABLE".equals(availableDriver.getStatus())) {
                throw new IllegalStateException("Driver became unavailable");
            }

            UserServiceClient.updateDriverStatus(availableDriver.getId(), "BUSY");

            long id = idGenerator.getAndIncrement();
            Double price = calculatePrice(origin, destination);
            String now = LocalDateTime.now().toString();

            Trip trip = new Trip(id, passengerId, availableDriver.getId(),
                    origin, destination, TripStatus.DRIVER_ASSIGNED.name(),
                    price, now, now);
            trips.put(id, trip);

            System.out.println("Trip created: " + id + " | Driver: " + availableDriver.getName() +
                    " | Price: $" + price);

            String passengerName = UserServiceClient.getPassengerName(passengerId);
            String driverName = availableDriver.getName();

            NotificationServiceClient.notifyPassenger(passengerId,
                    "Dear " + passengerName + ", your trip #" + id + " from " + origin + " to " + destination +
                            " has been assigned to driver " + driverName + ". Price: $" + price);

            NotificationServiceClient.notifyDriver(availableDriver.getId(),
                    "Dear " + driverName + ", you have a new trip #" + id + " from " + origin + " to " + destination +
                            ". Passenger: " + passengerName);

            return trip;
        }
    }

    public Trip getTrip(Long id) {
        Trip trip = trips.get(id);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + id);
        }
        return trip;
    }

    public ConcurrentHashMap<Long, Trip> getTripsByPassenger(Long passengerId) {
        ConcurrentHashMap<Long, Trip> result = new ConcurrentHashMap<>();
        for (var entry : trips.entrySet()) {
            if (entry.getValue().getPassengerId().equals(passengerId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public Trip updateTripStatus(Long id, String status, Long driverId) {
        Trip trip = trips.get(id);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + id);
        }

        if (!trip.getDriverId().equals(driverId)) {
            throw new SecurityException("Only assigned driver can update trip status");
        }

        String oldStatus = trip.getStatus();
        trip.setStatus(status);
        trip.setUpdatedAt(LocalDateTime.now().toString());

        String passengerName = UserServiceClient.getPassengerName(trip.getPassengerId());
        String driverName = UserServiceClient.getDriverName(trip.getDriverId());

        if (status.equals(TripStatus.IN_PROGRESS.name())) {
            NotificationServiceClient.notifyPassenger(trip.getPassengerId(),
                    "Dear " + passengerName + ", driver " + driverName + " has started your trip #" + id);

            NotificationServiceClient.notifyDriver(trip.getDriverId(),
                    "Dear " + driverName + ", trip #" + id + " is now in progress");
        }

        if (status.equals(TripStatus.COMPLETED.name())) {
            NotificationServiceClient.notifyPassenger(trip.getPassengerId(),
                    "Dear " + passengerName + ", your trip #" + id + " is completed. Thank you for choosing us!");

            NotificationServiceClient.notifyDriver(trip.getDriverId(),
                    "Dear " + driverName + ", trip #" + id + " is completed. You are now available for new trips");

            UserServiceClient.updateDriverStatus(trip.getDriverId(), "AVAILABLE");
            System.out.println("Driver " + trip.getDriverId() + " is now AVAILABLE");
        }

        if (status.equals(TripStatus.CANCELLED.name())) {
            NotificationServiceClient.notifyPassenger(trip.getPassengerId(),
                    "Dear " + passengerName + ", your trip #" + id + " has been cancelled");

            NotificationServiceClient.notifyDriver(trip.getDriverId(),
                    "Dear " + driverName + ", trip #" + id + " has been cancelled");

            UserServiceClient.updateDriverStatus(trip.getDriverId(), "AVAILABLE");
            System.out.println("Driver " + trip.getDriverId() + " is now AVAILABLE");
        }

        System.out.println("Trip " + id + " status updated to: " + status);
        return trip;
    }

    private Double calculatePrice(String origin, String destination) {
        double distance = Math.random() * 20 + 1;
        double price = distance * 2.0;
        return Math.round(price * 100.0) / 100.0;
    }

    public static ConcurrentHashMap<Long, Trip> getTrips() {
        return trips;
    }
}