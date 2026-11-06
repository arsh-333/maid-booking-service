package com.rupeek.maidbooking.repository.inmemory;

import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        store.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findActiveByMaidId(String maidId) {
        return store.values().stream()
                .filter(b -> b.getMaidId().equals(maidId))
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .toList();
    }

    @Override
    public List<Booking> findBySeriesId(String seriesId) {
        return store.values().stream()
                .filter(b -> seriesId.equals(b.getSeriesId()))
                .toList();
    }
}
