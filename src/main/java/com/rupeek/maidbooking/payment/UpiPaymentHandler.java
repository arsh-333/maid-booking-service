package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.domain.enums.PaymentMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UpiPaymentHandler implements PaymentMethodHandler {

    private final PaymentGateway gateway;

    public UpiPaymentHandler(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public PaymentMode getMode() {
        return PaymentMode.UPI;
    }

    @Override
    public String charge(BigDecimal amount, String idempotencyKey) {
        return gateway.charge(amount, getMode(), idempotencyKey);
    }
}
