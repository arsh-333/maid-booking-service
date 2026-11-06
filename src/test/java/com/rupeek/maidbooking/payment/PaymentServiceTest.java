package com.rupeek.maidbooking.payment;

import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.PaymentMode;
import com.rupeek.maidbooking.domain.enums.PaymentStatus;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Payment;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingRepository;
import com.rupeek.maidbooking.repository.inmemory.InMemoryPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        InMemoryBookingRepository bookings = new InMemoryBookingRepository();
        InMemoryPaymentRepository payments = new InMemoryPaymentRepository();
        PaymentGateway gateway = new MockPaymentGateway();
        paymentService = new PaymentService(bookings, payments, gateway,
                List.of(new CardPaymentHandler(gateway), new UpiPaymentHandler(gateway)));

        LocalDate future = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        bookings.save(new Booking("b1", "c1", "m1", ServiceType.CLEANING,
                new TimeSlot(future, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                BookingType.SCHEDULED, new BigDecimal("300"), null));
    }

    @Test
    void payIsIdempotentOnRetry() {
        Payment first = paymentService.pay("b1", PaymentMode.UPI, "key-1");
        Payment retry = paymentService.pay("b1", PaymentMode.UPI, "key-1");

        assertEquals(first.getId(), retry.getId());
        assertEquals(PaymentStatus.SUCCESS, retry.getStatus());
    }

    @Test
    void secondPaymentWithDifferentKeyIsRejected() {
        paymentService.pay("b1", PaymentMode.UPI, "key-1");
        assertThrows(IllegalStateException.class,
                () -> paymentService.pay("b1", PaymentMode.CARD, "key-2"));
    }
}
