package com.rupeek.maidbooking.domain.enums;

public enum BookingStatus {
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case CONFIRMED -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
