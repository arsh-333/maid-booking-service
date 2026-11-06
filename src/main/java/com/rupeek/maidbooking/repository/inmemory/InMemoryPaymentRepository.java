package com.rupeek.maidbooking.repository.inmemory;

import com.rupeek.maidbooking.domain.enums.PaymentStatus;
import com.rupeek.maidbooking.domain.model.Payment;
import com.rupeek.maidbooking.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> store = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        store.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return store.values().stream()
                .filter(p -> p.getIdempotencyKey().equals(idempotencyKey))
                .findFirst();
    }

    @Override
    public Optional<Payment> findSuccessfulByBookingId(String bookingId) {
        return store.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .findFirst();
    }
}
