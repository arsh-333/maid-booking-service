package com.rupeek.maidbooking.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;
import java.time.LocalTime;

/** A maid's recurring weekly working window, declared at onboarding. */
public class AvailabilityWindow {

    private final DayOfWeek day;
    private final LocalTime start;
    private final LocalTime end;

    @JsonCreator
    public AvailabilityWindow(@JsonProperty("day") DayOfWeek day,
                              @JsonProperty("start") LocalTime start,
                              @JsonProperty("end") LocalTime end) {
        if (day == null || start == null || end == null) {
            throw new IllegalArgumentException("day, start and end are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("window end must be after start");
        }
        this.day = day;
        this.start = start;
        this.end = end;
    }

    /** True if the whole slot fits inside this window. */
    public boolean covers(TimeSlot slot) {
        return slot.getDate().getDayOfWeek() == day
                && !slot.getStart().isBefore(start)
                && !slot.getEnd().isAfter(end);
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}
