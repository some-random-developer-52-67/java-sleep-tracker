package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long count = SleepNight.from(sessions)
                .filter(night -> night.sessions().isEmpty())
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", count);
    }
}
