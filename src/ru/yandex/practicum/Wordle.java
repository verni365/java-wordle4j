package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter("system.log", true), true);
             Scanner scanner = new Scanner(System.in)) {

            log.println("=== Запуск приложения Wordle ===");

            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.load("words_ru.txt", log);

            if (dictionary.getWords().isEmpty()) {
                throw new RuntimeException("Словарь пуст! Дальнейшая игра невозможна.");
            }

            WordleGame game = new WordleGame(dictionary, log);
            System.out.println("Добро пожаловать в игру Wordle!");
            System.out.println("Компьютер загадал существительное из 5 букв. У вас есть 6 попыток.");
            System.out.println("Введите слово. Если нужна подсказка - просто нажмите Enter.");

            while (!game.isGameOver()) {
                System.out.println("\nОсталось попыток: " + game.getSteps());
                System.out.print("Ваш ход: ");

                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    String hint = game.getHint();
                    System.out.println("💡 Подсказка: попробуйте слово '" + hint + "'");
                    log.println("Пользователь запросил подсказку. Выдано: " + hint);
                    continue;
                }

                try {
                    String feedback = game.guess(input);
                    System.out.println("> " + input);
                    System.out.println("> " + feedback);
                } catch (InvalidWordLengthException | WordNotFoundException e) {
                    System.out.println("⚠️ Ошибка: " + e.getMessage());
                    log.println("Предупреждение игроку: " + e.getMessage() + " (ввод: " + input + ")");
                }
            }

            if (game.isWin()) {
                System.out.println("\n🎉 Поздравляем! Вы отгадали слово: " + game.getAnswer());
                log.println("Игра завершена победой.");
            } else {
                System.out.println("\n💀 Попытки закончились. Было загадано слово: " + game.getAnswer());
                log.println("Игра завершена поражением.");
            }

        } catch (Exception e) {
            System.out.println("Произошла системная ошибка приложения.");
            System.out.println("Подробности: " + e.getMessage());
            e.printStackTrace();
        }
    }
}