package ru.taksebe.storage.sefer.api;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import ru.taksebe.storage.sefer.config.SeferConfig;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileService.class, SeferConfig.class})
@ActiveProfiles("test")
class FileServiceTest {

    @Autowired
    FileService fileService;

    final String toZipFileResourcePath = "Text.txt";
    final String noZipFileResourcePath = "Image.jpg";

    @Test
    public void fileLifeCycleTest() throws URISyntaxException {
        File toZipFile = getTestFile(this.toZipFileResourcePath);
        assertDoesNotThrow(() -> executeFileLifeCycle(toZipFile), "Throw legal \"to zip\" file life cycle");

        File noZipFile = getTestFile(this.noZipFileResourcePath);
        assertDoesNotThrow(() -> executeFileLifeCycle(noZipFile), "Throw legal \"no zip\" file life cycle");
    }

    @Test
    public void exceptionTest() throws IOException {
        uploadEmptyFile();

        UUID randomSeferFileName = UUID.randomUUID();
        assertThrows(FileNotFoundException.class, () -> downloadFile(randomSeferFileName),
                "Download file by random UUID doesn't throws");
        assertThrows(FileNotFoundException.class, () -> deleteFile(randomSeferFileName),
                "Delete file by random UUID doesn't throws");
    }

    private File getTestFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();
        return new File(uri);
    }

    private void executeFileLifeCycle(File file) throws IOException {
        UUID seferFileName = uploadFile(file);
        downloadFile(seferFileName);
        deleteFile(seferFileName);
    }

    private void uploadEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("sefer-test", "doc");
        try {
            assertThrows(IllegalArgumentException.class, () -> uploadFile(emptyFile),
                    "Upload empty file doesn't throws");
        } finally {
            emptyFile.deleteOnExit();
        }
    }

    private UUID uploadFile(File file) throws IOException {
        UUID seferFileName;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
            seferFileName = fileService.upload(multipartFile).getFileName();
        }
        return seferFileName;
    }

    private void downloadFile(UUID seferFileName) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            fileService.download(outputStream, seferFileName);
            assertNotNull(outputStream);
        }
    }

    private void deleteFile(UUID seferFileName) throws IOException {
        fileService.delete(seferFileName);
        assertThrows(Exception.class, () -> fileService.delete(seferFileName),
                "Download file by earlier deleted file's UUID doesn't throws");
    }
}