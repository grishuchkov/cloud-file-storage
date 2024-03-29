package ru.grishuchkov.cloudfilestorage.util;

import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;

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
        if (indexOfDot == -1 | indexOfDot == filename.length() - 1) {
            return "none";
        }
        return filename.substring(indexOfDot + 1);
    }

    public boolean hasExtension(String filename){
        int indexOfDot = filename.lastIndexOf(".");

        return !(indexOfDot == -1 | indexOfDot == filename.length() - 1);
    }

    public String getFilenameWithoutServiceExtension(String filename){
        String[] partsOfFilename = filename.split("\\.");
        String extension = partsOfFilename[partsOfFilename.length - 1];

        if ("none".equals(extension)){
            return partsOfFilename[0];
        }
        if("folder".equals(extension)){
            return partsOfFilename[0];
        }

        return filename;
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


    public boolean isValidPathAndFilename(String path){
        String regex = "[^<>#:;?\\\\\"*|]+$";
        return path.matches(regex);
    }

    public boolean isFolder(FileMetadata fileMetadata) {
        return "folder".equals(fileMetadata.getFileInfo().getExtension());
    }

    public String getPathWithoutFilename(String absolutePath){
        int indexOfSlash = absolutePath.lastIndexOf("/");

        if(indexOfSlash == -1){
            return "";
        }

        return absolutePath.substring(0, indexOfSlash + 1);
    }
}
