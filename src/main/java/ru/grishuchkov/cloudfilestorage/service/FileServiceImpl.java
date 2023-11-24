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

    @Override
    @SneakyThrows
    public void save(UploadFiles files) {
        String bucketTemplate = "user-%d-files";

        String ownerUsername = files.getOwnerUsername();
        List<MultipartFile> uploadFiles = files.getFiles();

        User owner = userService.getUserByUsername(ownerUsername);
        String userBucket = String.format(bucketTemplate, owner.getId());

        boolean found = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(userBucket)
                .build()
        );

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(userBucket).build());
        } else {
            System.out.println("Bucket already exists.");
        }

        for (MultipartFile multipartFile : uploadFiles) {

            InputStream inputStream = multipartFile.getInputStream();
            String originalFilename = multipartFile.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(userBucket)
                            .object(originalFilename)
                            .stream(inputStream, inputStream.available(), -1).build()
            );
            inputStream.close();
        }
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
    public List<FileMetadata> getFilesMetadata(String path) {
        List<Item> itemsAtDirectory = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("asiatrip")
                        .prefix(path)
                        .build()
        );

        for (Result<Item> result : results) {
            itemsAtDirectory.add(result.get());
        }

        List<FileMetadata> fileMetadata = itemsMapper.toFileMetadata(itemsAtDirectory);

        return fileMetadata;
    }

}
