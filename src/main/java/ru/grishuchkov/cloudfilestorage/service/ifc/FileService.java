package ru.grishuchkov.cloudfilestorage.service.ifc;

import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.FileMetadataForRename;
import ru.grishuchkov.cloudfilestorage.dto.FilesContainer;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;

public interface FileService {

    void save(UploadFiles uploadFiles);

    void delete(FileMetadata fileMetadata);

    void rename(FileMetadataForRename fileMetadata);

    byte[] downloadFile(FileMetadata fileMetadata);

    FilesContainer getFilesInfoOfUser(String pathToFile, String ownerUsername);
}
