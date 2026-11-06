package com.rupeek.maidbooking.domain.vo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotTest {

    private static final LocalDate MON = LocalDate.of(2026, 11, 9); // a Monday

    private TimeSlot slot(String start, String end) {
        return new TimeSlot(MON, LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    void overlappingSlotsOverlap() {
        assertTrue(slot("10:00", "12:00").overlaps(slot("11:00", "13:00")));
        assertTrue(slot("11:00", "13:00").overlaps(slot("10:00", "12:00")));
        assertTrue(slot("10:00", "12:00").overlaps(slot("10:00", "12:00")));
    }

    @Test
    void backToBackSlotsDoNotOverlap() {
        assertFalse(slot("10:00", "12:00").overlaps(slot("12:00", "14:00")));
    }

    @Test
    void differentDatesDoNotOverlap() {
        TimeSlot a = slot("10:00", "12:00");
        TimeSlot b = new TimeSlot(MON.plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertFalse(a.overlaps(b));
    }

    @Test
    void endMustBeAfterStart() {
        assertThrows(IllegalArgumentException.class, () -> slot("12:00", "10:00"));
    }
}
