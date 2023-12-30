package ru.grishuchkov.cloudfilestorage.service;

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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String BUCKET_NAME_TEMPLATE = "user-%d-files";

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Override
    public void save(@NotNull UploadFiles files) {
        List<MultipartFile> uploadedFiles = files.getFiles();
        User owner = getOwnerByUsername(files.getOwnerUsername());
        String userBucket = getUserBucketName(owner);

        try {
            fileRepository.save(uploadedFiles, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull FileMetadata file) {
        User owner = getOwnerByUsername(file.getOwnerUsername());
        String userBucket = getUserBucketName(owner);

        String absolutePath = getAbsolutePath(file);

        try {
            fileRepository.delete(absolutePath, userBucket);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void rename(@NotNull FileMetadataForRename fileMetadata) {
        String newFilenameWithExtension = fileMetadata.getNewFilenameWithExtension();

        User owner = getOwnerByUsername(fileMetadata.getOwnerUsername());
        String userBucket = getUserBucketName(owner);

        String oldPath = getAbsolutePath(fileMetadata);
        String newPath = fileMetadata.getFilePath().getPathString() + newFilenameWithExtension;

        try {
            fileRepository.copy(oldPath, newPath, userBucket);
            fileRepository.delete(oldPath, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public byte[] downloadFile(@NotNull FileMetadata fileMetadata) {
        User owner = getOwnerByUsername(fileMetadata.getOwnerUsername());

        String absolutePath = getAbsolutePath(fileMetadata);
        String userBucket = getUserBucketName(owner);

        try {
            return fileRepository.get(absolutePath, userBucket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FilesContainer getFilesInfoOfUser(String path, String ownerUsername) {
        User owner = getOwnerByUsername(ownerUsername);
        String userBucket = getUserBucketName(owner);

        List<FileInfo> filesFromBucket = new ArrayList<>();

        try {
            filesFromBucket = fileRepository.getFilesInfo(path, userBucket);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return FilesContainer.builder()
                .files(filesFromBucket)
                .path(new FilePath(path))
                .build();
    }


    private User getOwnerByUsername(String username) {
        return userRepository.findUserByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getUserBucketName(User user) {
        Long userId = user.getId();
        return String.format(BUCKET_NAME_TEMPLATE, userId);
    }

    private String getAbsolutePath(FileMetadata fileMetadata) {
        String path = fileMetadata.getFilePath().getPathString();
        String filenameWithExtension = fileMetadata.getFileInfo().getFilenameWithExtension();

        return path + filenameWithExtension;
    }
}
