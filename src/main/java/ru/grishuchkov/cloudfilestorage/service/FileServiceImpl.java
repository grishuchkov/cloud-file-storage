package ru.grishuchkov.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.grishuchkov.cloudfilestorage.dto.*;
import ru.grishuchkov.cloudfilestorage.entity.User;
import ru.grishuchkov.cloudfilestorage.repository.FileRepository;
import ru.grishuchkov.cloudfilestorage.repository.UserRepository;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;
import ru.grishuchkov.cloudfilestorage.util.mapper.ItemsToFileInfoMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final String BUCKET_NAME_TEMPLATE = "user-%d-files";

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private final FileUtils fileUtils;
    private final ItemsToFileInfoMapper itemsToFileInfoMapper;

    @Override
    public void save(@NotNull UploadFiles files) {
        List<MultipartFile> uploadedFiles = files.getFiles();
        String userBucket = getUserBucketByUsername(files.getOwnerUsername());

        for (MultipartFile file : uploadedFiles) {
            validateFilename(file.getOriginalFilename());
        }

        try {
            fileRepository.save(uploadedFiles, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateFilename(String filename) {
        if (!fileUtils.isValidPath(filename)) {
            throw new RuntimeException("File has bad symbols");
        }
    }

    @Override
    public void delete(@NotNull FileMetadata fileMetadata) {
        String userBucket = getUserBucketByUsername(fileMetadata.getOwnerUsername());
        String absolutePath = getAbsolutePath(fileMetadata);

        if (isFolder(fileMetadata)) {
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
        String userBucket = getUserBucketByUsername(fileMetadata.getOwnerUsername());

        String newFilenameWithExtension = fileMetadata.getNewFilenameWithExtension();
        String oldAbsolutePath = getAbsolutePath(fileMetadata);
        String newAbsolutePath = fileMetadata.getFilePath().getPathString() + newFilenameWithExtension;

        if (isFolder(fileMetadata)) {
            if (fileUtils.hasExtension(newFilenameWithExtension)) {
                throw new RuntimeException("Folder can't have extension");
            }

            List<String> objectsPaths = getPathsToAllFilesInDirectory(oldAbsolutePath, userBucket);

            for (String absoluteObjectPath : objectsPaths) {
                String[] splitPath = absoluteObjectPath.split(oldAbsolutePath);
                String usefulPartOfOldPath = splitPath[splitPath.length - 1];
                String newAbsoluteObjectPath = newAbsolutePath + usefulPartOfOldPath;

                doRename(absoluteObjectPath, newAbsoluteObjectPath, userBucket);
            }
            return;
        }

        doRename(oldAbsolutePath, newAbsolutePath, userBucket);
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
        String userBucket = getUserBucketByUsername(fileMetadata.getOwnerUsername());
        String absolutePath = getAbsolutePath(fileMetadata);

        try {
            return fileRepository.get(absolutePath, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FilesContainer getFilesInfoOfUser(String path, String ownerUsername) {
        String userBucket = getUserBucketByUsername(ownerUsername);

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


    private String getUserBucketByUsername(String username) {
        User user = userRepository.findUserByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return getUserBucketName(user);
    }

    private String getUserBucketName(User user) {
        Long userId = user.getId();
        return String.format(BUCKET_NAME_TEMPLATE, userId);
    }

    private String getAbsolutePath(FileMetadata fileMetadata) {
        String path = fileMetadata.getFilePath().getPathString();
        String fileWitServiceExtension = fileMetadata.getFileInfo().getFilenameWithExtension();
        String filename = fileUtils.getFilenameWithoutServiceExtension(fileWitServiceExtension);

        return path + filename;
    }

    private boolean isFolder(FileMetadata fileMetadata) {
        return "folder".equals(fileMetadata.getFileInfo().getExtension());
    }
}
