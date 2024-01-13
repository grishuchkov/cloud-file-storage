package ru.grishuchkov.cloudfilestorage.dto.metadata;

import lombok.*;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataForRename extends FileMetadata {
    private String newFilenameWithExtension;
}

