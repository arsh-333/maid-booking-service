package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.common.SlotUnavailableException;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ScheduledBookingStrategy implements BookingStrategy {

    private final AvailabilityService availabilityService;
    private final BookingRepository bookingRepository;

    public ScheduledBookingStrategy(AvailabilityService availabilityService,
                                    BookingRepository bookingRepository) {
        this.availabilityService = availabilityService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingType getType() {
        return BookingType.SCHEDULED;
    }

    @Override
    public List<Booking> book(BookingRequest request, Maid maid) {
        if (request.getSlot() == null) {
            throw new IllegalArgumentException("scheduled booking needs a slot");
        }
        if (!request.getSlot().startDateTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("scheduled slot must be in the future");
        }
        if (!availabilityService.isAvailable(maid, request.getSlot())) {
            throw new SlotUnavailableException(
                    "maid " + maid.getId() + " is not free for the requested slot");
        }
        Booking booking = new Booking(UUID.randomUUID().toString(), request.getCustomerId(),
                maid.getId(), request.getServiceType(), request.getSlot(), BookingType.SCHEDULED,
                maid.getPrices().get(request.getServiceType()), null);
        bookingRepository.save(booking);
        return List.of(booking);
    }
}
