package ru.yandex.practicum;

public class WordNotFoundException extends GameException {
    public WordNotFoundException(String message) {
        super(message);
    }
}