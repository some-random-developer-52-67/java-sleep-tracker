package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.stream.IntStream;

public class SleepingSessionLoader {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("dd.MM.uu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final int FIELD_COUNT = 3;

    public List<SleepingSession> load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<Integer> lineIndexes = IntStream.range(0, lines.size())
                .filter(index -> !lines.get(index).isBlank())
                .boxed()
                .toList();

        List<SleepingSession> sessions = lineIndexes.stream()
                .map(index -> parse(lines.get(index), index + 1))
                .toList();

        IntStream.range(1, sessions.size()).forEach(index -> {
            SleepingSession previous = sessions.get(index - 1);
            SleepingSession current = sessions.get(index);

            if (current.start().isBefore(previous.end())) {
                throw new IllegalArgumentException("Строка " + (lineIndexes.get(index) + 1)
                        + ": сессии должны идти по порядку и не пересекаться");
            }
        });

        return sessions;
    }

    private SleepingSession parse(String line, int lineNumber) {
        try {
            String[] fields = line.split(";", -1);

            if (fields.length != FIELD_COUNT) {
                throw new IllegalArgumentException("Ожидаются начало, окончание и качество сна через ';'");
            }

            return new SleepingSession(
                    LocalDateTime.parse(fields[0], DATE_TIME_FORMAT),
                    LocalDateTime.parse(fields[1], DATE_TIME_FORMAT),
                    SleepQuality.valueOf(fields[2])
            );
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Строка " + lineNumber + ": " + exception.getMessage(),
                    exception
            );
        }
    }
}
