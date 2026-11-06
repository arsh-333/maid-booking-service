package com.rupeek.maidbooking.domain.model;

import com.rupeek.maidbooking.domain.enums.SeriesStatus;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.RecurrencePattern;

/** A recurring booking rule; each occurrence is materialised as a Booking. */
public class BookingSeries {

    private final String id;
    private final String customerId;
    private final String maidId;
    private final ServiceType serviceType;
    private final RecurrencePattern pattern;
    private SeriesStatus status;

    public BookingSeries(String id, String customerId, String maidId,
                         ServiceType serviceType, RecurrencePattern pattern) {
        this.id = id;
        this.customerId = customerId;
        this.maidId = maidId;
        this.serviceType = serviceType;
        this.pattern = pattern;
        this.status = SeriesStatus.ACTIVE;
    }

    public void cancel() {
        this.status = SeriesStatus.CANCELLED;
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

    public RecurrencePattern getPattern() {
        return pattern;
    }

    public SeriesStatus getStatus() {
        return status;
    }
}
