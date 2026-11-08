package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleeplessNightsAnalysisTest {

    private final SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

    @Test
    void returnsZeroForEmptyList() {
        List<SleepingSession> sessions = List.of();


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void countsNightsWithoutSleepBetweenSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-04T23:00", "2025-10-05T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void recognizesDifferentKindsOfNighttimeSleep() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-10-02T19:00", "2025-10-03T05:00"),
                session("2025-10-04T01:00", "2025-10-04T10:00"),
                session("2025-10-05T02:00", "2025-10-05T05:00"),
                session("2025-10-05T23:00", "2025-10-06T03:00"),
                session("2025-10-07T02:00", "2025-10-07T07:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void doesNotTreatDaytimeAndEveningSleepAsNighttimeSleep() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T07:00", "2025-10-01T11:00"),
                session("2025-10-01T17:00", "2025-10-01T23:00"),
                session("2025-10-02T07:00", "2025-10-02T11:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void includesFirstNightWhenObservationStartsBeforeNoon() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T11:59", "2025-10-01T12:30"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(1L, result.value());
    }

    @Test
    void startsWithFollowingNightWhenObservationStartsAfterNoon() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T12:01", "2025-10-01T13:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void startsWithFollowingNightWhenObservationStartsAtNoon() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T12:00", "2025-10-01T13:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void countsSplitSleepAsOneCoveredNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:00", "2025-10-02T02:00"),
                session("2025-10-02T03:00", "2025-10-02T07:00"),
                session("2025-10-03T07:00", "2025-10-03T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(1L, result.value());
    }

    @Test
    void recognizesAllNightsCoveredByLongSession() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:00", "2025-10-04T08:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void doesNotCoverNightWhenSleepEndsAtMidnight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T00:00"),
                session("2025-10-02T07:00", "2025-10-02T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(1L, result.value());
    }

    @Test
    void doesNotCoverNightWhenSleepStartsAtSix() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T06:00", "2025-10-01T08:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(1L, result.value());
    }

    @Test
    void coversNightWhenSleepRunsFromMidnightToSix() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T00:00", "2025-10-01T06:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void excludesNightStartingAtEndOfObservation() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T22:00", "2025-10-02T00:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void includesLastPartiallyObservedNight() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T22:00", "2025-10-02T02:00"));


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(0L, result.value());
    }

    @Test
    void countsNightsAcrossMonthBoundary() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-30T23:00", "2025-10-31T08:00"),
                session("2025-11-02T07:00", "2025-11-02T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void countsNightsAcrossYearBoundary() {
        List<SleepingSession> sessions = List.of(
                session("2025-12-30T23:00", "2025-12-31T08:00"),
                session("2026-01-02T07:00", "2026-01-02T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    @Test
    void countsWholeMonthAsWellAsRemainingDays() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T08:00"),
                session("2025-11-03T07:00", "2025-11-03T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(32L, result.value());
    }

    @Test
    void countsNightsAcrossLeapDay() {
        List<SleepingSession> sessions = List.of(
                session("2024-02-28T23:00", "2024-02-29T08:00"),
                session("2024-03-02T07:00", "2024-03-02T08:00")
        );


        SleepAnalysisResult<Long> result = analysis.apply(sessions);


        assertEquals(2L, result.value());
    }

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }
}
