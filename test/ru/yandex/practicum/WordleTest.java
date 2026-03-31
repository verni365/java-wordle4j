package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleGame game;
    private WordleDictionary testDictionary;
    private StringWriter logContent;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        logContent = new StringWriter();
        log = new PrintWriter(logContent);
        testDictionary = new WordleDictionary(List.of("речка", "арбуз", "площа", "город", "кошка"));

        game = new WordleGame(new WordleDictionary(List.of("речка")), log);
    }

    @Test
    void testInitialState() {
        assertEquals(6, game.getSteps(), "В начале должно быть 6 попыток");
        assertFalse(game.isGameOver(), "Игра не должна быть окончена сразу");
        assertEquals("речка", game.getAnswer());
    }

    @Test
    void testFullMatch() throws GameException {
        String feedback = game.guess("речка");
        assertEquals("+++++", feedback, "Пять плюсов при полном совпадении");
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
    }

    @Test
    void testPartialMatch() throws GameException {
        WordleDictionary dict = new WordleDictionary(List.of("речка", "арбуз"));
        WordleGame customGame = new WordleGame(dict, log);

        String secret = customGame.getAnswer();
        String input = secret.equals("речка") ? "арбуз" : "речка";

        String feedback = customGame.guess(input);

        assertEquals(5, feedback.length());
        assertTrue(feedback.contains("^") || feedback.contains("-"));
        assertEquals(5, customGame.getSteps(), "Количество шагов должно уменьшиться");
    }

    @Test
    void testInvalidLengthThrowsException() {
        assertThrows(InvalidWordLengthException.class, () -> game.guess("рок"));
    }

    @Test
    void testWordNotFoundThrowsException() {
        assertThrows(WordNotFoundException.class, () -> game.guess("пицца"));
    }

    @Test
    void testLossAfterSixAttempts() throws GameException {
        WordleDictionary dict = new WordleDictionary(List.of("речка", "арбуз"));
        WordleGame loseGame = new WordleGame(dict, log);

        String wrongWord = loseGame.getAnswer().equals("речка") ? "арбуз" : "речка";
        for (int i = 0; i < 6; i++) {
            loseGame.guess(wrongWord);
        }
        assertTrue(loseGame.isGameOver());
        assertFalse(loseGame.isWin());
        assertEquals(0, loseGame.getSteps());
    }

    @Test
    void testLogging() {
        assertTrue(logContent.toString().contains("Новая игра начата"),
                "Сообщение о старте должно быть в логе");
    }
}