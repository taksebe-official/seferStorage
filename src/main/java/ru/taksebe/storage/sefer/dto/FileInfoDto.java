package ru.taksebe.storage.sefer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(title = "FileInfoDto", description = "Информация о загруженном файле")
public class FileInfoDto {

    @Schema(title = "Имя файла при хранении (guid)", required = true)
    @JsonProperty
    private UUID fileName;

    public UUID getFileName() {
        return fileName;
    }

    public FileInfoDto(UUID fileName) {
        this.fileName = fileName;
    }

    public FileInfoDto() {
    }
}