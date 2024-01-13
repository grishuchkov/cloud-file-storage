package ru.grishuchkov.cloudfilestorage.service;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.grishuchkov.cloudfilestorage.dto.metadata.FileMetadata;
import ru.grishuchkov.cloudfilestorage.repository.FileRepository;
import ru.grishuchkov.cloudfilestorage.util.mapper.PathStringToFileMetadataMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileSearchService {

    private final FileRepository fileRepository;
    private final UserService userService;

    private final PathStringToFileMetadataMapper pathToFileMetadataMapper;

    private static final String ROOT_DIRECTORY_PATH = "";

    public List<FileMetadata> searchFiles(String username, String searchName) {
        String userBucket = userService.getUserBucketOrElseThrow(username);

        List<String> suitableAbsoluteFilePaths = getItems(userBucket).stream()
                .map(Item::objectName)
                .filter(objectName -> objectName.toLowerCase().contains(searchName.toLowerCase()))
                .toList();

        return pathToFileMetadataMapper.toMetadataWithoutOwner(suitableAbsoluteFilePaths);
    }

    private List<Item> getItems(String userBucket) {
        List<Item> objects = new ArrayList<>();
        try {
            objects = fileRepository
                    .getListObjects(ROOT_DIRECTORY_PATH, userBucket, true);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return objects;
    }

}
