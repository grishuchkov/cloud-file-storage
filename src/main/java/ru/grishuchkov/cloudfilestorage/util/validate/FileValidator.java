package ru.grishuchkov.cloudfilestorage.util.validate;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadataForRename;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.exception.FileValidateException;
import ru.grishuchkov.cloudfilestorage.repository.MinIORepository;
import ru.grishuchkov.cloudfilestorage.service.UserService;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator {

    private final MinIORepository minIORepository;
    private final FileUtils fileUtils;
    private final UserService userService;

    public <T extends FileMetadata> void validate(T fileMetadata) {

        String userBucket = userService.getUserBucketOrElseThrow(fileMetadata.getOwnerUsername());

        if (isFileMetadataForRename(fileMetadata)) {
            FileMetadataForRename file = (FileMetadataForRename) fileMetadata;
            String newFilenameWithExtension = file.getNewFilenameWithExtension();

            if (!isValidFilename(newFilenameWithExtension)) {
                throw new FileValidateException("File has bad symbols: " + newFilenameWithExtension);
            }

            if (isFileAlreadyExist(file, userBucket)) {
                throw new FileValidateException("This file already exist");
            }

            if (fileUtils.isFolder(file)) {
                if (fileUtils.hasExtension(newFilenameWithExtension)) {
                    throw new FileValidateException("Folder can't have extension");
                }
            }
        }
    }


    public void validate(UploadFiles uploadFiles) {
        List<String> filenames = uploadFiles.getFiles()
                .stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();

        for (String filename : filenames) {
            if(!isValidFilename(filename)){
                throw new FileValidateException("Uploaded file has symbols: " + filename);
            }
        }
    }

    private <T extends FileMetadata> boolean isFileMetadataForRename(T fileMetadata) {
        return fileMetadata.getClass().equals(FileMetadataForRename.class);
    }


    private boolean isFileAlreadyExist(FileMetadataForRename file, String userBucket) {
        String path = file.getFilePath().getPathString();
        List<String> listOfNamesAtDirectory = getListFilenamesFromDirectory(path, userBucket);

        String filename = path + file.getNewFilenameWithExtension();

        if (fileUtils.isFolder(file)) {
            filename = filename + "/";
        }

        return listOfNamesAtDirectory.contains(filename);
    }

    private List<String> getListFilenamesFromDirectory(String path, String userBucket) {
        List<String> result = new ArrayList<>();
        try {
            result = minIORepository
                    .getListObjects(path, userBucket, false)
                    .stream()
                    .map(Item::objectName)
                    .toList();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    private boolean isValidFilename(String newFilenameWithExtension) {
        return fileUtils.isValidPathAndFilename(newFilenameWithExtension);
    }

}
