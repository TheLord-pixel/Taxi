package notificationservice;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final NotificationController controller;
    private final List<NotificationWorker> workers;
    private final List<Thread> workerThreads;
    private final int workerCount;

    public NotificationService(int workerCount) {
        this.workerCount = workerCount;
        this.controller = new NotificationController();
        this.workers = new ArrayList<>();
        this.workerThreads = new ArrayList<>();
    }

    public void start() {
        System.out.println("Starting Notification Service with " + workerCount + " workers");

        for (int i = 0; i < workerCount; i++) {
            NotificationWorker worker = new NotificationWorker(i + 1, controller);
            workers.add(worker);
            Thread thread = new Thread(worker);
            workerThreads.add(thread);
            thread.start();
        }
    }

    public void stop() {
        System.out.println("Stopping Notification Service...");

        for (NotificationWorker worker : workers) {
            worker.stop();
        }

        for (Thread thread : workerThreads) {
            try {
                thread.interrupt();
                thread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Notification Service stopped");
    }

    public NotificationController getController() {
        return controller;
    }
}