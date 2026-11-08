package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class SessionCountAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Integer>> {

    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult<>(
                "Количество сессий сна",
                sessions.size()
        );
    }
}
