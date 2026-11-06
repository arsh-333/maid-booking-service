package com.rupeek.maidbooking.repository;

import com.rupeek.maidbooking.domain.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(String id);

    /** Active = not cancelled; these are the slots that block a maid. */
    List<Booking> findActiveByMaidId(String maidId);

    List<Booking> findBySeriesId(String seriesId);
}
