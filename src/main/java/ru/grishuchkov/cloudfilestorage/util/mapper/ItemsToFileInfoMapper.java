package ru.grishuchkov.cloudfilestorage.util.mapper;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.FileInfo;
import ru.grishuchkov.cloudfilestorage.util.FilenameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemsToFileInfoMapper {

    private final FilenameUtils filenameUtils;

    public List<FileInfo> toFile(List<Item> items) {
        List<FileInfo> fileInfos = new ArrayList<>(items.size());

        for (Item item : items) {
            String nameWithPathAndExtension = item.objectName();

            String extension = filenameUtils.getExtension(nameWithPathAndExtension);
            String filename = filenameUtils.getFilename(nameWithPathAndExtension);

            FileInfo fileInfo = createFile(filename, extension);
            fileInfos.add(fileInfo);
        }
        Collections.reverse(fileInfos);

        return fileInfos;
    }

    private FileInfo createFile(String filename, String extension) {
        return FileInfo.builder()
                .filename(filename)
                .extension(extension)
                .build();
    }
}
