package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

public class WordleDictionary {

    private final List<String> words;
    private final Random random;

    public WordleDictionary(List<String> words) {
        this.words = words;
        this.random = new Random();
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new RuntimeException("Критическая ошибка: Словарь пуст.");
        }
        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWords() {
        return words;
    }
}
