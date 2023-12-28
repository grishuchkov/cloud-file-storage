package ru.grishuchkov.cloudfilestorage.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataForRename extends FileMetadata {
    private String newFilename;
}

