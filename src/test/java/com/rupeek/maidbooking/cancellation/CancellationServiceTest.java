package com.rupeek.maidbooking.cancellation;

import com.rupeek.maidbooking.dto.CancelResult;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.PaymentMode;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.BookingSeries;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.payment.MockPaymentGateway;
import com.rupeek.maidbooking.payment.PaymentGateway;
import com.rupeek.maidbooking.payment.PaymentService;
import com.rupeek.maidbooking.payment.UpiPaymentHandler;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingRepository;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingSeriesRepository;
import com.rupeek.maidbooking.repository.inmemory.InMemoryPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancellationServiceTest {

    private InMemoryBookingRepository bookings;
    private CancellationService cancellationService;
    private PaymentService paymentService;
    private AvailabilityService availabilityService;
    private Maid maid;
    private LocalDate slotDate; // > 24h out, inside the maid's Monday window

    @BeforeEach
    void setUp() {
        bookings = new InMemoryBookingRepository();
        InMemoryBookingSeriesRepository series = new InMemoryBookingSeriesRepository();
        InMemoryPaymentRepository payments = new InMemoryPaymentRepository();
        PaymentGateway gateway = new MockPaymentGateway();
        paymentService = new PaymentService(bookings, payments, gateway,
                List.of(new UpiPaymentHandler(gateway)));
        cancellationService = new CancellationService(bookings, series, paymentService,
                new StandardRefundPolicy());
        availabilityService = new AvailabilityService(bookings);

        maid = new Maid("m1", "Sunita", Gender.FEMALE, "Koramangala",
                Map.of(ServiceType.CLEANING, new BigDecimal("300")),
                List.of(new AvailabilityWindow(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))),
                Map.of());
        slotDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(1);
    }

    private TimeSlot slot() {
        return new TimeSlot(slotDate, LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    @Test
    void cancelSingleBookingRefundsFullyAndFreesSlot() {
        bookings.save(new Booking("b1", "c1", maid.getId(), ServiceType.CLEANING, slot(),
                BookingType.SCHEDULED, new BigDecimal("300"), null));
        paymentService.pay("b1", PaymentMode.UPI, "key-1");

        CancelResult result = cancellationService.cancelBooking("b1");

        assertEquals(new BigDecimal("300"), result.getRefundedAmount());
        assertEquals(BookingStatus.CANCELLED, bookings.findById("b1").orElseThrow().getStatus());
        assertTrue(availabilityService.isAvailable(maid, slot()));
    }

    @Test
    void cancelSeriesCancelsFutureOccurrencesOnly() {
        InMemoryBookingSeriesRepository seriesRepo = new InMemoryBookingSeriesRepository();
        CancellationService service = new CancellationService(bookings, seriesRepo,
                paymentService, new StandardRefundPolicy());
        seriesRepo.save(new BookingSeries("s1", "c1", maid.getId(), ServiceType.CLEANING, null));

        // past occurrence (last Monday), future occurrence (slotDate)
        LocalDate pastMon = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        bookings.save(new Booking("b-past", "c1", maid.getId(), ServiceType.CLEANING,
                new TimeSlot(pastMon, LocalTime.of(10, 0), LocalTime.of(12, 0)),
                BookingType.RECURRING, new BigDecimal("300"), "s1"));
        bookings.save(new Booking("b-future", "c1", maid.getId(), ServiceType.CLEANING, slot(),
                BookingType.RECURRING, new BigDecimal("300"), "s1"));

        List<CancelResult> results = service.cancelSeries("s1");

        assertEquals(1, results.size());
        assertEquals("b-future", results.get(0).getBookingId());
        assertEquals(BookingStatus.CONFIRMED, bookings.findById("b-past").orElseThrow().getStatus());
        assertEquals(BookingStatus.CANCELLED, bookings.findById("b-future").orElseThrow().getStatus());
    }
}
