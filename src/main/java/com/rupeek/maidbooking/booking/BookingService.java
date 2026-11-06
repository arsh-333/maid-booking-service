package com.rupeek.maidbooking.booking;

import com.rupeek.maidbooking.dto.BookingRequest;
import com.rupeek.maidbooking.common.NotFoundException;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.model.Booking;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.repository.MaidRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Booking entry point. Resolves the strategy by type and serialises bookings
 * per maid with a striped lock, so the availability check + write inside the
 * strategy is atomic per maid (double-booking prevention, requirement 3.3).
 */
@Service
public class BookingService {

    private final MaidRepository maidRepository;
    private final Map<BookingType, BookingStrategy> strategies;
    private final ConcurrentHashMap<String, ReentrantLock> maidLocks = new ConcurrentHashMap<>();

    public BookingService(MaidRepository maidRepository, List<BookingStrategy> strategies) {
        this.maidRepository = maidRepository;
        this.strategies = new EnumMap<>(BookingType.class);
        strategies.forEach(s -> this.strategies.put(s.getType(), s));
    }

    public List<Booking> book(BookingRequest request) {
        Maid maid = maidRepository.findById(request.getMaidId())
                .orElseThrow(() -> new NotFoundException("maid not found: " + request.getMaidId()));
        if (!maid.offers(request.getServiceType())) {
            throw new IllegalArgumentException(
                    "maid " + maid.getId() + " does not offer " + request.getServiceType());
        }
        BookingStrategy strategy = strategies.get(request.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("unsupported booking type: " + request.getType());
        }

        ReentrantLock lock = maidLocks.computeIfAbsent(maid.getId(), k -> new ReentrantLock());
        lock.lock();
        try {
            return strategy.book(request, maid);
        } finally {
            lock.unlock();
        }
    }
}
