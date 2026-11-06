package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.common.SlotUnavailableException;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.BookingSeries;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.BookingRepository;
import com.rupeek.maidbooking.repository.BookingSeriesRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Expands the recurrence rule into one Booking per occurrence. All occurrences
 * are validated first, then all are saved — a series is created atomically or
 * not at all. Every occurrence reserves its slot immediately (even unpaid).
 */
@Component
public class RecurringBookingStrategy implements BookingStrategy {

    private final AvailabilityService availabilityService;
    private final BookingRepository bookingRepository;
    private final BookingSeriesRepository seriesRepository;

    public RecurringBookingStrategy(AvailabilityService availabilityService,
                                    BookingRepository bookingRepository,
                                    BookingSeriesRepository seriesRepository) {
        this.availabilityService = availabilityService;
        this.bookingRepository = bookingRepository;
        this.seriesRepository = seriesRepository;
    }

    @Override
    public BookingType getType() {
        return BookingType.RECURRING;
    }

    @Override
    public List<Booking> book(BookingRequest request, Maid maid) {
        if (request.getRecurrence() == null) {
            throw new IllegalArgumentException("recurring booking needs a recurrence pattern");
        }
        List<TimeSlot> occurrences = request.getRecurrence().expand();
        if (occurrences.isEmpty()) {
            throw new IllegalArgumentException("recurrence pattern produces no occurrences");
        }
        for (TimeSlot slot : occurrences) {
            if (!availabilityService.isAvailable(maid, slot)) {
                throw new SlotUnavailableException("maid " + maid.getId()
                        + " is not free for occurrence " + slot + "; series not created");
            }
        }

        String seriesId = UUID.randomUUID().toString();
        seriesRepository.save(new BookingSeries(seriesId, request.getCustomerId(),
                maid.getId(), request.getServiceType(), request.getRecurrence()));

        BigDecimal amount = maid.getPrices().get(request.getServiceType());
        List<Booking> bookings = occurrences.stream()
                .map(slot -> new Booking(UUID.randomUUID().toString(), request.getCustomerId(),
                        maid.getId(), request.getServiceType(), slot, BookingType.RECURRING,
                        amount, seriesId))
                .toList();
        bookings.forEach(bookingRepository::save);
        return bookings;
    }
}
