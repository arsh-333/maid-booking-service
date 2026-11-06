package com.rupeek.maidbooking.cancellation;

import com.rupeek.maidbooking.domain.model.Booking;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/** Full refund >= 24h before slot start, 50% after that, nothing once the slot has started. */
@Component
public class StandardRefundPolicy implements RefundPolicy {

    private static final BigDecimal HALF = new BigDecimal("0.5");

    @Override
    public BigDecimal refundAmount(Booking booking, LocalDateTime cancelledAt) {
        if (!cancelledAt.isBefore(booking.getSlot().startDateTime())) {
            return BigDecimal.ZERO;
        }
        long hoursBefore = Duration.between(cancelledAt, booking.getSlot().startDateTime()).toHours();
        if (hoursBefore >= 24) {
            return booking.getAmount();
        }
        return booking.getAmount().multiply(HALF);
    }
}
