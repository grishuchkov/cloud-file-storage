package ru.grishuchkov.cloudfilestorage.dto;

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