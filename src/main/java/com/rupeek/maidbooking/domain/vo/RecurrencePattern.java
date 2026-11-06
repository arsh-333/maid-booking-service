package com.rupeek.maidbooking.domain.vo;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Rule describing a repeating booking, e.g. weekdays 9-11am from date X to Y. */
public class RecurrencePattern {

    private final Set<DayOfWeek> days;
    private final LocalTime start;
    private final Duration duration;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public RecurrencePattern(Set<DayOfWeek> days, LocalTime start, Duration duration,
                             LocalDate startDate, LocalDate endDate) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("at least one day is required");
        }
        if (start == null || duration == null || duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("start and a positive duration are required");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("valid startDate/endDate are required");
        }
        this.days = days;
        this.start = start;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Materialises every occurrence slot between startDate and endDate (inclusive). */
    public List<TimeSlot> expand() {
        List<TimeSlot> slots = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (days.contains(d.getDayOfWeek())) {
                slots.add(new TimeSlot(d, start, start.plus(duration)));
            }
        }
        return slots;
    }

    public Set<DayOfWeek> getDays() {
        return days;
    }

    public LocalTime getStart() {
        return start;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
