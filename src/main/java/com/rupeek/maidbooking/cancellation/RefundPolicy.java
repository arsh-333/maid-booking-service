package com.rupeek.maidbooking.cancellation;

import com.rupeek.maidbooking.domain.model.Booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pluggable refund rule, deliberately orthogonal to booking type:
 * refund depends on time-to-slot and amount paid, not on how the booking was made.
 */
public interface RefundPolicy {

    BigDecimal refundAmount(Booking booking, LocalDateTime cancelledAt);
}
