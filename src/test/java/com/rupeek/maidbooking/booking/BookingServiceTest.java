package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.common.SlotUnavailableException;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;
import com.rupeek.maidbooking.domain.vo.RecurrencePattern;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingRepository;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingSeriesRepository;
import com.rupeek.maidbooking.repository.inmemory.InMemoryMaidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingServiceTest {

    private BookingService bookingService;
    private Maid maid;
    private LocalDate nextMon;

    @BeforeEach
    void setUp() {
        InMemoryMaidRepository maids = new InMemoryMaidRepository();
        InMemoryBookingRepository bookings = new InMemoryBookingRepository();
        InMemoryBookingSeriesRepository series = new InMemoryBookingSeriesRepository();
        AvailabilityService availability = new AvailabilityService(bookings);
        bookingService = new BookingService(maids, List.of(
                new InstantBookingStrategy(availability, bookings),
                new ScheduledBookingStrategy(availability, bookings),
                new RecurringBookingStrategy(availability, bookings, series)));

        maid = maids.save(new Maid("m1", "Sunita", Gender.FEMALE, "Koramangala",
                Map.of(ServiceType.CLEANING, new BigDecimal("300")),
                List.of(new AvailabilityWindow(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                        new AvailabilityWindow(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))),
                Map.of()));
        nextMon = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }

    private BookingRequest scheduled(TimeSlot slot) {
        return new BookingRequest(BookingType.SCHEDULED, "c1", maid.getId(),
                ServiceType.CLEANING, slot, null, null);
    }

    @Test
    void overlappingScheduledBookingIsRejected() {
        TimeSlot slot = new TimeSlot(nextMon, LocalTime.of(10, 0), LocalTime.of(12, 0));
        bookingService.book(scheduled(slot));

        TimeSlot overlapping = new TimeSlot(nextMon, LocalTime.of(11, 0), LocalTime.of(13, 0));
        assertThrows(SlotUnavailableException.class, () -> bookingService.book(scheduled(overlapping)));
    }

    @Test
    void recurringBookingCreatesSeriesAndOneBookingPerOccurrence() {
        RecurrencePattern pattern = new RecurrencePattern(
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0), Duration.ofHours(1), nextMon, nextMon.plusDays(1));

        List<Booking> bookings = bookingService.book(new BookingRequest(BookingType.RECURRING,
                "c1", maid.getId(), ServiceType.CLEANING, null, null, pattern));

        assertEquals(2, bookings.size());
        String seriesId = bookings.get(0).getSeriesId();
        assertNotNull(seriesId);
        bookings.forEach(b -> assertEquals(seriesId, b.getSeriesId()));

        // occurrences reserve their slots immediately
        TimeSlot clash = new TimeSlot(nextMon, LocalTime.of(9, 30), LocalTime.of(10, 30));
        assertThrows(SlotUnavailableException.class, () -> bookingService.book(scheduled(clash)));
    }

    @Test
    void recurringSeriesIsAtomicWhenOneOccurrenceClashes() {
        TimeSlot taken = new TimeSlot(nextMon, LocalTime.of(9, 0), LocalTime.of(10, 0));
        bookingService.book(scheduled(taken));

        RecurrencePattern pattern = new RecurrencePattern(
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0), Duration.ofHours(1), nextMon, nextMon.plusDays(1));

        assertThrows(SlotUnavailableException.class, () -> bookingService.book(
                new BookingRequest(BookingType.RECURRING, "c1", maid.getId(),
                        ServiceType.CLEANING, null, null, pattern)));

        // Tuesday occurrence must NOT have been saved
        TimeSlot tuesday = new TimeSlot(nextMon.plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0));
        bookingService.book(scheduled(tuesday)); // would throw if the series had leaked a booking
    }
}
