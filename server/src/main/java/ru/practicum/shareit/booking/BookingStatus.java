package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus from(String status) {
        if (status == null) {
            return null;
        }

        return switch (status.toLowerCase()) {
            case "waiting" -> WAITING;
            case "approved" -> APPROVED;
            case "rejected" -> REJECTED;
            case "canceled" -> CANCELED;
            default -> null;
        };
    }
}