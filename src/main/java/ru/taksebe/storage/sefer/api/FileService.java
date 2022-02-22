package ru.taksebe.storage.sefer.api;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.taksebe.storage.sefer.config.SeferConfig;
import ru.taksebe.storage.sefer.dto.FileInfoDto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {
    private final SeferConfig seferConfig;

    public FileService(SeferConfig seferConfig) {
        this.seferConfig = seferConfig;
    }

    public FileInfoDto upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = getFileName(file);
        UUID seferFileName = UUID.randomUUID();

        if (isFileToZip(filename)) {
            uploadZip(file, seferFileName);
        } else {
            uploadFile(file, seferFileName);
        }
        return new FileInfoDto(seferFileName);
    }

    public void download(OutputStream os, UUID fileName) throws IOException {
        String filePath = seferConfig.getPath() + fileName;
        File file = new File(filePath);
        File zipFile = new File(filePath + ".zip");

        if (zipFile.exists()) {
            downloadZip(os, zipFile);
        } else if (file.exists()) {
            downloadFile(os, file);
        } else {
            throw new FileNotFoundException();
        }
    }

    public void delete(UUID fileName) throws IOException {
        String filePath = seferConfig.getPath() + fileName;
        Path file = Path.of(filePath);
        Path zipFile = Path.of(filePath + ".zip");

        if (!Files.deleteIfExists(zipFile)) {
            if (!Files.deleteIfExists(file)) {
                throw new FileNotFoundException();
            }
        }
    }

    private String getFileName(MultipartFile file) {
        String fileName = file.getName();
        if (!fileName.isBlank() && fileName.contains(".")) {
            return fileName;
        }
        return Objects.requireNonNullElse(file.getOriginalFilename(), "");
    }

    private boolean isFileToZip(String fileName) {
        String[] nameParts = fileName.split("\\.");
        return seferConfig.getToZipFileTypeList().contains(nameParts[nameParts.length - 1].toLowerCase());
    }

    private void uploadZip(MultipartFile file, UUID fileName) throws IOException {
        String filePath = seferConfig.getPath() + fileName;
        File fileToZip = new File(filePath);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());

        try (FileOutputStream fos = new FileOutputStream(filePath + ".zip");
             ZipOutputStream zos = new ZipOutputStream(fos);
             InputStream is = file.getInputStream()) {
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) > 0) {
                zos.write(bytes, 0, length);
            }
        }
    }

    private void uploadFile(MultipartFile file, UUID fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(seferConfig.getPath() + fileName);
             InputStream is = file.getInputStream()) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) > 0) {
                fos.write(bytes, 0, length);
            }
        }
    }

    private void downloadZip(OutputStream os, File file) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream((file)))) {
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry != null) {
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
            zis.closeEntry();
        }
    }

    private void downloadFile(OutputStream outputStream, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}