package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaximumDurationAnalysisTest {

    private final MaximumDurationAnalysis analysis = new MaximumDurationAnalysis();

    @Test
    void findsLongestSessionAcrossMonthBoundary() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-31T14:00", "2025-10-31T14:30"),
                session("2025-10-31T23:00", "2025-11-01T08:00"),
                session("2025-11-01T14:00", "2025-11-01T15:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(540L, result.value());
    }

    @Test
    void returnsDurationOfSingleSession() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T14:00", "2025-10-01T14:45"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(45L, result.value());
    }

    @Test
    void rejectsEmptyList() {
        assertThrows(
                NoSuchElementException.class,
                () -> analysis.apply(List.of())
        );
    }

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }
}
