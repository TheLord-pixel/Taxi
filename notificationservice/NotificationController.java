package notificationservice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;

public class NotificationController {
    private static final ConcurrentHashMap<Long, NotificationTask> tasks = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public NotificationTask createTask(Long tripId, String recipientType, String recipientId, String message) {
        long id = idGenerator.getAndIncrement();
        String now = LocalDateTime.now().toString();
        NotificationTask task = new NotificationTask(id, tripId, recipientType, recipientId,
                message, NotificationStatus.PENDING.name(), 0, now, now);
        tasks.put(id, task);

        System.out.println("Notification task created: " + id + " for trip " + tripId);
        return task;
    }

    public NotificationTask getPendingTask() {
        for (var entry : tasks.entrySet()) {
            NotificationTask task = entry.getValue();
            if (NotificationStatus.PENDING.name().equals(task.getStatus()) && task.getRetryCount() < 3) {
                synchronized (task) {
                    if (NotificationStatus.PENDING.name().equals(task.getStatus())) {
                        task.setStatus(NotificationStatus.PROCESSING.name());
                        task.setUpdatedAt(LocalDateTime.now().toString());
                        return task;
                    }
                }
            }
        }
        return null;
    }

    public void markAsSent(Long taskId) {
        NotificationTask task = tasks.get(taskId);
        if (task != null) {
            synchronized (task) {
                task.setStatus(NotificationStatus.SENT.name());
                task.setUpdatedAt(LocalDateTime.now().toString());
            }
        }
    }

    public void markAsFailed(Long taskId) {
        NotificationTask task = tasks.get(taskId);
        if (task != null) {
            synchronized (task) {
                task.setRetryCount(task.getRetryCount() + 1);
                if (task.getRetryCount() >= 3) {
                    task.setStatus(NotificationStatus.FAILED.name());
                } else {
                    task.setStatus(NotificationStatus.PENDING.name());
                }
                task.setUpdatedAt(LocalDateTime.now().toString());
            }
        }
    }

    public ConcurrentHashMap<Long, NotificationTask> getTasksByTrip(Long tripId) {
        ConcurrentHashMap<Long, NotificationTask> result = new ConcurrentHashMap<>();
        for (var entry : tasks.entrySet()) {
            if (entry.getValue().getTripId().equals(tripId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static ConcurrentHashMap<Long, NotificationTask> getTasks() {
        return tasks;
    }
}