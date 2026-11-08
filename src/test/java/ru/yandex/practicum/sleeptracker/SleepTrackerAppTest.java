package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepTrackerAppTest {

    @TempDir
    Path directory;

    @Test
    void printsAllBasicMetricsWithDescriptions() throws IOException {
        Path path = writeLog("""
                01.10.25 23:15;02.10.25 07:30;GOOD
                02.10.25 14:10;02.10.25 15:00;BAD
                """);
        String expected = """
                Количество сессий сна: 2
                Минимальная продолжительность сессии сна (мин): 50
                Максимальная продолжительность сессии сна (мин): 495
                Средняя продолжительность сессии сна (мин): 272,5
                Количество сессий с плохим качеством сна: 1
                """.replace("\n", System.lineSeparator());


        String output = runApp(path.toString());


        assertEquals(expected, output);
    }

    @Test
    void roundsAverageOnlyForOutput() throws IOException {
        Path path = writeLog("""
                01.10.25 14:00;01.10.25 15:00;GOOD
                02.10.25 14:00;02.10.25 15:01;NORMAL
                03.10.25 14:00;03.10.25 15:01;GOOD
                """);


        String output = runApp(path.toString());


        assertTrue(
                output.contains("Средняя продолжительность сессии сна (мин): 60,67" + System.lineSeparator())
        );
    }

    @Test
    void reportsMissingArgument() {
        String output = runApp();


        assertEquals("Укажите путь к файлу с логом сна" + System.lineSeparator(), output);
    }

    @Test
    void rejectsExtraArguments() {
        String output = runApp("first.txt", "second.txt");


        assertEquals("Укажите путь к файлу с логом сна" + System.lineSeparator(), output);
    }

    @Test
    void reportsEmptyFileWithoutRunningAnalysis() throws IOException {
        Path path = writeLog("");


        String output = runApp(path.toString());


        assertEquals("В файле нет сессий сна" + System.lineSeparator(), output);
    }

    @Test
    void reportsMissingFile() {
        String output = runApp(directory.resolve("missing.txt").toString());


        assertTrue(output.startsWith("Не удалось прочитать файл с логом сна:"));
    }

    @Test
    void reportsMalformedInputWithoutRunningAnalysis() throws IOException {
        Path path = writeLog("неверная запись");


        String output = runApp(path.toString());


        assertTrue(output.startsWith("Не удалось загрузить лог сна: Строка 1:"));
        assertEquals(1, output.lines().count());
    }

    @Test
    void reportsInvalidPath() {
        String output = runApp("invalid\0path");


        assertTrue(output.startsWith("Не удалось загрузить лог сна:"));
    }

    private Path writeLog(String content) throws IOException {
        return Files.writeString(
                directory.resolve("сон.txt"),
                content,
                StandardCharsets.UTF_8
        );
    }

    private String runApp(String... args) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            SleepTrackerApp.main(args);
        } finally {
            System.setOut(originalOutput);
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
