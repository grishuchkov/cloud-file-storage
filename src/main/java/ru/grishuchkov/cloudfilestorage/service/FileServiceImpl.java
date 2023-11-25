package ru.grishuchkov.cloudfilestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;
import ru.grishuchkov.cloudfilestorage.entity.User;
import ru.grishuchkov.cloudfilestorage.service.ifc.FileService;
import ru.grishuchkov.cloudfilestorage.util.mapper.ItemsToFileMetadataMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final UserService userService;
    private final ItemsToFileMetadataMapper itemsMapper;
    private static final String BUCKET_NAME_TEMPLATE = "user-%d-files";

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
        uploadMultipartFiles(uploadFiles, userBucket);
    }

    @Override
    @SneakyThrows
    public byte[] get(String filename) {

        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("asiatrip")
                        .object(filename)
                        .build());


        return IOUtils.toByteArray(object);
    }

    @Override
    @SneakyThrows
    public List<FileMetadata> getUserFilesMetadata(String path, String username) {
        List<Item> itemsAtDirectory = new ArrayList<>();

        User owner = getOwnerByUsername(username);
        String userBucket = getUserBucketName(owner);

        if (!isBucketExists(userBucket)) {
            makeBucket(userBucket);
        }

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(userBucket)
                        .prefix(path)
                        .build()
        );

        for (Result<Item> result : results) {
            itemsAtDirectory.add(result.get());
        }

        List<FileMetadata> fileMetadata = itemsMapper.toFileMetadata(itemsAtDirectory);

        return fileMetadata;
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
    private void uploadMultipartFiles(List<MultipartFile> files, String bucketName) {
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
