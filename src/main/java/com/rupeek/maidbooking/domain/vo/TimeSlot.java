package com.rupeek.maidbooking.domain.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/** A concrete bookable time range on a specific date. Value object. */
public class TimeSlot {

    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;

    public TimeSlot(LocalDate date, LocalTime start, LocalTime end) {
        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("date, start and end are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("slot end must be after start");
        }
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public boolean overlaps(TimeSlot other) {
        return date.equals(other.date)
                && start.isBefore(other.end)
                && other.start.isBefore(end);
    }

    public LocalDateTime startDateTime() {
        return LocalDateTime.of(date, start);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeSlot other)) {
            return false;
        }
        return date.equals(other.date) && start.equals(other.start) && end.equals(other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, start, end);
    }

    @Override
    public String toString() {
        return date + " " + start + "-" + end;
    }
}
