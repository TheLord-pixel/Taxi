package userservice;

import java.util.List;

public class PassengerController {
    private final PassengerRepository repository;

    public Passenger getPassengerById(Long id) {
        return repository.findById(id);
    }

    public PassengerController() {
        this.repository = new PassengerRepository();
    }

    public Passenger registerPassenger(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        Passenger passenger = new Passenger();
        passenger.setName(name);
        passenger.setEmail(email);
        passenger.setPhone(phone);
        passenger.setCreatedAt(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        repository.save(passenger);

        System.out.println("Passenger registered: " + name + " (ID: " + passenger.getId() + ")");
        return passenger;
    }

    public Passenger getPassenger(Long id) {
        Passenger passenger = repository.findById(id);
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger not found with ID: " + id);
        }
        return passenger;
    }

    public List<Passenger> getAllPassengers() {
        return repository.findAll();
    }

    public boolean passengerExists(Long id) {
        return repository.existsById(id);
    }
}