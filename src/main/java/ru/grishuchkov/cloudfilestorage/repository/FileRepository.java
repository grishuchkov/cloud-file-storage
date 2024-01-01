package ru.grishuchkov.cloudfilestorage.repository;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private final MinioClient minioClient;

    public void save(List<MultipartFile> files, String userBucket) throws Exception {
        makeBucketIfNotExists(userBucket);

        for (MultipartFile multipartFile : files) {
            InputStream inputStream = multipartFile.getInputStream();
            String absolutePath = multipartFile.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(userBucket)
                            .object(absolutePath)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );
            inputStream.close();
        }
    }

    public void delete(String absolutePath, String userBucket) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(userBucket)
                        .object(absolutePath)
                        .build());
    }

    public byte[] get(String absolutePath, String userBucket) throws Exception {
        makeBucketIfNotExists(userBucket);

        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(userBucket)
                        .object(absolutePath)
                        .build());

        return IOUtils.toByteArray(object);
    }

    public List<Item> getListObjects(String path, String userBucket, Boolean recursively) throws Exception {
        List<Item> itemsAtDirectory = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(userBucket)
                        .prefix(path)
                        .recursive(recursively)
                        .build()
        );

        for (Result<Item> result : results) {
            itemsAtDirectory.add(result.get());
        }

        return itemsAtDirectory;
    }

    public void copy(String oldAbsolutePath, String newAbsolutePath, String userBucket) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(userBucket)
                        .object(newAbsolutePath)
                        .source(
                                CopySource.builder()
                                        .bucket(userBucket)
                                        .object(oldAbsolutePath)
                                        .build())
                        .build());
    }

    @SneakyThrows
    private Boolean isBucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build()
        );
    }

    @SneakyThrows
    private void makeBucketIfNotExists(String bucketName) {
        if (!isBucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build()
            );
        }
    }
}
