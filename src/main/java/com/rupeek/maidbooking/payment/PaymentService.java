package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.common.NotFoundException;
import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.enums.PaymentMode;
import com.rupeek.maidbooking.domain.enums.PaymentStatus;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Payment;
import com.rupeek.maidbooking.repository.BookingRepository;
import com.rupeek.maidbooking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Payment is per booking (recurring => per occurrence). pay() is idempotent:
 * retrying with the same idempotency key returns the original payment.
 */
@Service
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway gateway;
    private final Map<PaymentMode, PaymentMethodHandler> handlers;

    public PaymentService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          PaymentGateway gateway,
                          List<PaymentMethodHandler> handlers) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.gateway = gateway;
        this.handlers = new EnumMap<>(PaymentMode.class);
        handlers.forEach(h -> this.handlers.put(h.getMode(), h));
    }

    public Payment pay(String bookingId, PaymentMode mode, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required");
        }
        var existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found: " + bookingId));
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("cannot pay for a " + booking.getStatus() + " booking");
        }
        if (paymentRepository.findSuccessfulByBookingId(bookingId).isPresent()) {
            throw new IllegalStateException("booking " + bookingId + " is already paid");
        }
        PaymentMethodHandler handler = handlers.get(mode);
        if (handler == null) {
            throw new IllegalArgumentException("unsupported payment mode: " + mode);
        }
        String ref = handler.charge(booking.getAmount(), idempotencyKey);
        Payment payment = new Payment(UUID.randomUUID().toString(), bookingId,
                booking.getAmount(), mode, idempotencyKey, ref, PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }

    /** Refunds the successful payment on a booking, if any. Returns refunded amount (0 if unpaid). */
    public BigDecimal refundBooking(String bookingId, BigDecimal amount) {
        var paymentOpt = paymentRepository.findSuccessfulByBookingId(bookingId);
        if (paymentOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Payment payment = paymentOpt.get();
        if (amount.signum() > 0) {
            gateway.refund(payment.getGatewayRef());
        }
        payment.markRefunded();
        paymentRepository.save(payment);
        return amount;
    }
}
