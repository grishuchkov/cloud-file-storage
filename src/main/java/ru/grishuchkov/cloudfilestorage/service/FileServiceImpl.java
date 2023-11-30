package ru.grishuchkov.cloudfilestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.grishuchkov.cloudfilestorage.dto.*;
import ru.grishuchkov.cloudfilestorage.entity.User;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;
import ru.grishuchkov.cloudfilestorage.util.mapper.ItemsToFileInfoMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String BUCKET_NAME_TEMPLATE = "user-%d-files";
    private final MinioClient minioClient;
    private final UserService userService;
    private final ItemsToFileInfoMapper itemsMapper;

    @Override
    @SneakyThrows
    public void save(UploadFiles files) {
        List<MultipartFile> uploadFiles = files.getFiles();
        String ownerUsername = files.getOwnerUsername();

        User owner = getOwnerByUsername(ownerUsername);
        String userBucket = getUserBucketName(owner);

        if (!isBucketExists(userBucket)) {
            makeBucket(userBucket);
        }
        uploadMultipartFilesToBucket(uploadFiles, userBucket);
    }

    @Override
    public boolean delete(FileMetadata file) {
        String ownerUsername = file.getOwnerUsername();
        User owner = getOwnerByUsername(ownerUsername);
        String userBucket = getUserBucketName(owner);

        String path = file.getFilePath().getPathString();
        String filename = file.getFileInfo().getFilename();
        String extension = file.getFileInfo().getExtension();

        String finalUrl = path + filename + "." + extension;

        if (!isBucketExists(userBucket)) {
            makeBucket(userBucket);
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(userBucket)
                            .object(finalUrl)
                            .build());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    @SneakyThrows
    public byte[] downloadFile(String filename, String ownerUsername) {

        User owner = getOwnerByUsername(ownerUsername);
        String userBucket = getUserBucketName(owner);

        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(userBucket)
                        .object(filename)
                        .build());


        return IOUtils.toByteArray(object);
    }

    @Override
    @SneakyThrows
    public FilesContainer getUserFiles(String pathToFile, String ownerUsername) {

        User owner = getOwnerByUsername(ownerUsername);
        String userBucket = getUserBucketName(owner);

        if (!isBucketExists(userBucket)) {
            makeBucket(userBucket);
        }

        List<FileInfo> filesFromBucket = getFilesFromBucket(userBucket, pathToFile);

        return FilesContainer.builder()
                .files(filesFromBucket)
                .path(new FilePath(pathToFile))
                .build();
    }


    private User getOwnerByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    private String getUserBucketName(User user) {
        Long userId = user.getId();
        return String.format(BUCKET_NAME_TEMPLATE, userId);
    }

    @SneakyThrows
    private Boolean isBucketExists(String bucketName) {
        boolean found = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build()
        );
        return found;
    }

    @SneakyThrows
    private void makeBucket(String bucketName) {
        minioClient.makeBucket(MakeBucketArgs
                .builder()
                .bucket(bucketName)
                .build()
        );
    }

    @SneakyThrows
    private List<FileInfo> getFilesFromBucket(String userBucket, String path) {
        List<Item> itemsAtDirectory = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(userBucket)
                        .prefix(path)
                        .build()
        );

        for (Result<Item> result : results) {
            itemsAtDirectory.add(result.get());
        }

        return itemsMapper.toFile(itemsAtDirectory);
    }

    @SneakyThrows
    private void uploadMultipartFilesToBucket(List<MultipartFile> files, String bucketName) {
        for (MultipartFile multipartFile : files) {
            InputStream inputStream = multipartFile.getInputStream();
            String originalFilename = multipartFile.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(originalFilename)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
            inputStream.close();
        }
    }
}
