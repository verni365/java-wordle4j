package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;

    // LinkedHashMap сохраняет порядок ходов. Ключ - слово, Значение - его результат (+^-^-)
    private final LinkedHashMap<String, String> history;
    private final Set<String> usedHints;

    // Список слов-кандидатов для подсказок (сужается с каждым ходом)
    private List<String> currentCandidates;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = 6;
        this.history = new LinkedHashMap<>();
        this.usedHints = new HashSet<>();
        // Изначально кандидатами являются все слова
        this.currentCandidates = new ArrayList<>(dictionary.getWords());

        log.println("Новая игра начата. (Для отладки: загадано слово '" + answer + "')");
    }

    public int getSteps() { return steps; }
    public String getAnswer() { return answer; }

    // Игра завершается, если ходов не осталось или слово угадано
    public boolean isGameOver() {
        return steps <= 0 || history.containsValue("+++++");
    }

    public boolean isWin() {
        return history.containsValue("+++++");
    }

    // Основной метод хода
    // Выбрасывает проверяемые исключения в сигнатуре согласно ТЗ
    public String guess(String word) throws InvalidWordLengthException, WordNotFoundException {
        if (word == null || word.length() != 5) {
            throw new InvalidWordLengthException("Введенное слово должно состоять ровно из 5 букв.");
        }

        word = word.toLowerCase().replace('ё', 'е');

        if (!dictionary.contains(word)) {
            throw new WordNotFoundException("Слова '" + word + "' нет в словаре.");
        }

        String feedback = calculateFeedback(word, answer);

        // Ход засчитывается только если слово прошло все проверки
        steps--;
        history.put(word, feedback);
        log.println("Пользователь ввел: " + word + ", Результат: " + feedback + ", Осталось шагов: " + steps);

        // Обновляем список кандидатов для системы подсказок (уменьшаем вычислительную сложность)
        filterCandidates(word, feedback);

        return feedback;
    }

    // Метод получения подсказки
    public String getHint() {
        if (currentCandidates.isEmpty()) {
            return "Не могу найти подходящих слов в словаре.";
        }
        // Ищем первое слово, которое мы еще не вводили и не выдавали в качестве подсказки
        for (String candidate : currentCandidates) {
            if (!history.containsKey(candidate) && !usedHints.contains(candidate)) {
                usedHints.add(candidate);
                return candidate;
            }
        }
        return "У меня закончились варианты для подсказок.";
    }

    // Отсеиваем слова, которые не подходят под известные правила
    private void filterCandidates(String guessWord, String actualFeedback) {
        List<String> nextCandidates = new ArrayList<>();
        for (String candidate : currentCandidates) {
            // Слово подходит, если бы оно давало точно такой же фидбек на введённое пользователем слово
            if (calculateFeedback(guessWord, candidate).equals(actualFeedback)) {
                nextCandidates.add(candidate);
            }
        }
        currentCandidates = nextCandidates;
    }

    // Алгоритм сравнения слова и формирования маски +^-^-
    private String calculateFeedback(String guess, String target) {
        StringBuilder feedback = new StringBuilder("-----");
        boolean[] targetUsed = new boolean[5];
        boolean[] guessUsed = new boolean[5];

        // 1-й проход: ищем точные совпадения (знак '+')
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                feedback.setCharAt(i, '+');
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // 2-й проход: ищем буквы не на своих местах (знак '^')
        for (int i = 0; i < 5; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < 5; j++) {
                    if (!targetUsed[j] && guess.charAt(i) == target.charAt(j)) {
                        feedback.setCharAt(i, '^');
                        targetUsed[j] = true;
                        break;
                    }
                }
            }
        }
        return feedback.toString();
    }
}