package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.domain.enums.PaymentMode;

import java.math.BigDecimal;

/** The third-party gateway, hidden behind our own abstraction (mocked here). */
public interface PaymentGateway {

    /** Returns the gateway transaction reference. */
    String charge(BigDecimal amount, PaymentMode mode, String idempotencyKey);

    void refund(String gatewayRef);
}
