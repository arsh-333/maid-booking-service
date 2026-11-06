package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.common.SlotUnavailableException;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class InstantBookingStrategy implements BookingStrategy {

    private final AvailabilityService availabilityService;
    private final BookingRepository bookingRepository;

    public InstantBookingStrategy(AvailabilityService availabilityService,
                                  BookingRepository bookingRepository) {
        this.availabilityService = availabilityService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingType getType() {
        return BookingType.INSTANT;
    }

    @Override
    public List<Booking> book(BookingRequest request, Maid maid) {
        if (request.getDurationMins() == null || request.getDurationMins() <= 0) {
            throw new IllegalArgumentException("instant booking needs a positive durationMins");
        }
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        TimeSlot slot = new TimeSlot(now.toLocalDate(), now.toLocalTime(),
                now.toLocalTime().plusMinutes(request.getDurationMins()));
        if (!availabilityService.isAvailable(maid, slot)) {
            throw new SlotUnavailableException("maid " + maid.getId() + " is not free right now");
        }
        Booking booking = new Booking(UUID.randomUUID().toString(), request.getCustomerId(),
                maid.getId(), request.getServiceType(), slot, BookingType.INSTANT,
                maid.getPrices().get(request.getServiceType()), null);
        bookingRepository.save(booking);
        return List.of(booking);
    }
}
