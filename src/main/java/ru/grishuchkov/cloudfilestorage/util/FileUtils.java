package ru.grishuchkov.cloudfilestorage.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FileUtils {

    public String getExtension(String filename) {
        int indexOfDot = filename.lastIndexOf(".");
        String lastCharOfFilename = filename.substring(filename.length() - 1);

        if(lastCharOfFilename.equals("/")){
            return "folder";
        }
        if (indexOfDot == -1) {
            return "none";
        }
        return filename.substring(indexOfDot + 1);
    }

    public String getFilename(String nameWithPathAndExtension) {
        List<String> parts = Arrays.stream(nameWithPathAndExtension.split("/")).toList();
        String filenameWithExtension = nameWithPathAndExtension;

        if (!parts.isEmpty()) {
            filenameWithExtension = parts.get(parts.size() - 1);
        }

        int indexOfDot = filenameWithExtension.lastIndexOf(".");

        if(indexOfDot == -1){
            return filenameWithExtension;
        }

        return filenameWithExtension.substring(0, indexOfDot);
    }
}
