package com.rupeek.maidbooking.api;

import com.rupeek.maidbooking.dto.CreatePaymentRequest;
import com.rupeek.maidbooking.domain.model.Payment;
import com.rupeek.maidbooking.payment.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment pay(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.pay(request.getBookingId(), request.getMode(), request.getIdempotencyKey());
    }
}
