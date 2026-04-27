package notificationservice;

public class NotificationTask {
    private Long id;
    private Long tripId;
    private String recipientType;
    private String recipientId;
    private String message;
    private String status;
    private int retryCount;
    private String createdAt;
    private String updatedAt;

    public NotificationTask() {}

    public NotificationTask(Long id, Long tripId, String recipientType, String recipientId,
                            String message, String status, int retryCount, String createdAt, String updatedAt) {
        this.id = id;
        this.tripId = tripId;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
        this.status = status;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String recipientType) { this.recipientType = recipientType; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}