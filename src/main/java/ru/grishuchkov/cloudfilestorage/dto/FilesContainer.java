package ru.grishuchkov.cloudfilestorage.dto;

import lombok.Builder;
import lombok.Data;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileInfo;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FilePath;

import java.util.List;

@Data
@Builder
public class FilesContainer {
    private List<FileInfo> files;
    private FilePath path;
}
