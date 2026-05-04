package tripservice;

import userservice.DriverController;
import userservice.PassengerController;
import notificationservice.NotificationController;
import java.util.List;
import java.time.LocalDateTime;

public class TripController {
    private final TripRepository repository;
    private final PassengerController passengerController;
    private final DriverController driverController;
    private NotificationController notificationController;

    public TripController() {
        this.repository = new TripRepository();
        this.passengerController = new PassengerController();
        this.driverController = new DriverController();
    }

    public void setNotificationController(NotificationController nc) {
        this.notificationController = nc;
    }

    public synchronized Trip createTrip(Long passengerId, String origin, String destination) {
        if (!passengerController.passengerExists(passengerId)) {
            throw new IllegalArgumentException("Passenger not found: " + passengerId);
        }

        var availableDriver = driverController.findAvailableDriver();
        if (availableDriver == null) {
            throw new IllegalStateException("No available drivers at the moment");
        }

        synchronized (availableDriver) {
            driverController.updateDriverStatus(availableDriver.getId(), "BUSY");

            Double price = calculatePrice(origin, destination);
            String now = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Trip trip = new Trip();
            trip.setPassengerId(passengerId);
            trip.setDriverId(availableDriver.getId());
            trip.setOrigin(origin);
            trip.setDestination(destination);
            trip.setStatus(TripStatus.DRIVER_ASSIGNED.name());
            trip.setPrice(price);
            trip.setCreatedAt(now);
            trip.setUpdatedAt(now);

            repository.save(trip);

            System.out.println("Trip created: " + trip.getId() + " | Driver: " + availableDriver.getName() +
                    " | Price: $" + price);

            if (notificationController != null) {
                notificationController.createTask(trip.getId(), "PASSENGER", String.valueOf(passengerId),
                        "Your trip #" + trip.getId() + " from " + origin + " to " + destination +
                                " has been assigned to driver " + availableDriver.getName());

                notificationController.createTask(trip.getId(), "DRIVER", String.valueOf(availableDriver.getId()),
                        "You have a new trip #" + trip.getId() + " from " + origin + " to " + destination);
            }

            return trip;
        }
    }

    public Trip getTrip(Long id) {
        Trip trip = repository.findById(id);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + id);
        }
        return trip;
    }

    public Trip updateTripStatus(Long id, String status, Long driverId) {
        Trip trip = repository.findById(id);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + id);
        }

        if (!trip.getDriverId().equals(driverId)) {
            throw new SecurityException("Only assigned driver can update trip status");
        }

        repository.updateStatus(id, status);

        if (status.equals(TripStatus.COMPLETED.name()) || status.equals(TripStatus.CANCELLED.name())) {
            driverController.updateDriverStatus(trip.getDriverId(), "AVAILABLE");
            System.out.println("Driver " + trip.getDriverId() + " is now AVAILABLE");
        }

        if (notificationController != null) {
            notificationController.createTask(id, "PASSENGER", String.valueOf(trip.getPassengerId()),
                    "Trip #" + id + " status changed to: " + status);

            notificationController.createTask(id, "DRIVER", String.valueOf(driverId),
                    "Trip #" + id + " status changed to: " + status);
        }

        System.out.println("Trip " + id + " status updated to: " + status);
        return repository.findById(id);
    }

    private Double calculatePrice(String origin, String destination) {
        double distance = Math.random() * 20 + 1;
        double price = distance * 2.0;
        return Math.round(price * 100.0) / 100.0;
    }

    public List<Trip> getAllTrips() {
        return repository.findAll();
    }

    public void printStats() {
        System.out.println("\n=== СТАТИСТИКА ===");
        System.out.println("Всего поездок: " + getAllTrips().size());
        System.out.println("Завершено: " + countByStatus(TripStatus.COMPLETED.name()));
        System.out.println("Отменено: " + countByStatus(TripStatus.CANCELLED.name()));

        double avgPrice = getAllTrips().stream()
                .mapToDouble(Trip::getPrice)
                .average()
                .orElse(0);
        System.out.println("Средняя цена: $" + avgPrice);
    }

    private long countByStatus(String status) {
        return getAllTrips().stream()
                .filter(t -> t.getStatus().equals(status))
                .count();
    }

    public void printStatistics() {
        List<Trip> trips = getAllTrips();

        if (trips.isEmpty()) {
            System.out.println("Нет поездок для статистики");
            return;
        }

        long completed = trips.stream()
                .filter(t -> TripStatus.COMPLETED.name().equals(t.getStatus()))
                .count();

        long cancelled = trips.stream()
                .filter(t -> TripStatus.CANCELLED.name().equals(t.getStatus()))
                .count();

        double totalPrice = trips.stream()
                .mapToDouble(Trip::getPrice)
                .sum();

        double avgPrice = trips.stream()
                .mapToDouble(Trip::getPrice)
                .average()
                .orElse(0);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("СТАТИСТИКА ПОЕЗДОК");
        System.out.println("=".repeat(50));
        System.out.println("Всего поездок: " + trips.size());
        System.out.println("Завершено: " + completed);
        System.out.println("Отменено: " + cancelled);
        System.out.printf("Общая выручка: $%.2f%n", totalPrice);
        System.out.printf("Средняя цена: $%.2f%n", avgPrice);
    }
}