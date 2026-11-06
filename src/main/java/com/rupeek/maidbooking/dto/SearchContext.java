package com.rupeek.maidbooking.dto;

import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.TimeSlot;

import java.math.BigDecimal;

/** All search criteria; any field may be null meaning "not requested". */
public class SearchContext {

    private final String locality;
    private final ServiceType serviceType;
    private final Gender gender;
    private final BigDecimal maxPrice;
    private final Double minRating;
    private final TimeSlot slot;

    public SearchContext(String locality, ServiceType serviceType, Gender gender,
                         BigDecimal maxPrice, Double minRating, TimeSlot slot) {
        this.locality = locality;
        this.serviceType = serviceType;
        this.gender = gender;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
        this.slot = slot;
    }

    public String getLocality() {
        return locality;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Gender getGender() {
        return gender;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public Double getMinRating() {
        return minRating;
    }

    public TimeSlot getSlot() {
        return slot;
    }
}
