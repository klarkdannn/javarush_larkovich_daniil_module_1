package com.javarush.caesar.service;

import com.javarush.caesar.exception.CipherException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager {

    public String readFile(String filePath) throws CipherException {
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CipherException(
                    "Не удалось прочитать файл " + filePath + ": " + e.getMessage(), e);
        }
    }

    public void writeFile(String content, String filePath) throws CipherException {
        try {
            Path path = Paths.get(filePath);
            Files.writeString(path, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new CipherException(
                    "Не удалось записать файл " + filePath + ": " + e.getMessage(), e);
        }
    }
}