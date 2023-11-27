package ru.grishuchkov.cloudfilestorage.service.ifc;

import ru.grishuchkov.cloudfilestorage.dto.FilesContainer;
import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;

public interface FileService {

     void save(UploadFiles uploadFiles);

     byte[] get(String filename, String username);

     FilesContainer getUserFiles(String path, String username);
}
