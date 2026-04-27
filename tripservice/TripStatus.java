package tripservice;

public enum TripStatus {
    PENDING,      // ожидает водителя
    DRIVER_ASSIGNED,  // водитель назначен
    IN_PROGRESS,  // поездка началась
    COMPLETED,    // завершена
    CANCELLED     // отменена
}