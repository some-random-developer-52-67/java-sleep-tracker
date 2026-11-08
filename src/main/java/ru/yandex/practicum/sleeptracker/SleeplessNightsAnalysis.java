package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {

    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        LocalDateTime firstStart = sessions.getFirst().start();
        LocalDateTime lastEnd = sessions.getLast().end();
        LocalDate firstNight = firstStart.toLocalTime().isBefore(LocalTime.NOON)
                ? firstStart.toLocalDate()
                : firstStart.toLocalDate().plusDays(1);

        long count = firstNight.datesUntil(lastEnd.toLocalDate().plusDays(1))
                .filter(date -> date.atStartOfDay().isBefore(lastEnd))
                .filter(date -> isSleepless(date, sessions))
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", count);
    }

    private boolean isSleepless(LocalDate date, List<SleepingSession> sessions) {
        LocalDateTime nightStart = date.atStartOfDay();
        LocalDateTime nightEnd = date.atTime(NIGHT_END);

        return sessions.stream()
                .noneMatch(session -> session.start().isBefore(nightEnd) && session.end().isAfter(nightStart));
    }
}
