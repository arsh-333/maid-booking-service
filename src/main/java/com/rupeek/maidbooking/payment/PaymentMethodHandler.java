package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.domain.enums.PaymentMode;

import java.math.BigDecimal;

/**
 * Extension point for payment methods. New method = new @Component;
 * PaymentService's registry picks it up automatically.
 */
public interface PaymentMethodHandler {

    PaymentMode getMode();

    /** Mode-specific handling; delegates to the gateway. Returns gateway ref. */
    String charge(BigDecimal amount, String idempotencyKey);
}
