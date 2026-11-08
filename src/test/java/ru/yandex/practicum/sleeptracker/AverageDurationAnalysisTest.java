package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AverageDurationAnalysisTest {

    private final AverageDurationAnalysis analysis = new AverageDurationAnalysis();

    @Test
    void averagesDaytimeAndNighttimeSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00")
        );


        SleepAnalysisResult<Double> result = analysis.apply(sessions);


        assertEquals(300.0, result.value());
    }

    @Test
    void preservesFractionalMinutesWithoutRounding() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00"),
                session("2025-10-02T14:00", "2025-10-02T15:01"),
                session("2025-10-03T14:00", "2025-10-03T15:01")
        );


        SleepAnalysisResult<Double> result = analysis.apply(sessions);


        assertEquals(60.666666666666664, result.value(), 0.000000001);
    }

    @Test
    void returnsDurationOfSingleSession() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T14:00", "2025-10-01T14:45"));


        SleepAnalysisResult<Double> result = analysis.apply(sessions);


        assertEquals(45.0, result.value());
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
