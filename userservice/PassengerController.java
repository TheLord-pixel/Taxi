package userservice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PassengerController {
    private static final ConcurrentHashMap<Long, Passenger> passengers = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public Passenger registerPassenger(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        long id = idGenerator.getAndIncrement();
        Passenger passenger = new Passenger(id, name, email, phone,
                java.time.LocalDateTime.now().toString());
        passengers.put(id, passenger);

        System.out.println("Passenger registered: " + name + " (ID: " + id + ")");
        return passenger;
    }

    public Passenger getPassenger(Long id) {
        Passenger passenger = passengers.get(id);
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger not found with ID: " + id);
        }
        return passenger;
    }

    public static ConcurrentHashMap<Long, Passenger> getPassengers() {
        return passengers;
    }
}