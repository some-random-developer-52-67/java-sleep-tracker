package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

record SleepNight(
        LocalDate date,
        List<SleepingSession> sessions
) {

    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    SleepNight {
        sessions = List.copyOf(sessions);
    }

    static Stream<SleepNight> from(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return Stream.empty();
        }

        LocalDateTime firstStart = sessions.getFirst().start();
        LocalDateTime lastEnd = sessions.getLast().end();
        LocalDate firstNight = firstStart.toLocalTime().isBefore(LocalTime.NOON)
                ? firstStart.toLocalDate()
                : firstStart.toLocalDate().plusDays(1);

        return firstNight.datesUntil(lastEnd.toLocalDate().plusDays(1))
                .filter(date -> date.atStartOfDay().isBefore(lastEnd))
                .map(date -> fromDate(date, sessions));
    }

    private static SleepNight fromDate(LocalDate date, List<SleepingSession> sessions) {
        LocalDateTime nightStart = date.atStartOfDay();
        LocalDateTime nightEnd = date.atTime(NIGHT_END);

        List<SleepingSession> nightSessions = sessions.stream()
                .filter(session -> session.start().isBefore(nightEnd) && session.end().isAfter(nightStart))
                .toList();

        return new SleepNight(date, nightSessions);
    }
}
