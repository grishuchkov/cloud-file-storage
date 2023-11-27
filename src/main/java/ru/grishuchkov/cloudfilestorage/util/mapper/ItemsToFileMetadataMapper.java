package ru.grishuchkov.cloudfilestorage.util.mapper;

import io.minio.messages.Item;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ItemsToFileMetadataMapper {

    public List<File> toFile(List<Item> items) {
        List<File> files = new ArrayList<>(items.size());

        for (Item item : items) {
            String allFilename = item.objectName();
            String extension = getExtension(allFilename);
            File file = createFileMetadata(allFilename, extension);
            files.add(file);
        }
        Collections.reverse(files);
        return files;
    }

    private String getExtension(String filename) {
        int indexOfDot = filename.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "none";
        }
        return filename.substring(indexOfDot + 1);
    }

    private File createFileMetadata(String filename, String extension) {
        return File.builder()
                .filename(filename)
                .extension(extension)
                .build();
    }
}
