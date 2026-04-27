package notificationservice;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(50));
        System.out.println("NOTIFICATION SERVICE");
        System.out.println("=".repeat(50));

        NotificationService service = new NotificationService(4);
        service.start();

        NotificationController controller = service.getController();

        controller.createTask(1L, "PASSENGER", "1", "Your trip #1 has been assigned to driver Mike");
        controller.createTask(1L, "DRIVER", "1", "New trip #1 assigned to you");
        controller.createTask(2L, "PASSENGER", "2", "Your trip #2 is in progress");
        controller.createTask(2L, "DRIVER", "2", "Trip #2 status changed to IN_PROGRESS");
        controller.createTask(3L, "PASSENGER", "3", "Your trip #3 is completed");

        Thread.sleep(5000);

        System.out.println("\nPending tasks: " +
                controller.getTasks().values().stream().filter(t ->
                        t.getStatus().equals(NotificationStatus.PENDING.name())).count());

        System.out.println("Processing tasks: " +
                controller.getTasks().values().stream().filter(t ->
                        t.getStatus().equals(NotificationStatus.PROCESSING.name())).count());

        System.out.println("Sent tasks: " +
                controller.getTasks().values().stream().filter(t ->
                        t.getStatus().equals(NotificationStatus.SENT.name())).count());

        service.stop();

        System.out.println("\nNotification Service ready!");
    }
}