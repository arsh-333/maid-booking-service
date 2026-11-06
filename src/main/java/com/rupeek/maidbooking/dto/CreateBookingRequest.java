package com.rupeek.maidbooking.dto;

import com.rupeek.maidbooking.domain.enums.BookingType;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * SCHEDULED: date + start + end.
 * INSTANT:   durationMins (slot computed from now).
 * RECURRING: recurrence.
 */
public class CreateBookingRequest {

    @NotNull
    private BookingType type;

    @NotBlank
    private String customerId;

    @NotBlank
    private String maidId;

    @NotNull
    private ServiceType serviceType;

    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private Integer durationMins;
    private RecurrenceDto recurrence;

    public CreateBookingRequest() {
    }

    public static class RecurrenceDto {

        private Set<DayOfWeek> days;
        private LocalTime start;
        private int durationMins;
        private LocalDate startDate;
        private LocalDate endDate;

        public RecurrenceDto() {
        }

        public Set<DayOfWeek> getDays() {
            return days;
        }

        public void setDays(Set<DayOfWeek> days) {
            this.days = days;
        }

        public LocalTime getStart() {
            return start;
        }

        public void setStart(LocalTime start) {
            this.start = start;
        }

        public int getDurationMins() {
            return durationMins;
        }

        public void setDurationMins(int durationMins) {
            this.durationMins = durationMins;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }

    public BookingType getType() {
        return type;
    }

    public void setType(BookingType type) {
        this.type = type;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMaidId() {
        return maidId;
    }

    public void setMaidId(String maidId) {
        this.maidId = maidId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public Integer getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(Integer durationMins) {
        this.durationMins = durationMins;
    }

    public RecurrenceDto getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceDto recurrence) {
        this.recurrence = recurrence;
    }
}
