package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MaximumDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long minutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .max()
                .orElseThrow();

        return new SleepAnalysisResult<>(
                "Максимальная продолжительность сессии сна (мин)",
                minutes
        );
    }
}
