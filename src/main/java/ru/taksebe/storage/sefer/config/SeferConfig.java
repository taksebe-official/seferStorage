package ru.taksebe.storage.sefer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SeferConfig {
    private final String path;
    private final List<String> toZipFileTypeList;

    public SeferConfig(@Value("${sefer.store}") String path,
                       @Value("${sefer.to-zip}") String toZipFileTypes) {
        if (path == null || !(path.endsWith("/") || path.endsWith("\\"))) {
            throw new IllegalArgumentException("Неверный адрес папки для хранения файлов");
        }
        this.path = path;
        this.toZipFileTypeList = Arrays.asList(toZipFileTypes.split(","));
    }

    public String getPath() {
        return path;
    }

    public List<String> getToZipFileTypeList() {
        return toZipFileTypeList;
    }
}