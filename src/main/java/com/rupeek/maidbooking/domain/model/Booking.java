package com.rupeek.maidbooking.domain.model;

import com.rupeek.maidbooking.domain.enums.BookingStatus;
import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.TimeSlot;

import java.math.BigDecimal;

/**
 * One booked slot. For recurring bookings each occurrence is its own Booking,
 * linked to the parent series via seriesId.
 */
public class Booking {

    private final String id;
    private final String customerId;
    private final String maidId;
    private final ServiceType serviceType;
    private final TimeSlot slot;
    private final BookingType type;
    private BookingStatus status;
    private final BigDecimal amount;
    private final String seriesId; // null unless part of a recurring series

    public Booking(String id, String customerId, String maidId, ServiceType serviceType,
                   TimeSlot slot, BookingType type, BigDecimal amount, String seriesId) {
        this.id = id;
        this.customerId = customerId;
        this.maidId = maidId;
        this.serviceType = serviceType;
        this.slot = slot;
        this.type = type;
        this.amount = amount;
        this.seriesId = seriesId;
        this.status = BookingStatus.CONFIRMED;
    }

    public void transitionTo(BookingStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException("cannot move booking from " + status + " to " + next);
        }
        this.status = next;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getMaidId() {
        return maidId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public BookingType getType() {
        return type;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getSeriesId() {
        return seriesId;
    }
}
