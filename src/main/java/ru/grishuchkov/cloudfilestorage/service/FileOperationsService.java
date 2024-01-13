package ru.grishuchkov.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.grishuchkov.cloudfilestorage.dto.*;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileInfo;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadataForRename;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FilePath;
import ru.grishuchkov.cloudfilestorage.repository.FileRepository;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;
import ru.grishuchkov.cloudfilestorage.util.mapper.ItemsToFileInfoMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public final class FileOperationsService implements FileService {

    private final UserService userService;
    private final FileRepository fileRepository;

    private final FileUtils fileUtils;

    private final ItemsToFileInfoMapper itemsToFileInfoMapper;

    @Override
    public void save(@NotNull UploadFiles files) {
        List<MultipartFile> uploadedFiles = files.getFiles();
        String userBucket = getUserBucket(files.getOwnerUsername());

        doSave(uploadedFiles, userBucket);
    }

    private void doSave(List<MultipartFile> uploadedFiles, String userBucket) {
        try {
            fileRepository.save(uploadedFiles, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull FileMetadata fileMetadata) {
        String userBucket = getUserBucket(fileMetadata.getOwnerUsername());
        String absolutePath = getAbsolutePath(fileMetadata);

        if (fileUtils.isFolder(fileMetadata)) {
            List<String> paths = getPathsToAllFilesInDirectory(absolutePath, userBucket);

            for (String path : paths) {
                doDelete(path, userBucket);
            }
        } else {
            doDelete(absolutePath, userBucket);
        }
    }

    private void doDelete(String path, String userBucket) {
        try {
            fileRepository.delete(path, userBucket);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void rename(@NotNull FileMetadataForRename fileMetadata) {
        String userBucket = getUserBucket(fileMetadata.getOwnerUsername());

        String newFilenameWithExtension = fileMetadata.getNewFilenameWithExtension();
        String oldAbsolutePath = getAbsolutePath(fileMetadata);
        String newAbsolutePath = fileMetadata.getFilePath().getPathString() + newFilenameWithExtension;

        if (fileUtils.isFolder(fileMetadata)) {
            List<String> objectsPaths = getPathsToAllFilesInDirectory(oldAbsolutePath, userBucket);
            for (String absoluteObjectPath : objectsPaths) {
                String[] splitPath = absoluteObjectPath.split(oldAbsolutePath);
                String usefulPartOfOldPath = splitPath[splitPath.length - 1];
                String newAbsoluteObjectPath = newAbsolutePath + usefulPartOfOldPath;
                doRename(absoluteObjectPath, newAbsoluteObjectPath, userBucket);
            }
        } else {
            doRename(oldAbsolutePath, newAbsolutePath, userBucket);
        }
    }

    @NotNull
    private List<String> getPathsToAllFilesInDirectory(String directoryPath, String userBucket) {
        List<String> filesPaths = new ArrayList<>();
        try {
            filesPaths = fileRepository
                    .getListObjects(directoryPath, userBucket, true)
                    .stream()
                    .map(Item::objectName)
                    .toList();

        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return filesPaths;
    }

    private void doRename(String oldAbsolutePath, String newAbsolutePath, String userBucket) {
        try {
            fileRepository.copy(oldAbsolutePath, newAbsolutePath, userBucket);
            fileRepository.delete(oldAbsolutePath, userBucket);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @Override
    public byte[] downloadFile(@NotNull FileMetadata fileMetadata) {
        String userBucket = getUserBucket(fileMetadata.getOwnerUsername());
        String absolutePath = getAbsolutePath(fileMetadata);

        try {
            return fileRepository.get(absolutePath, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FilesContainer getFilesInfoOfUser(String path, String ownerUsername) {
        String userBucket = getUserBucket(ownerUsername);

        List<Item> itemsFromBucket = new ArrayList<>();
        try {
            itemsFromBucket = fileRepository.getListObjects(path, userBucket, false);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        List<FileInfo> files = itemsToFileInfoMapper.toFile(itemsFromBucket);
        return FilesContainer.builder()
                .files(files)
                .path(new FilePath(path))
                .build();
    }

    private String getUserBucket(String username) {
        return userService.getUserBucketOrElseThrow(username);
    }

    private String getAbsolutePath(FileMetadata fileMetadata) {
        String path = fileMetadata.getFilePath().getPathString();
        String fileWitServiceExtension = fileMetadata.getFileInfo().getFilenameWithExtension();
        String filename = fileUtils.getFilenameWithoutServiceExtension(fileWitServiceExtension);

        return path + filename;
    }
}
