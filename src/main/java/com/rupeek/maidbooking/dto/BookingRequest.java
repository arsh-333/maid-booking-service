package com.rupeek.maidbooking.dto;

import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.RecurrencePattern;
import com.rupeek.maidbooking.domain.vo.TimeSlot;

/**
 * slot          -> SCHEDULED
 * durationMins  -> INSTANT (slot computed from now)
 * recurrence    -> RECURRING
 */
public class BookingRequest {

    private final BookingType type;
    private final String customerId;
    private final String maidId;
    private final ServiceType serviceType;
    private final TimeSlot slot;
    private final Integer durationMins;
    private final RecurrencePattern recurrence;

    public BookingRequest(BookingType type, String customerId, String maidId,
                          ServiceType serviceType, TimeSlot slot, Integer durationMins,
                          RecurrencePattern recurrence) {
        this.type = type;
        this.customerId = customerId;
        this.maidId = maidId;
        this.serviceType = serviceType;
        this.slot = slot;
        this.durationMins = durationMins;
        this.recurrence = recurrence;
    }

    public BookingType getType() {
        return type;
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

    public Integer getDurationMins() {
        return durationMins;
    }

    public RecurrencePattern getRecurrence() {
        return recurrence;
    }
}
