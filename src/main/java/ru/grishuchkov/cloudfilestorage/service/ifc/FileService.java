package ru.grishuchkov.cloudfilestorage.service.ifc;

import ru.grishuchkov.cloudfilestorage.dto.FileMetadata;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;

import java.util.List;

public interface FileService {

     void save(UploadFiles uploadFiles);

     byte[] get(String filename);

     List<FileMetadata> getUserFilesMetadata(String path, String username);
}
