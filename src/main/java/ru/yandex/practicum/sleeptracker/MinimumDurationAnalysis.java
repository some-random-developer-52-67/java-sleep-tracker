package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MinimumDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long minutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .min()
                .orElseThrow();

        return new SleepAnalysisResult<>(
                "Минимальная продолжительность сессии сна (мин)",
                minutes
        );
    }
}
