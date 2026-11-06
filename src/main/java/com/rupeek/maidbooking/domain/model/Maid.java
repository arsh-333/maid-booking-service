package com.rupeek.maidbooking.domain.model;

import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Maid profile. Busy slots are deliberately NOT stored here — they are derived
 * from active Bookings (single source of truth in BookingRepository).
 */
public class Maid {

    private final String id;
    private final String name;
    private final Gender gender;
    private final String locality;
    private final Map<ServiceType, BigDecimal> prices;          // price per session
    private final List<AvailabilityWindow> availability;        // weekly working windows
    private final Map<ServiceType, Double> ratings;             // provided at onboarding, static

    public Maid(String id, String name, Gender gender, String locality,
                Map<ServiceType, BigDecimal> prices,
                List<AvailabilityWindow> availability,
                Map<ServiceType, Double> ratings) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.locality = locality;
        this.prices = prices;
        this.availability = availability;
        this.ratings = ratings;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public String getLocality() {
        return locality;
    }

    public Map<ServiceType, BigDecimal> getPrices() {
        return prices;
    }

    public List<AvailabilityWindow> getAvailability() {
        return availability;
    }

    public Map<ServiceType, Double> getRatings() {
        return ratings;
    }

    public boolean offers(ServiceType service) {
        return prices.containsKey(service);
    }
}
