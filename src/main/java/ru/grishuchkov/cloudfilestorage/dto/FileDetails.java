package ru.grishuchkov.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDetails {
    private File file;
    private Path path;
    private String ownerUsername;

    public FileDetails(File file, String ownerUsername) {
        this.file = file;
        this.ownerUsername = ownerUsername;
    }

    public FileDetails() {
        this.file = new File();
        this.path = new Path();
    }
}
