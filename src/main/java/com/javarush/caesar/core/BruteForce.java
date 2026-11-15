package com.javarush.caesar.core;

import com.javarush.caesar.exception.CipherException;

import java.util.Set;

public class BruteForce {

    private static final Set<String> FREQUENT_WORDS = Set.of(
            "и", "в", "не", "на", "что", "с", "по", "это", "как",
            "к", "у", "из", "за", "от", "а", "же", "ли", "о", "Привет", 
            "дела", "один", "самые" , "так", "далее" , "ключ"
    );

    public String decryptByBruteForce(String encryptedText, char[] alphabet) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText == null ? "" : encryptedText;
        }

        Cipher cipher = new Cipher(alphabet);

        int bestKey = 0;
        String bestText = encryptedText;
        int bestScore = Integer.MIN_VALUE;

        for (int key = 0; key < alphabet.length; key++) {
            String candidate;
            try {
                candidate = cipher.decrypt(encryptedText, key);
            } catch (CipherException e) {
                continue; // не смогли — пробуем следующий ключ
            }
            int score = score(candidate);
            if (score > bestScore) {
                bestScore = score;
                bestKey = key;
                bestText = candidate;
            }
        }

        System.out.println("[brute force] найден ключ: " + bestKey);
        return bestText;
    }

    // эвристики

    private int score(String text) {
        return punctuationScore(text)
                + wordLengthScore(text)
                + frequentWordsScore(text);
    }

    /** После (точки/запятой/!/?/:/); ожидаем пробел — поощряем; иначе штрафуем. */
    private int punctuationScore(String text) {
        int score = 0;
        for (int i = 0; i + 1 < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '.' || c == ',' || c == '!' || c == '?' || c == ':' || c == ';') {
                score += Character.isWhitespace(text.charAt(i + 1)) ? 3 : -2;
            }
        }
        return score;
    }

    /** Русские слова в среднем 1..12 символов. Склейки > 20 — сильный штраф. */
    private int wordLengthScore(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int normal = 0;
        int tooLong = 0;
        for (String w : words) {
            if (w.isEmpty()) {
                continue;
            }
            int len = w.length();
            if (len >= 1 && len <= 12) {
                normal++;
            }
            if (len > 20) {
                tooLong++;
            }
        }
        return normal - 5 * tooLong;
    }

    /** Частые короткие слова в «каше» почти не встречаются — вес *2. */
    private int frequentWordsScore(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int count = 0;
        for (String w : words) {
            if (FREQUENT_WORDS.contains(w)) {
                count++;
            }
        }
        return count * 2;
    }
}