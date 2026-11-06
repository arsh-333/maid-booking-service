package com.rupeek.maidbooking.availability;

import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.inmemory.InMemoryBookingRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvailabilityServiceTest {

    private static final LocalDate MON = LocalDate.of(2026, 11, 9);

    private final InMemoryBookingRepository bookings = new InMemoryBookingRepository();
    private final AvailabilityService availability = new AvailabilityService(bookings);

    private Maid maid() {
        return new Maid("m1", "Sunita", Gender.FEMALE, "Koramangala",
                Map.of(ServiceType.CLEANING, new BigDecimal("300")),
                List.of(new AvailabilityWindow(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))),
                Map.of());
    }

    private TimeSlot slot(String start, String end) {
        return new TimeSlot(MON, LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    void freeWhenInsideWindowAndNoBookings() {
        assertTrue(availability.isAvailable(maid(), slot("10:00", "12:00")));
    }

    @Test
    void notFreeOutsideWindow() {
        assertFalse(availability.isAvailable(maid(), slot("17:30", "19:00")));
        assertFalse(availability.isAvailable(maid(),
                new TimeSlot(MON.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0)))); // Tuesday
    }

    @Test
    void notFreeWhenActiveBookingOverlaps_andFreeAgainAfterCancel() {
        Maid maid = maid();
        Booking booking = new Booking("b1", "c1", maid.getId(), ServiceType.CLEANING,
                slot("10:00", "12:00"), BookingType.SCHEDULED, new BigDecimal("300"), null);
        bookings.save(booking);

        assertFalse(availability.isAvailable(maid, slot("11:00", "13:00")));
        assertTrue(availability.isAvailable(maid, slot("12:00", "13:00"))); // back-to-back is fine

        booking.transitionTo(BookingStatus.CANCELLED);
        bookings.save(booking);

        assertTrue(availability.isAvailable(maid, slot("11:00", "13:00")));
    }
}
