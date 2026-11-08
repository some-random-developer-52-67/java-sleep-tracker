package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepNightTest {

    @Test
    void groupsSplitSleepAndKeepsUncoveredNight() {
        SleepingSession firstPart = session("2025-10-01T21:00", "2025-10-02T02:00");
        SleepingSession secondPart = session("2025-10-02T03:00", "2025-10-02T07:00");
        SleepingSession daytimeSleep = session("2025-10-03T07:00", "2025-10-03T08:00");


        List<SleepNight> nights = SleepNight.from(List.of(firstPart, secondPart, daytimeSleep)).toList();


        assertEquals(2, nights.size());
        assertEquals(LocalDate.of(2025, 10, 2), nights.getFirst().date());
        assertEquals(List.of(firstPart, secondPart), nights.getFirst().sessions());
        assertEquals(LocalDate.of(2025, 10, 3), nights.getLast().date());
        assertEquals(List.of(), nights.getLast().sessions());
    }

    @Test
    void protectsSessionListFromExternalChanges() {
        SleepingSession session = session("2025-10-01T23:00", "2025-10-02T08:00");
        List<SleepingSession> sessions = new ArrayList<>(List.of(session));


        SleepNight night = new SleepNight(LocalDate.of(2025, 10, 2), sessions);
        sessions.clear();


        assertEquals(List.of(session), night.sessions());
        assertThrows(
                UnsupportedOperationException.class,
                () -> night.sessions().clear()
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
