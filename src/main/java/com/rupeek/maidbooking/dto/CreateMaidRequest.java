package com.rupeek.maidbooking.dto;

import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateMaidRequest {

    @NotBlank
    private String name;

    @NotNull
    private Gender gender;

    @NotBlank
    private String locality;

    @NotEmpty
    private Map<ServiceType, BigDecimal> prices;

    @NotEmpty
    private List<AvailabilityWindow> availability;

    private Map<ServiceType, Double> ratings;

    public CreateMaidRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Map<ServiceType, BigDecimal> getPrices() {
        return prices;
    }

    public void setPrices(Map<ServiceType, BigDecimal> prices) {
        this.prices = prices;
    }

    public List<AvailabilityWindow> getAvailability() {
        return availability;
    }

    public void setAvailability(List<AvailabilityWindow> availability) {
        this.availability = availability;
    }

    public Map<ServiceType, Double> getRatings() {
        return ratings;
    }

    public void setRatings(Map<ServiceType, Double> ratings) {
        this.ratings = ratings;
    }
}
