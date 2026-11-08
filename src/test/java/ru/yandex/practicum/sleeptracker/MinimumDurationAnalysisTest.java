package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MinimumDurationAnalysisTest {

    private final MinimumDurationAnalysis analysis = new MinimumDurationAnalysis();

    @Test
    void findsShortestSessionAmongDaytimeAndNighttimeSleep() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-02T14:00", "2025-10-02T14:30"),
                session("2025-10-02T16:00", "2025-10-02T17:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(30L, result.value());
    }

    @Test
    void returnsDurationOfSingleSession() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:00", "2025-10-02T07:15"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(495L, result.value());
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
