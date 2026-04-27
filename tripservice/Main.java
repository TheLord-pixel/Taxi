package tripservice;

public class Main {
    public static void main(String[] args) {
        userservice.Main.main(args);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("TRIP SERVICE");
        System.out.println("=".repeat(50));

        TripController tripController = new TripController();

        try {
            Trip trip = tripController.createTrip(1L, "Moscow, Tverskaya 1", "Moscow, Red Square");
            System.out.println("Trip created successfully!");

            Trip found = tripController.getTrip(trip.getId());
            System.out.println("Trip info: " + found.getOrigin() + " -> " + found.getDestination());

            tripController.updateTripStatus(trip.getId(), TripStatus.IN_PROGRESS.name(), found.getDriverId());
            tripController.updateTripStatus(trip.getId(), TripStatus.COMPLETED.name(), found.getDriverId());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\nTotal trips: " + TripController.getTrips().size());
        System.out.println("Trip Service ready!");
    }
}