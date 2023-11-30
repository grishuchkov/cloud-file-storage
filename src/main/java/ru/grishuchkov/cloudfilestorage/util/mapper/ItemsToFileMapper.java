package ru.grishuchkov.cloudfilestorage.util.mapper;

import io.minio.messages.Item;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.FileInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ItemsToFileMapper {

    public List<FileInfo> toFile(List<Item> items) {
        List<FileInfo> fileInfos = new ArrayList<>(items.size());

        for (Item item : items) {
            String nameWithPathAndExtension = item.objectName();

            String extension = getExtension(nameWithPathAndExtension);
            String filename = getFilename(nameWithPathAndExtension);

            FileInfo fileInfo = createFile(filename, extension);
            fileInfos.add(fileInfo);
        }
        Collections.reverse(fileInfos);
        return fileInfos;
    }

    private String getExtension(String filename) {
        int indexOfDot = filename.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "none";
        }
        return filename.substring(indexOfDot + 1);
    }

    private String getFilename(String nameWithPathAndExtension){
        List<String> parts = Arrays.stream(nameWithPathAndExtension.split("/")).toList();

        if(parts.isEmpty()){
            return nameWithPathAndExtension;
        }
        return parts.get(parts.size() - 1);
    }

    private FileInfo createFile(String filename, String extension) {
        return FileInfo.builder()
                .filename(filename)
                .extension(extension)
                .build();
    }
}
