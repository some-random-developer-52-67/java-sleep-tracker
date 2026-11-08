package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepingSessionTest {

    @Test
    void calculatesDaytimeDurationInMinutes() {
        SleepingSession session = session("2025-10-01T14:10", "2025-10-01T15:00");


        long duration = session.durationMinutes();


        assertEquals(50, duration);
    }

    @Test
    void calculatesDurationAcrossYearBoundary() {
        SleepingSession session = session("2025-12-31T23:15", "2026-01-01T07:30");


        long duration = session.durationMinutes();


        assertEquals(495, duration);
    }

    @Test
    void rejectsEndBeforeStart() {
        assertThrows(
                IllegalArgumentException.class,
                () -> session("2025-10-01T23:00", "2025-10-01T08:00")
        );
    }

    @Test
    void rejectsZeroDuration() {
        assertThrows(
                IllegalArgumentException.class,
                () -> session("2025-10-01T23:00", "2025-10-01T23:00")
        );
    }

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.GOOD
        );
    }
}
