package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<Chronotype>> {

    private static final LocalTime OWL_BEDTIME = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_TIME = LocalTime.of(9, 0);
    private static final LocalTime LARK_BEDTIME = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_TIME = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> nightCounts = SleepNight.from(sessions)
                .filter(night -> !night.sessions().isEmpty())
                .map(this::classify)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maximum = nightCounts.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        List<Chronotype> mostFrequent = nightCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maximum)
                .map(Map.Entry::getKey)
                .toList();

        Chronotype chronotype = mostFrequent.size() == 1
                ? mostFrequent.getFirst()
                : Chronotype.PIGEON;

        return new SleepAnalysisResult<>("Хронотип", chronotype);
    }

    private Chronotype classify(SleepNight night) {
        LocalDate previousDate = night.date().minusDays(1);
        LocalDateTime bedtime = night.sessions().getFirst().start();
        LocalDateTime wakeTime = night.sessions().getLast().end();

        if (bedtime.isAfter(previousDate.atTime(OWL_BEDTIME))
                && wakeTime.isAfter(night.date().atTime(OWL_WAKE_TIME))) {
            return Chronotype.OWL;
        }

        if (bedtime.isBefore(previousDate.atTime(LARK_BEDTIME))
                && wakeTime.isBefore(night.date().atTime(LARK_WAKE_TIME))) {
            return Chronotype.LARK;
        }

        return Chronotype.PIGEON;
    }
}
