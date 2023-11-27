package ru.grishuchkov.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilesContainer {
    private List<File> files;
    private Path path;
}
