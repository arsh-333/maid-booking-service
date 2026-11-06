package com.rupeek.maidbooking.repository;

import com.rupeek.maidbooking.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String id);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    /** The successful payment for a booking, if it has been paid. */
    Optional<Payment> findSuccessfulByBookingId(String bookingId);
}
