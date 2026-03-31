package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;

    private final LinkedHashMap<String, String> history;
    private final Set<String> usedHints;

    private List<String> currentCandidates;

    private boolean isWin;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = MAX_STEPS;

        this.history = new LinkedHashMap<>();
        this.usedHints = new HashSet<>();
        this.currentCandidates = new ArrayList<>(dictionary.getWords());

        log.println("Новая игра начата. (Для отладки: загадано слово '" + answer + "')");
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isGameOver() {
        return steps <= 0 || isWin;
    }

    public boolean isWin() {
        return isWin;
    }

    public String guess(String word) throws InvalidWordLengthException, WordNotFoundException {
        if (word == null || word.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Введенное слово должно состоять ровно из 5 букв.");
        }

        word = word.toLowerCase().replace('ё', 'е');

        if (!dictionary.contains(word)) {
            throw new WordNotFoundException("Слова '" + word + "' нет в словаре.");
        }

        String feedback = calculateFeedback(word, answer);

        steps--;
        history.put(word, feedback);

        if (feedback.equals("+++++")) {
            isWin = true;
        }

        log.println("Пользователь ввел: " + word + ", Результат: " + feedback + ", Осталось шагов: " + steps);

        filterCandidates(word, feedback);

        return feedback;
    }

    public String getHint() {
        if (currentCandidates.isEmpty()) {
            return "Не могу найти подходящих слов в словаре.";
        }

        for (String candidate : currentCandidates) {
            if (!history.containsKey(candidate) && !usedHints.contains(candidate)) {
                usedHints.add(candidate);
                return candidate;
            }
        }

        return "У меня закончились варианты для подсказок.";
    }

    private void filterCandidates(String guessWord, String actualFeedback) {
        List<String> nextCandidates = new ArrayList<>();

        for (String candidate : currentCandidates) {
            if (calculateFeedback(guessWord, candidate).equals(actualFeedback)) {
                nextCandidates.add(candidate);
            }
        }

        currentCandidates = nextCandidates;
    }

    private String calculateFeedback(String guess, String target) {
        StringBuilder feedback = new StringBuilder("-----");
        boolean[] targetUsed = new boolean[WORD_LENGTH];
        boolean[] guessUsed = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                feedback.setCharAt(i, '+');
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < WORD_LENGTH; j++) {
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
//Если что я знаю чем черевато отсутствие комментариев. Чем дольше над чем то работаю тем больше они мешают
//мне ошибку выдает загруженость предложения местоимениями прикол