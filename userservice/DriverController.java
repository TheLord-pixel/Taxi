package userservice;

import java.util.List;

public class DriverController {
    private final DriverRepository repository;

    public Driver getDriverById(Long id) {
        return repository.findById(id);
    }

    public DriverController() {
        this.repository = new DriverRepository();
    }

    public Driver registerDriver(String name, String email, String phone, String licenseNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number is required");
        }

        Driver driver = new Driver();
        driver.setName(name);
        driver.setEmail(email);
        driver.setPhone(phone);
        driver.setLicenseNumber(licenseNumber);
        driver.setStatus(DriverStatus.AVAILABLE.name());
        driver.setCreatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        repository.save(driver);

        System.out.println("Driver registered: " + name + " (ID: " + driver.getId() + ")");
        return driver;
    }

    public Driver getDriver(Long id) {
        Driver driver = repository.findById(id);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with ID: " + id);
        }
        return driver;
    }

    public List<Driver> getAllDrivers() {
        return repository.findAll();
    }

    public Driver findAvailableDriver() {
        return repository.findAvailableDriver();
    }

    public void updateDriverStatus(Long driverId, String status) {
        Driver driver = repository.findById(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found with ID: " + driverId);
        }
        repository.updateStatus(driverId, status);
        System.out.println("Driver " + driverId + " status updated to: " + status);
    }

    public Driver updateDriverStatusAndReturn(Long driverId, String status) {
        updateDriverStatus(driverId, status);
        return repository.findById(driverId);
    }
}