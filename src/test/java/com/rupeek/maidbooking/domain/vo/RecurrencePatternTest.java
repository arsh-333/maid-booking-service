package com.rupeek.maidbooking.domain.vo;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecurrencePatternTest {

    @Test
    void weekdaysOverOneWeekExpandToFiveSlots() {
        // Mon 2026-11-09 .. Sun 2026-11-15
        RecurrencePattern pattern = new RecurrencePattern(
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                LocalTime.of(9, 0), Duration.ofHours(2),
                LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 15));

        List<TimeSlot> slots = pattern.expand();

        assertEquals(5, slots.size());
        assertEquals(new TimeSlot(LocalDate.of(2026, 11, 9), LocalTime.of(9, 0), LocalTime.of(11, 0)),
                slots.get(0));
    }

    @Test
    void sundaysOnlySkipsOtherDays() {
        RecurrencePattern pattern = new RecurrencePattern(
                Set.of(DayOfWeek.SUNDAY), LocalTime.of(9, 0), Duration.ofHours(1),
                LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 22));

        assertEquals(2, pattern.expand().size()); // Nov 15 and Nov 22
    }
}
