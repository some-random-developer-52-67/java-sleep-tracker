package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChronotypeAnalysisTest {

    private final ChronotypeAnalysis analysis = new ChronotypeAnalysis();

    @Test
    void identifiesOwl() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:01", "2025-10-02T09:01"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void identifiesOwlWithBedtimeAfterMidnight() {
        List<SleepingSession> sessions = List.of(session("2025-10-02T01:00", "2025-10-02T10:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void identifiesLark() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T21:59", "2025-10-02T06:59"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void identifiesPigeonForIntermediateSchedule() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T22:30", "2025-10-02T08:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void doesNotClassifyBedtimeAtElevenAsOwl() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:00", "2025-10-02T10:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void doesNotClassifyWakingAtNineAsOwl() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:30", "2025-10-02T09:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void doesNotClassifyBedtimeAtTenAsLark() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T22:00", "2025-10-02T06:30"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void doesNotClassifyWakingAtSevenAsLark() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T21:30", "2025-10-02T07:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void requiresBothOwlConditions() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T23:30", "2025-10-02T08:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void requiresBothLarkConditions() {
        List<SleepingSession> sessions = List.of(session("2025-10-01T21:30", "2025-10-02T08:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void returnsPigeonForEmptyInput() {
        List<SleepingSession> sessions = List.of();


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void returnsPigeonForDaytimeSleepOnly() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T07:00", "2025-10-01T11:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void ignoresDaytimeAndEveningSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T14:00", "2025-10-01T15:00"),
                session("2025-10-01T17:00", "2025-10-01T21:00"),
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void ignoresSleeplessNights() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-11-03T14:00", "2025-11-03T15:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void choosesMostFrequentTypeWithoutRequiringAbsoluteMajority() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-10-02T21:00", "2025-10-03T06:00"),
                session("2025-10-03T22:30", "2025-10-04T08:00"),
                session("2025-10-04T23:30", "2025-10-05T10:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void choosesLarkWhenItIsMostFrequent() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:30", "2025-10-02T06:00"),
                session("2025-10-02T23:30", "2025-10-03T10:00"),
                session("2025-10-03T21:30", "2025-10-04T06:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void choosesPigeonWhenItIsMostFrequent() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T22:30", "2025-10-02T08:00"),
                session("2025-10-02T23:30", "2025-10-03T10:00"),
                session("2025-10-03T22:30", "2025-10-04T08:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void returnsPigeonWhenOwlAndLarkAreTied() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-10-02T21:30", "2025-10-03T06:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void returnsPigeonWhenAllTypesAreTied() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-10-02T21:30", "2025-10-03T06:00"),
                session("2025-10-03T22:30", "2025-10-04T08:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void usesEarliestBedtimeAndLatestWakeTimeForSplitNight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:00", "2025-10-02T02:00"),
                session("2025-10-02T03:00", "2025-10-02T10:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void classifiesSplitOwlNightOnce() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T01:00"),
                session("2025-10-02T02:00", "2025-10-02T10:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void countsNightsRatherThanIndividualSessions() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:00", "2025-10-02T06:00"),
                session("2025-10-02T21:00", "2025-10-03T06:00"),
                session("2025-10-03T23:30", "2025-10-04T01:00"),
                session("2025-10-04T02:00", "2025-10-04T03:00"),
                session("2025-10-04T04:00", "2025-10-04T10:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void comparesBedtimeWithPreviousDateAcrossNewYear() {
        List<SleepingSession> sessions = List.of(session("2025-12-31T23:30", "2026-01-01T10:00"));


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void ignoresSleepStartingAtSix() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T06:00", "2025-10-01T10:00"),
                session("2025-10-01T21:30", "2025-10-02T06:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void ignoresSleepEndingAtMidnight() {
        List<SleepingSession> sessions = List.of(
                session("2025-10-01T21:00", "2025-10-02T00:00"),
                session("2025-10-02T00:30", "2025-10-02T10:00")
        );


        SleepAnalysisResult<Chronotype> result = analysis.apply(sessions);


        assertEquals(Chronotype.OWL, result.value());
    }

    private SleepingSession session(String start, String end) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                SleepQuality.NORMAL
        );
    }
}
