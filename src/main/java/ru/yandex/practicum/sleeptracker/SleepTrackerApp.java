package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class SleepTrackerApp {

    private static final int MAX_FRACTION_DIGITS = 2;

    private static final List<Function<List<SleepingSession>, ? extends SleepAnalysisResult<?>>> ANALYSES = List.of(
            new SessionCountAnalysis(),
            new MinimumDurationAnalysis(),
            new MaximumDurationAnalysis(),
            new AverageDurationAnalysis(),
            new BadQualityCountAnalysis()
    );

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Укажите путь к файлу с логом сна");
            return;
        }

        try {
            List<SleepingSession> sessions = new SleepingSessionLoader().load(Path.of(args[0]));

            if (sessions.isEmpty()) {
                System.out.println("В файле нет сессий сна");
                return;
            }

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"));
            numberFormat.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
            numberFormat.setGroupingUsed(false);

            ANALYSES.stream()
                    .map(analysis -> analysis.apply(sessions))
                    .forEach(result -> {
                        Object value = result.value() instanceof Number number
                                ? numberFormat.format(number)
                                : result.value();

                        System.out.println(result.description() + ": " + value);
                    });
        } catch (IOException exception) {
            System.out.println("Не удалось прочитать файл с логом сна: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            System.out.println("Не удалось загрузить лог сна: " + exception.getMessage());
        }
    }
}
