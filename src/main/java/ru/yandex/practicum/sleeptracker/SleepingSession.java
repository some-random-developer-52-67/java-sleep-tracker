package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public record SleepingSession(
        LocalDateTime start,
        LocalDateTime end,
        SleepQuality quality
) {

    public SleepingSession {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        Objects.requireNonNull(quality);

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Окончание сна должно быть позже начала");
        }
    }

    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
