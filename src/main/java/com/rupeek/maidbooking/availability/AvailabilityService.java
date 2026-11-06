package com.rupeek.maidbooking.availability;

import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

/**
 * Single source of truth for free/busy reasoning:
 * free(slot) = slot fits inside an availability window AND no active booking overlaps it.
 * Used by both search (AvailabilityFilter) and booking-time validation (BookingService),
 * so search results and the write path can never disagree.
 */
@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;

    public AvailabilityService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean isAvailable(Maid maid, TimeSlot slot) {
        boolean insideWindow = maid.getAvailability().stream().anyMatch(w -> w.covers(slot));
        if (!insideWindow) {
            return false;
        }
        return bookingRepository.findActiveByMaidId(maid.getId()).stream()
                .noneMatch(b -> b.getSlot().overlaps(slot));
    }
}
