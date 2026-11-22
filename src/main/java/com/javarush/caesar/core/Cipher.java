package com.javarush.caesar.core;

import com.javarush.caesar.exception.CipherException;

import java.util.HashMap;
import java.util.Map;

public class Cipher {

    private final char[] alphabet;
    /** Быстрый поиск позиции символа: O(1) вместо перебора массива. */
    private final Map<Character, Integer> index;

    public Cipher(char[] alphabet) {
        if (alphabet == null || alphabet.length == 0) {
            throw new IllegalArgumentException("Алфавит не может быть пустым");
        }
        this.alphabet = alphabet.clone();
        this.index = new HashMap<>();
        for (int i = 0; i < this.alphabet.length; i++) {
            this.index.putIfAbsent(this.alphabet[i], i);
        }
    }

    public String encrypt(String text, int shift) {
        if (text == null) {
            return "";
        }
        return process(text.toLowerCase(), shift);
    }

    public String decrypt(String encryptedText, int shift) throws CipherException {
        if (encryptedText == null) {
            throw new CipherException("Текст для расшифровки не задан");
        }
        return process(encryptedText.toLowerCase(), -shift);
    }

    private String process(String text, int shift) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            sb.append(shiftChar(text.charAt(i), shift));
        }
        return sb.toString();
    }

    private char shiftChar(char c, int shift) {
        Integer idx = index.get(c);
        if (idx == null) {
            return c; // символа нет в алфавите — оставляем как есть
        }
        int newIdx = Math.floorMod(idx + shift, alphabet.length);
        return alphabet[newIdx];
    }
}