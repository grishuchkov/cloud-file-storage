package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Data;

@Data
public class FileMetadata {
    private int ownerId;
    private String filename;
    private String extension;
}
