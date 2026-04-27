package userservice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DriverController {
    private static final ConcurrentHashMap<Long, Driver> drivers = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public Driver registerDriver(String name, String email, String phone, String licenseNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number is required");
        }

        long id = idGenerator.getAndIncrement();
        Driver driver = new Driver(id, name, email, phone, licenseNumber,
                DriverStatus.AVAILABLE.name(), java.time.LocalDateTime.now().toString());
        drivers.put(id, driver);

        System.out.println("Driver registered: " + name + " (ID: " + id + ")");
        return driver;
    }

    public Driver getDriver(Long id) {
        Driver driver = drivers.get(id);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with ID: " + id);
        }
        return driver;
    }

    public Driver updateDriverStatus(Long id, String status) {
        Driver driver = drivers.get(id);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with ID: " + id);
        }

        try {
            DriverStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use: AVAILABLE, BUSY, OFFLINE");
        }

        driver.setStatus(status.toUpperCase());
        System.out.println("Driver " + id + " status updated to: " + status);
        return driver;
    }

    public Driver findAvailableDriver() {
        return drivers.values().stream()
                .filter(d -> DriverStatus.AVAILABLE.name().equals(d.getStatus()))
                .findFirst()
                .orElse(null);
    }

    public static ConcurrentHashMap<Long, Driver> getDrivers() {
        return drivers;
    }
}