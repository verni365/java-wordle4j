package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        // Оборачиваем весь процесс в единый глобальный try-catch,
        // а также используем try-with-resources для Scanner и PrintWriter
        try (PrintWriter log = new PrintWriter(new FileWriter("system.log", true), true);
             Scanner scanner = new Scanner(System.in)) {

            log.println("=== Запуск приложения Wordle ===");

            // Загрузка словаря
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.load("words_ru.txt", log);

            if (dictionary.getWords().isEmpty()) {
                throw new RuntimeException("Словарь пуст! Дальнейшая игра невозможна.");
            }

            // Создание и запуск игры
            WordleGame game = new WordleGame(dictionary, log);
            System.out.println("Добро пожаловать в игру Wordle!");
            System.out.println("Компьютер загадал существительное из 5 букв. У вас есть 6 попыток.");
            System.out.println("Введите слово. Если нужна подсказка - просто нажмите Enter.");

            // Игровой цикл
            while (!game.isGameOver()) {
                System.out.println("\nОсталось попыток: " + game.getSteps());
                System.out.print("Ваш ход: ");

                String input = scanner.nextLine().trim();

                // Обработка подсказки при пустом вводе
                if (input.isEmpty()) {
                    String hint = game.getHint();
                    System.out.println("💡 Подсказка: попробуйте слово '" + hint + "'");
                    log.println("Пользователь запросил подсказку. Выдано: " + hint);
                    continue; // Ход не тратится
                }

                // Обработка попытки
                try {
                    String feedback = game.guess(input);
                    System.out.println("> " + input);
                    System.out.println("> " + feedback);
                } catch (InvalidWordLengthException | WordNotFoundException e) {
                    // Перехватываем игровые исключения и сообщаем об этом пользователю
                    System.out.println("⚠️ Ошибка: " + e.getMessage());
                    log.println("Предупреждение игроку: " + e.getMessage() + " (ввод: " + input + ")");
                }
            }

            // Итоги игры
            if (game.isWin()) {
                System.out.println("\n🎉 Поздравляем! Вы отгадали слово: " + game.getAnswer());
                log.println("Игра завершена победой.");
            } else {
                System.out.println("\n💀 Попытки закончились. Было загадано слово: " + game.getAnswer());
                log.println("Игра завершена поражением.");
            }

        } catch (Exception e) {
            // Глобальный перехват любых системных и непредвиденных ошибок (отсутствие файла и т.д.)
            System.out.println("Произошла системная ошибка приложения.");
            System.out.println("Подробности: " + e.getMessage());
            e.printStackTrace();
        }
    }
}