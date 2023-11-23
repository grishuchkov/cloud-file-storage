package ru.grishuchkov.cloudfilestorage.service.ifc;

import ru.grishuchkov.cloudfilestorage.dto.UploadFiles;

public interface FileService {

     void save(UploadFiles uploadFiles);

     byte[] get(String filename);

     void scan(String path);
}
