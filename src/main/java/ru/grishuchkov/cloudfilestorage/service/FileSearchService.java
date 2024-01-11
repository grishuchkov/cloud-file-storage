package ru.grishuchkov.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.grishuchkov.cloudfilestorage.dto.FileInfo;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.FilePath;
import ru.grishuchkov.cloudfilestorage.repository.FileRepository;
import ru.grishuchkov.cloudfilestorage.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileSearchService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final FileUtils fileUtils;


    private static final String ROOT = "";

    public List<FileMetadata> searchFiles(String username, String searchName) {
        String userBucket = userService.getUserBucketOrElseThrow(username);

        List<String> fileItems = getItems(userBucket).stream()
                .map(Item::objectName)
                .filter(file -> file.contains(searchName))
                .toList();

        List<FileMetadata> fileMetadataList = new ArrayList<>();

        for (String file : fileItems) {
            FileInfo fileInfo = FileInfo.builder()
                    .filename(fileUtils.getFilename(file))
                    .extension(fileUtils.getExtension(file))
                    .build();

            FilePath filePath = new FilePath(fileUtils.getPathWithoutFilename(file));

            fileMetadataList.add(new FileMetadata(fileInfo, filePath));
        }

        return fileMetadataList;
    }

    private List<Item> getItems(String userBucket) {
        List<Item> objects = new ArrayList<>();
        try {
            objects = fileRepository.getListObjects(ROOT, userBucket, true);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return objects;
    }

}
