package com.rupeek.maidbooking.api;

import com.rupeek.maidbooking.dto.CreateBookingRequest;
import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.booking.BookingService;
import com.rupeek.maidbooking.dto.CancelResult;
import com.rupeek.maidbooking.cancellation.CancellationService;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.vo.RecurrencePattern;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
public class BookingController {

    private final BookingService bookingService;
    private final CancellationService cancellationService;

    public BookingController(BookingService bookingService, CancellationService cancellationService) {
        this.bookingService = bookingService;
        this.cancellationService = cancellationService;
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Booking> book(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.book(toBookingRequest(request));
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public CancelResult cancelBooking(@PathVariable String bookingId) {
        return cancellationService.cancelBooking(bookingId);
    }

    @PostMapping("/series/{seriesId}/cancel")
    public List<CancelResult> cancelSeries(@PathVariable String seriesId) {
        return cancellationService.cancelSeries(seriesId);
    }

    private BookingRequest toBookingRequest(CreateBookingRequest r) {
        TimeSlot slot = null;
        if (r.getDate() != null && r.getStart() != null && r.getEnd() != null) {
            slot = new TimeSlot(r.getDate(), r.getStart(), r.getEnd());
        }
        RecurrencePattern recurrence = null;
        if (r.getRecurrence() != null) {
            CreateBookingRequest.RecurrenceDto rec = r.getRecurrence();
            recurrence = new RecurrencePattern(rec.getDays(), rec.getStart(),
                    Duration.ofMinutes(rec.getDurationMins()), rec.getStartDate(), rec.getEndDate());
        }
        return new BookingRequest(r.getType(), r.getCustomerId(), r.getMaidId(), r.getServiceType(),
                slot, r.getDurationMins(), recurrence);
    }
}
