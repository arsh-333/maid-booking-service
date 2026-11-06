package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;

import java.util.List;

/**
 * Extension point for booking types. A new type = a new @Component implementing
 * this interface; BookingService's registry picks it up automatically.
 * Cancellation is intentionally NOT here — CancellationService is the single path.
 */
public interface BookingStrategy {

    BookingType getType();

    /** Called by BookingService while holding the per-maid lock.
     *  Returns created bookings (1 for instant/scheduled, N for recurring). */
    List<Booking> book(BookingRequest request, Maid maid);
}
