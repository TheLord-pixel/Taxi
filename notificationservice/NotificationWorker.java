package notificationservice;

public class NotificationWorker implements Runnable {
    private final int workerId;
    private final NotificationController controller;
    private volatile boolean running;

    public NotificationWorker(int workerId, NotificationController controller) {
        this.workerId = workerId;
        this.controller = controller;
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                NotificationTask task = controller.getPendingTask();
                if (task != null) {
                    processTask(task);
                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Worker " + workerId + " error: " + e.getMessage());
            }
        }
        System.out.println("Worker " + workerId + " stopped");
    }

    private void processTask(NotificationTask task) {
        System.out.println("Worker " + workerId + " processing task " + task.getId() +
                " for trip " + task.getTripId());

        try {
            Thread.sleep(500);

            System.out.println("Worker " + workerId + " sent notification to " +
                    task.getRecipientType() + " " + task.getRecipientId() +
                    ": " + task.getMessage());

            controller.markAsSent(task.getId());

        } catch (Exception e) {
            System.err.println("Worker " + workerId + " failed to send notification for task " +
                    task.getId() + ": " + e.getMessage());
            controller.markAsFailed(task.getId());
        }
    }

    public void stop() {
        running = false;
    }
}