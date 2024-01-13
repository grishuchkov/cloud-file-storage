package ru.grishuchkov.cloudfilestorage.dto.metadata;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private String filename;
    private String extension;

    public String getFilenameWithExtension(){
        return filename + "." + extension;
    }
}
