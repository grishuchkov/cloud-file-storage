package ru.grishuchkov.cloudfilestorage.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileMetadata {
    private FileInfo fileInfo;
    private FilePath filePath;
    private String ownerUsername;

    public FileMetadata(FileInfo fileInfo, String ownerUsername) {
        this.fileInfo = fileInfo;
        this.ownerUsername = ownerUsername;
    }

    public FileMetadata(FileInfo fileInfo, FilePath filePath) {
        this.fileInfo = fileInfo;
        this.filePath = filePath;
    }

    public FileMetadata() {
        this.fileInfo = new FileInfo();
        this.filePath = new FilePath();
    }

}
