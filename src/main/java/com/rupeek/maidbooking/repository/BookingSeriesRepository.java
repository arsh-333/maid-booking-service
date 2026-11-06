package com.rupeek.maidbooking.repository;

import com.rupeek.maidbooking.domain.model.BookingSeries;

import java.util.Optional;

public interface BookingSeriesRepository {

    BookingSeries save(BookingSeries series);

    Optional<BookingSeries> findById(String id);
}
