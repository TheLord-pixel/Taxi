package userservice;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.getConnection();

        PassengerController passengerController = new PassengerController();
        DriverController driverController = new DriverController();

        System.out.println("=".repeat(50));
        System.out.println("USER SERVICE");
        System.out.println("=".repeat(50));

        // Проверяем, есть ли уже пассажиры
        if (passengerController.getAllPassengers().isEmpty()) {
            passengerController.registerPassenger("John Doe", "john@example.com", "+123456789");
            passengerController.registerPassenger("Jane Smith", "jane@example.com", "+987654321");
        } else {
            System.out.println("Passengers already exist, skipping creation");
        }

        if (driverController.getAllDrivers().isEmpty()) {
            driverController.registerDriver("Mike Driver", "mike@taxi.com", "+111111111", "LIC12345");
            driverController.registerDriver("Sarah Driver", "sarah@taxi.com", "+222222222", "LIC67890");
        } else {
            System.out.println("Drivers already exist, skipping creation");
        }

        System.out.println("\nPassengers count: " + passengerController.getAllPassengers().size());
        System.out.println("Drivers count: " + driverController.getAllDrivers().size());

        Driver available = driverController.findAvailableDriver();
        if (available != null) {
            System.out.println("Available driver: " + available.getName());
        }

        System.out.println("\n✅ User Service ready!");
    }
}