package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.domain.enums.PaymentMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Stands in for a real gateway; always succeeds. */
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public String charge(BigDecimal amount, PaymentMode mode, String idempotencyKey) {
        return "mock-txn-" + idempotencyKey;
    }

    @Override
    public void refund(String gatewayRef) {
        // no-op mock
    }
}
