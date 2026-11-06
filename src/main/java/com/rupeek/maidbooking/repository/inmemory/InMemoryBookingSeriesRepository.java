package com.rupeek.maidbooking.repository.inmemory;

import com.rupeek.maidbooking.domain.model.BookingSeries;
import com.rupeek.maidbooking.repository.BookingSeriesRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingSeriesRepository implements BookingSeriesRepository {

    private final Map<String, BookingSeries> store = new ConcurrentHashMap<>();

    @Override
    public BookingSeries save(BookingSeries series) {
        store.put(series.getId(), series);
        return series;
    }

    @Override
    public Optional<BookingSeries> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
