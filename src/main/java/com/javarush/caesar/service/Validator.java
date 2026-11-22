package com.javarush.caesar.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Validator {

    public boolean isValidKey(int key, char[] alphabet) {
        if (alphabet == null || alphabet.length == 0) {
            return false;
        }
        return key >= 0 && key < alphabet.length;
    }

    public boolean isFileExists(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return false;
        }
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }
}