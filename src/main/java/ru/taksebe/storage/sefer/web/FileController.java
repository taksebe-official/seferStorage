package ru.taksebe.storage.sefer.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.taksebe.storage.sefer.api.FileService;
import ru.taksebe.storage.sefer.dto.FileInfoDto;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/files")
@Tag(name = "Работа с файлами", description = "FileController")
public class FileController {
    FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "Upload file")
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FileInfoDto uploadFile(MultipartFile file) throws IOException {
        return fileService.upload(file);
    }

    @Operation(summary = "Download file")
    @GetMapping(value = "/download/{guid}")
    public void downloadFile(HttpServletResponse response,
                             @PathVariable(name = "guid")
                             @Parameter(description = "guid - имя файла", required = true)
                                     UUID fileName) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        try {
            fileService.download(response.getOutputStream(), fileName);
            response.setStatus(HttpStatus.OK.value());
        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Operation(summary = "Delete file")
    @DeleteMapping(value = "/{guid}")
    public ResponseEntity<Void> deleteFile(@PathVariable(name = "guid")
                                           @Parameter(description = "guid - имя файла", required = true)
                                                   UUID fileName) {
        try {
            fileService.delete(fileName);
            return ResponseEntity.ok()
                    .build();
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound()
                    .build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}