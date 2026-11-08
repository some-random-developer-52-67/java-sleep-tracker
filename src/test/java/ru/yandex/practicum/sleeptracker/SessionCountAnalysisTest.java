package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionCountAnalysisTest {

    private final SessionCountAnalysis analysis = new SessionCountAnalysis();

    @Test
    void countsDaytimeAndNighttimeSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00"),
                session("2025-10-02T23:30", "2025-10-03T07:00")
        );


        SleepAnalysisResult<Integer> result = analysis.apply(sessions);


        assertEquals(3, result.value());
        assertEquals("Количество сессий сна", result.description());
    }

    @Test
    void countsSingleSession() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:00", "2025-10-02T08:00"));


        SleepAnalysisResult<Integer> result = analysis.apply(sessions);


        assertEquals(1, result.value());
    }

    @Test
    void returnsZeroForEmptyList() {
        SleepAnalysisResult<Integer> result = analysis.apply(List.of());


        assertEquals(0, result.value());
    }

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }
}
