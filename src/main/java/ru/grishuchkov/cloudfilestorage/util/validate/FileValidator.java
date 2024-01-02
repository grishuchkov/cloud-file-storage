package ru.grishuchkov.cloudfilestorage.util.validate;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadataForRename;
import ru.grishuchkov.cloudfilestorage.repository.FileRepository;
import ru.grishuchkov.cloudfilestorage.service.UserService;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator {

    private final FileRepository fileRepository;
    private final FileUtils fileUtils;
    private final UserService userService;

    public <T extends FileMetadata> boolean validate(T fileMetadata){

        String userBucket = userService.getUserBucket(fileMetadata.getOwnerUsername());

        if(fileMetadata.getClass().equals(FileMetadataForRename.class)){
            FileMetadataForRename file = (FileMetadataForRename) fileMetadata;

            if(hasDuplicate(file, userBucket)){
                throw new RuntimeException("Такой файл уже есть");
            }
        }
        return true;
    }

    private boolean hasDuplicate(FileMetadataForRename file, String userBucket) {
        String path = file.getFilePath().getPathString();
        List<String> listOfNamesAtDirectory = getListFilenamesFromDirectory(path, userBucket);

        String filename = path + file.getNewFilenameWithExtension();

        if (fileUtils.isFolder(file)) {
            filename = filename + "/";
        }

        return listOfNamesAtDirectory.contains(filename);
    }

    private List<String> getListFilenamesFromDirectory(String path, String userBucket){
        List<String> result = new ArrayList<>();
        try {
            result = fileRepository
                    .getListObjects(path, userBucket, false)
                    .stream()
                    .map(Item::objectName)
                    .toList();
        } catch (Exception ex){
            log.error(ex.getMessage());
        }
        return result;
    }

}
