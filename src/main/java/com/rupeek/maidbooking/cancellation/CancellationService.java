package com.rupeek.maidbooking.cancellation;

import com.rupeek.maidbooking.dto.CancelResult;
import com.rupeek.maidbooking.common.NotFoundException;
import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.BookingSeries;
import com.rupeek.maidbooking.payment.PaymentService;
import com.rupeek.maidbooking.repository.BookingRepository;
import com.rupeek.maidbooking.repository.BookingSeriesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Single cancellation path for all booking types. Cancelling marks the booking
 * CANCELLED; availability is derived from active bookings, so the slot becomes
 * discoverable/bookable again with nothing extra to "free".
 */
@Service
public class CancellationService {

    private final BookingRepository bookingRepository;
    private final BookingSeriesRepository seriesRepository;
    private final PaymentService paymentService;
    private final RefundPolicy refundPolicy;

    public CancellationService(BookingRepository bookingRepository,
                               BookingSeriesRepository seriesRepository,
                               PaymentService paymentService,
                               RefundPolicy refundPolicy) {
        this.bookingRepository = bookingRepository;
        this.seriesRepository = seriesRepository;
        this.paymentService = paymentService;
        this.refundPolicy = refundPolicy;
    }

    /** Cancels one booking (for recurring series: a single occurrence). */
    public CancelResult cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found: " + bookingId));
        BigDecimal refund = refundPolicy.refundAmount(booking, LocalDateTime.now());
        booking.transitionTo(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        BigDecimal refunded = paymentService.refundBooking(bookingId, refund);
        return new CancelResult(bookingId, refunded);
    }

    /** Cancels every future, still-confirmed occurrence of a recurring series. */
    public List<CancelResult> cancelSeries(String seriesId) {
        BookingSeries series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new NotFoundException("series not found: " + seriesId));
        LocalDateTime now = LocalDateTime.now();
        List<CancelResult> results = bookingRepository.findBySeriesId(seriesId).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> b.getSlot().startDateTime().isAfter(now))
                .map(b -> cancelBooking(b.getId()))
                .toList();
        series.cancel();
        seriesRepository.save(series);
        return results;
    }
}
