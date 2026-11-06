package com.rupeek.maidbooking.domain.model;

import com.rupeek.maidbooking.domain.enums.PaymentMode;
import com.rupeek.maidbooking.domain.enums.PaymentStatus;

import java.math.BigDecimal;

/** One payment per booking (recurring => one payment per occurrence). */
public class Payment {

    private final String id;
    private final String bookingId;
    private final BigDecimal amount;
    private final PaymentMode mode;
    private final String idempotencyKey;
    private final String gatewayRef;
    private PaymentStatus status;

    public Payment(String id, String bookingId, BigDecimal amount, PaymentMode mode,
                   String idempotencyKey, String gatewayRef, PaymentStatus status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.mode = mode;
        this.idempotencyKey = idempotencyKey;
        this.gatewayRef = gatewayRef;
        this.status = status;
    }

    public void markRefunded() {
        if (status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("only a successful payment can be refunded");
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public String getId() {
        return id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMode getMode() {
        return mode;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getGatewayRef() {
        return gatewayRef;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
