package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadata {
    private String filename;
    private String extension;
    private String path;
}
