package tripservice;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        userservice.Main.main(args);

        notificationservice.NotificationService notificationService = new notificationservice.NotificationService(4);
        notificationService.start();

        NotificationServiceClient.setController(notificationService.getController());

        System.out.println("\n" + "=".repeat(50));
        System.out.println("TRIP SERVICE WITH NOTIFICATIONS");
        System.out.println("=".repeat(50));

        TripController tripController = new TripController();

        try {
            Trip trip = tripController.createTrip(1L, "Moscow, Tverskaya 1", "Moscow, Red Square");
            System.out.println("\nTrip created successfully!\n");

            Thread.sleep(2000);

            tripController.updateTripStatus(trip.getId(), TripStatus.IN_PROGRESS.name(), trip.getDriverId());

            Thread.sleep(2000);

            tripController.updateTripStatus(trip.getId(), TripStatus.COMPLETED.name(), trip.getDriverId());

            Thread.sleep(3000);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\nTotal trips: " + TripController.getTrips().size());

        notificationService.stop();

        System.out.println("\nTrip Service ready!");
    }
}