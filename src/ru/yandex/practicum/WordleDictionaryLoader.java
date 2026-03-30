package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    // Метод принимает имя файла и логгер PrintWriter
    public WordleDictionary load(String filename, PrintWriter log) throws IOException {
        List<String> validWords = new ArrayList<>();

        // Используем try-with-resources для корректного закрытия потоков
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Нормализация слова
                String word = line.trim().toLowerCase().replace('ё', 'е');
                // Нас интересуют только слова из 5 букв
                if (word.length() == 5) {
                    validWords.add(word);
                }
            }
        }
        log.println("Загружено " + validWords.size() + " пятибуквенных слов из файла " + filename);

        return new WordleDictionary(validWords);
    }
}