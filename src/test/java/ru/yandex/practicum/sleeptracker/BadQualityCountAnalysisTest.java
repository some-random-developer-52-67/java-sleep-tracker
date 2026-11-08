package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadQualityCountAnalysisTest {

    private final BadQualityCountAnalysis analysis = new BadQualityCountAnalysis();

    @Test
    void countsOnlyBadSessions() {
        List<SleepingSession> sessions = List.of(
                session(1, SleepQuality.BAD),
                session(2, SleepQuality.GOOD),
                session(3, SleepQuality.NORMAL),
                session(4, SleepQuality.BAD)
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void returnsZeroWhenNoBadSessions() {
        List<SleepingSession> sessions = List.of(
                session(1, SleepQuality.GOOD),
                session(2, SleepQuality.NORMAL)
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void countsAllSessionsWhenEveryQualityIsBad() {
        List<SleepingSession> sessions = List.of(
                session(1, SleepQuality.BAD),
                session(2, SleepQuality.BAD)
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void returnsZeroForEmptyList() {
        SleepAnalysisResult<Long> result = analysis.apply(List.of());


        assertEquals(0L, result.value());
    }

    private SleepingSession session(int day, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.of(2025, 10, day, 14, 0),
                LocalDateTime.of(2025, 10, day, 15, 0),
                quality
        );
    }
}
