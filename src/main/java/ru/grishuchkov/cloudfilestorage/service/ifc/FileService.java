package ru.grishuchkov.cloudfilestorage.service.ifc;

import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.FilesContainer;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;

public interface FileService {

    void save(UploadFiles uploadFiles);

    boolean delete(FileMetadata fileMetadata);

    byte[] downloadFile(FileMetadata fileMetadata);

    FilesContainer getUserFiles(String pathToFile, String ownerUsername);
}
