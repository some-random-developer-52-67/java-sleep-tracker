package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AverageDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Double>> {

    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        double minutes = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .average()
                .orElseThrow();

        return new SleepAnalysisResult<>(
                "Средняя продолжительность сессии сна (мин)",
                minutes
        );
    }
}
