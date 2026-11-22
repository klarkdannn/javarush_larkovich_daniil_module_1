package com.javarush.caesar;

import com.javarush.caesar.core.BruteForce;
import com.javarush.caesar.core.Cipher;
import com.javarush.caesar.exception.CipherException;
import com.javarush.caesar.service.FileManager;
import com.javarush.caesar.service.Validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MainApp {

    private static final char[] ALPHABET = {
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
            'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            '.', ',', '«', '»', '"', '\'', ':', '!', '?', ' '
    };

    private final Scanner scanner = new Scanner(System.in);
    private final FileManager fileManager = new FileManager();
    private final Validator validator = new Validator();
    private final Cipher cipher = new Cipher(ALPHABET);
    private final BruteForce bruteForce = new BruteForce();

    public static void main(String[] args) {
        new MainApp().run();
    }

    private void run() {
        System.out.println("Шифр Цезаря: шифрование и расшифровка файлов");
        System.out.println("Алфавит: " + ALPHABET.length + " символов, ключ 0.." + (ALPHABET.length - 1));
        while (true) {
            printMenu();
            String choice = ask("Выберите действие: ");
            try {
                switch (choice) {
                    case "1" -> encryptFile();
                    case "2" -> decryptFile();
                    case "3" -> bruteForceFile();
                    case "0", "exit", "выход" -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Нет такого пункта: " + choice);
                }
            } catch (CipherException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Зашифровать файл");
        System.out.println("2. Расшифровать файл с ключом");
        System.out.println("3. Взломать перебором ключей");
        System.out.println("0. Выход (или exit)");
    }

    //режимы

    private void encryptFile() throws CipherException {
        Path src = askSourceFile();
        Path dst = askOutputFile(src);
        int key = askKey();

        String text = fileManager.readFile(src.toString());
        String encrypted = cipher.encrypt(text, key);
        fileManager.writeFile(encrypted, dst.toString());
        System.out.println("Зашифровано в " + dst.toAbsolutePath());
    }

    private void decryptFile() throws CipherException {
        Path src = askSourceFile();
        Path dst = askOutputFile(src);
        int key = askKey();

        String text = fileManager.readFile(src.toString());
        String decrypted = cipher.decrypt(text, key);
        fileManager.writeFile(decrypted, dst.toString());
        System.out.println("Расшифровано в " + dst.toAbsolutePath());
    }

    private void bruteForceFile() throws CipherException {
        Path src = askSourceFile();
        Path dst = askOutputFile(src);

        String text = fileManager.readFile(src.toString());
        String decrypted = bruteForce.decryptByBruteForce(text, ALPHABET);
        fileManager.writeFile(decrypted, dst.toString());
        System.out.println("Записано в " + dst.toAbsolutePath());
    }

    // ввод с проверками

    /** Спрашивает исходный файл, пока не получит существующий. */
    private Path askSourceFile() throws CipherException {
        while (true) {
            String raw = ask("Исходный файл: ");
            if (isExit(raw)) {
                throw new CipherException("Выход по запросу пользователя.");
            }
            if (raw.isEmpty()) {
                System.out.println("Путь не может быть пустым. Попробуйте снова.");
                continue;
            }
            if (!validator.isFileExists(raw)) {
                System.out.println("Файл не найден: " + raw + ". Попробуйте снова.");
                continue;
            }
            return Paths.get(raw);
        }
    }

    /** Спрашивает выходной файл, пока не получит корректный. */
    private Path askOutputFile(Path src) throws CipherException {
        while (true) {
            String raw = ask("Файл результата: ");
            if (isExit(raw)) {
                throw new CipherException("Выход по запросу пользователя.");
            }
            if (raw.isEmpty()) {
                System.out.println("Путь не может быть пустым. Попробуйте снова.");
                continue;
            }
            Path dst = Paths.get(raw);

            // защита от перезаписи исходного файла
            try {
                if (Files.exists(dst) && Files.isSameFile(src, dst)) {
                    System.out.println("Выходной файл совпадает с исходным — перезапись запрещена. Попробуйте снова.");
                    continue;
                }
            } catch (Exception ignored) {
                ignored.getStackTrace();
            }

            Path parent = dst.toAbsolutePath().getParent();
            if (parent != null && !Files.exists(parent)) {
                System.out.println("Директория не существует: " + parent + ". Попробуйте снова.");
                continue;
            }
            return dst;
        }
    }

    /** Спрашивает ключ в диапазоне 0..42, повторяет при ошибке */
    private int askKey() throws CipherException {
        while (true) {
            String raw = ask("Ключ (0.." + (ALPHABET.length - 1) + "), 'exit' — выход: ");
            if (isExit(raw)) {
                throw new CipherException("Выход по запросу пользователя.");
            }
            int key;
            try {
                key = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Ключ должен быть целым числом. Вы ввели: \"" + raw + "\". Попробуйте снова.");
                continue;
            }
            if (!validator.isValidKey(key, ALPHABET)) {
                System.out.println(
                        "Ключ должен быть в диапазоне 0.." + (ALPHABET.length - 1)
                                + ". Вы ввели: " + key + ". Попробуйте снова.");
                continue;
            }
            return key;
        }
    }

    // утилиты

    private boolean isExit(String s) {
        return s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("выход");
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}