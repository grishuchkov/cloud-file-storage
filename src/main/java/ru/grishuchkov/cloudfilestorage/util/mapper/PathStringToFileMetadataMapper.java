package ru.grishuchkov.cloudfilestorage.util.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.FileInfo;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.FilePath;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PathStringToFileMetadataMapper {

    private final FileUtils fileUtils;

    public List<FileMetadata> toMetadata(List<String> paths){
        List<FileMetadata> fileMetadataList = new ArrayList<>();

        for (String path : paths) {
            FileInfo fileInfo = FileInfo.builder()
                    .filename(fileUtils.getFilename(path))
                    .extension(fileUtils.getExtension(path))
                    .build();

            FilePath filePath = new FilePath(fileUtils.getPathWithoutFilename(path));

            fileMetadataList.add(new FileMetadata(fileInfo, filePath));
        }

        return fileMetadataList;
    }
}
