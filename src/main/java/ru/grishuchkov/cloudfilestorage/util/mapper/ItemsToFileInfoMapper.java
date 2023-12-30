package ru.grishuchkov.cloudfilestorage.util.mapper;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.FileInfo;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemsToFileInfoMapper {

    private final FileUtils fileUtils;

    public List<FileInfo> toFile(List<Item> items) {
        List<FileInfo> fileInfos = new ArrayList<>(items.size());

        for (Item item : items) {
            String nameWithPathAndExtension = item.objectName();

            String extension = fileUtils.getExtension(nameWithPathAndExtension);
            String filename = fileUtils.getFilename(nameWithPathAndExtension);

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
