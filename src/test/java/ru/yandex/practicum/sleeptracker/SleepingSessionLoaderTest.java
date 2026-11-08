package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepingSessionLoaderTest {

    @TempDir
    Path directory;

    private final SleepingSessionLoader loader = new SleepingSessionLoader();

    @Test
    void loadsSessionsWithAllQualityValues() throws IOException {
        Path path = writeLog("""
                01.10.25 23:15;02.10.25 07:30;GOOD
                02.10.25 14:10;02.10.25 15:00;NORMAL
                02.10.25 23:40;03.10.25 08:00;BAD
                """);


        List<SleepingSession> expected = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 10),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 40),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.BAD
                )
        );


        List<SleepingSession> sessions = loader.load(path);


        assertEquals(expected, sessions);
    }

    @Test
    void skipsBlankLines() throws IOException {
        Path path = writeLog("\n  \n01.10.25 23:15;02.10.25 07:30;GOOD\n\n");


        List<SleepingSession> sessions = loader.load(path);


        assertEquals(1, sessions.size());
    }

    @Test
    void loadsEmptyFile() throws IOException {
        Path path = writeLog("");


        List<SleepingSession> sessions = loader.load(path);


        assertTrue(sessions.isEmpty());
    }

    @Test
    void loadsFileContainingOnlyBlankLines() throws IOException {
        Path path = writeLog("\n \n\t\n");


        List<SleepingSession> sessions = loader.load(path);


        assertTrue(sessions.isEmpty());
    }

    @Test
    void rejectsMissingFile() {
        assertThrows(
                NoSuchFileException.class,
                () -> loader.load(directory.resolve("missing.txt"))
        );
    }

    @Test
    void reportsOriginalLineNumberForMalformedLine() throws IOException {
        Path path = writeLog("\n01.10.25 23:15;02.10.25 07:30;GOOD\n\nневерная запись\n");


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(path)
        );


        assertTrue(exception.getMessage().startsWith("Строка 4:"));
    }

    @Test
    void rejectsExtraField() throws IOException {
        assertInvalidLine("01.10.25 23:15;02.10.25 07:30;GOOD;extra");
    }

    @Test
    void rejectsMissingQuality() throws IOException {
        assertInvalidLine("01.10.25 23:15;02.10.25 07:30;");
    }

    @Test
    void rejectsUnknownQuality() throws IOException {
        assertInvalidLine("01.10.25 23:15;02.10.25 07:30;EXCELLENT");
    }

    @Test
    void rejectsInvalidCalendarDate() throws IOException {
        assertInvalidLine("31.02.25 23:15;01.03.25 07:30;GOOD");
    }

    @Test
    void rejectsInvalidTime() throws IOException {
        assertInvalidLine("01.10.25 24:00;02.10.25 07:30;GOOD");
    }

    @Test
    void rejectsInvalidDateFormat() throws IOException {
        assertInvalidLine("2025-10-01 23:15;2025-10-02 07:30;GOOD");
    }

    @Test
    void rejectsEndBeforeStart() throws IOException {
        assertInvalidLine("02.10.25 23:15;02.10.25 07:30;GOOD");
    }

    @Test
    void rejectsZeroDuration() throws IOException {
        assertInvalidLine("02.10.25 23:15;02.10.25 23:15;GOOD");
    }

    @Test
    void rejectsOverlappingSessionsWithOriginalLineNumber() throws IOException {
        Path path = writeLog("""
                01.10.25 23:15;02.10.25 07:30;GOOD

                02.10.25 07:00;02.10.25 08:00;NORMAL
                """);


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(path)
        );


        assertTrue(exception.getMessage().startsWith("Строка 3:"));
    }

    @Test
    void rejectsSessionsOutOfOrder() throws IOException {
        Path path = writeLog("""
                02.10.25 23:15;03.10.25 07:30;GOOD
                01.10.25 23:15;02.10.25 07:30;NORMAL
                """);


        assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(path)
        );
    }

    @Test
    void rejectsDuplicateSessions() throws IOException {
        Path path = writeLog("""
                01.10.25 23:15;02.10.25 07:30;GOOD
                01.10.25 23:15;02.10.25 07:30;GOOD
                """);


        assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(path)
        );
    }

    @Test
    void allowsAdjacentSessions() throws IOException {
        Path path = writeLog("""
                01.10.25 23:15;02.10.25 07:30;GOOD
                02.10.25 07:30;02.10.25 08:00;NORMAL
                """);


        List<SleepingSession> sessions = loader.load(path);


        assertEquals(2, sessions.size());
    }

    private void assertInvalidLine(String line) throws IOException {
        Path path = writeLog(line);


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(path)
        );


        assertTrue(exception.getMessage().startsWith("Строка 1:"));
    }

    private Path writeLog(String content) throws IOException {
        return Files.writeString(
                directory.resolve("сон.txt"),
                content,
                StandardCharsets.UTF_8
        );
    }
}
