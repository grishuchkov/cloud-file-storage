package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilesContainer {
    private List<FileInfo> files;
    private FilePath path;
}
